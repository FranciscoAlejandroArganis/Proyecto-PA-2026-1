/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.util.Counter;
import com.pa.io.FileUtils;
import com.pa.iter.CartesianProductIterator;
import com.pa.query.SelectFrom;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.stats.SLRResult;
import com.pa.table.Cell;
import com.pa.table.Header;
import com.pa.table.Row;
import com.pa.time.SegmentedPeriod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Representa un programa que procesa los datos
 *
 * @author francisco-alejandro
 */
public abstract class Program {

    /**
     * Registro con la información de los archivos a utilizar
     */
    protected FilesInfo filesInfo;

    /**
     * Cantidad de fragmentos
     */
    protected int numFrags;

    /**
     * Consulta proporcionada por el usuario para filtrar
     */
    protected SelectFromWhere userQuery;

    /**
     * Registro con la información de los parámetros del análisis
     */
    protected AnalysisInfo analysisInfo;

    /**
     * Arreglo de contadores de valores únicos por cada columna de interés
     */
    protected Counter<Cell>[] columnCounters;

    /**
     * Mapa de cada grupo formado a su periodo de tiempo segmentado
     */
    protected Map<Row, SegmentedPeriod> periodMap;

    /**
     * Mapa de cada grupo formado a su serie de tiempo
     */
    protected Map<Row, Long[]> seriesMap;

    /**
     * Consulta usada para seleccionar filas que pertenecen a uno de los grupos
     * formados
     */
    protected SelectFromWhere groupQuery;

    /**
     * Construye un nuevo programa
     *
     * @param filesInfo contiene la información de los archivos a utilizar
     * @param numFrags es la cantidad deseada de fragmentos a generar
     * @param analysisInfo contiene la información de los parámetros usados para
     * el análisis de los datos
     */
    public Program(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        this.filesInfo = filesInfo;
        this.numFrags = numFrags;
        this.analysisInfo = analysisInfo;
        columnCounters = new Counter[analysisInfo.getGroupColumns().size()];
        for (int i = 0; i < columnCounters.length; i++) {
            columnCounters[i] = new Counter<>();
        }
    }

    /**
     * Inicia la ejecución del programa
     */
    public void execute() {
        long totalStart = System.nanoTime();
        init();
        long procStart = System.nanoTime();
        frags();
        long procEnd = System.nanoTime();
        results();
        long totalEnd = System.nanoTime();
        System.out.println("Tiempo total de la ejecución: " + Duration.ofNanos(totalEnd - totalStart));
        System.out.println("Tiempo de procesamiento de los fragmentos:" + Duration.ofNanos(procEnd - procStart));
    }

    /**
     * Lee la consulta y genera los fragmentos
     */
    protected void init() {
        QueryReader qr = new QueryReader(analysisInfo.getDataHeader());
        userQuery = qr.readQuery();
        if (filesInfo.getTempDir().mkdir()) {
            try {
                long totalLines = FileUtils.countLines(filesInfo.getDataset());
                long fragLines = 1 + (totalLines - 1) / numFrags;
                numFrags = FileUtils.splitLines(filesInfo.getDataset(), filesInfo.getTempDir(), totalLines, fragLines, filesInfo.getDataset().getName());
            } catch (IOException e) {
                Reviews.LOGGER.severe("No se puede escribir los archivos en el directorio temporal: " + filesInfo.getTempDir());
            }
        } else {
            Reviews.LOGGER.severe("No se puede crear el directorio temporal: " + filesInfo.getTempDir());
        }
    }

    /**
     * Escribe los resultados finales
     */
    protected void results() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filesInfo.getResults()))) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Row, SegmentedPeriod> entry : periodMap.entrySet()) {
                Row rep = entry.getKey();
                SegmentedPeriod period = entry.getValue();
                Long[] timeSeries = seriesMap.get(rep);
                Accumulator acc = new Accumulator(2);
                sb.append(rep);
                sb.append(" from ");
                sb.append(period.getStart());
                sb.append(" to ");
                sb.append(period.getEnd());
                sb.append('\n');
                sb.append(Arrays.toString(timeSeries));
                sb.append('\n');
                long sum = 0;
                for (int i = 0; i < timeSeries.length; i++) {
                    sum += timeSeries[i];
                    acc.add(new double[]{i, sum});
                }
                SLRResult slr = acc.simpleLinearRegression(0, 1);
                sb.append("m = ");
                sb.append(slr.getSlope());
                sb.append(", b = ");
                sb.append(slr.getIntercept());
                sb.append(", r² = ");
                sb.append(slr.getCoefficientOfDetermination());
                sb.append("\n\n");
            }
            writer.write(sb.toString());
            writer.newLine();
            FileUtils.mergeLines(filesInfo.getFiltered(), filesInfo.getTempDir(), filesInfo.getFiltered().getName());
            FileUtils.recursiveDelete(filesInfo.getTempDir());
        } catch (IOException e) {
            Reviews.LOGGER.severe("No se puede escribir en el archivo de resultados: " + filesInfo.getResults().getName());
        }
    }

    /**
     * Construye los grupos a partir de los valores más frecuentes de cada
     * columna de interés
     */
    protected void buildGroups() {
        Cell[][] columnValues = new Cell[analysisInfo.getGroupColumns().size()][];
        for (int i = 0; i < columnCounters.length; i++) {
            Counter<Cell> counter = columnCounters[i];
            Set<Cell> uniquesInColumn = counter.getMap().keySet();
            PriorityQueue<Cell> maxQueue = new PriorityQueue<>(
                    Math.max (1, uniquesInColumn.size()),
                    (a, b) -> counter.getCount(a) <= counter.getCount(b) ? 1 : -1
            );
            maxQueue.addAll(uniquesInColumn);
            Cell[] mostFrequentValues = new Cell[Math.min(analysisInfo.getMaxUniques(), uniquesInColumn.size())];
            for (int j = 0; j < mostFrequentValues.length; j++) {
                mostFrequentValues[j] = maxQueue.poll();
            }
            columnValues[i] = mostFrequentValues;
        }
        columnCounters = null;
        periodMap = new HashMap<>();
        seriesMap = new HashMap<>();
        CartesianProductIterator<Cell> iter = new CartesianProductIterator<>(columnValues);
        while (iter.hasNext()) {
            Row rep = new Row(analysisInfo.getGroupColumns(), iter.next());
            periodMap.put(rep, new SegmentedPeriod(analysisInfo.getNumSegments()));
            Long[] timeSeries = new Long[analysisInfo.getNumSegments()];
            Arrays.fill(timeSeries, 0l);
            seriesMap.put(rep, timeSeries);
        }
        SelectFrom selRep = new SelectFrom(analysisInfo.getGroupColumns(), analysisInfo.getDataHeader());
        groupQuery = new SelectFromWhere(
                selRep,
                row -> periodMap.containsKey(selRep.apply(row))
        );
    }

    /**
     * Regresa el número de fragmentos
     *
     * @return el número de fragmentos
     */
    public int getNumFrags() {
        return numFrags;
    }

    /**
     * Asigna el número de fragmentos
     *
     * @param numFrags el número de fragmentos
     */
    public void setNumFrags(int numFrags) {
        this.numFrags = numFrags;
    }

    /**
     * Regresa la consulta del usuario
     *
     * @return la consulta del usuario
     */
    public SelectFromWhere getUserQuery() {
        return userQuery;
    }

    /**
     * Regresa la información de los archivos
     *
     * @return la información de los archivos
     */
    public FilesInfo getFilesInfo() {
        return filesInfo;
    }

    /**
     * Regresa la información de los parámetros del análisis
     *
     * @return la información de los parámetros del análisis
     */
    public AnalysisInfo getAnalysisInfo() {
        return analysisInfo;
    }

    /**
     * Regresa el arreglo de contadores de valores únicos por columna
     *
     * @return el arreglo de contadores de valores únicos por columna
     */
    public Counter<Cell>[] getColumnCounters() {
        return columnCounters;
    }

    /**
     * Regresa el mapa de grupos a periodos segmentados
     *
     * @return el mapa de grupos a periodos segmentados
     */
    public Map<Row, SegmentedPeriod> getPeriodMap() {
        return periodMap;
    }

    /**
     * Regresa el mapa de grupos a series de tiempo
     *
     * @return el mapa de grupos a series de tiempo
     */
    public Map<Row, Long[]> getSeriesMap() {
        return seriesMap;
    }

    /**
     * Regresa la consulta que únicamente selecciona las filas que pertencen a
     * alguno de los grupos formados
     *
     * @return la consulta que únicamente selecciona las filas que pertencen a
     * alguno de los grupos formados
     */
    public SelectFromWhere getGroupQuery() {
        return groupQuery;
    }

    /**
     * Procesa los fragmentos
     */
    protected abstract void frags();

    /**
     * Representa toda la información sobre archivos que requiere el programa
     */
    public static class FilesInfo {

        /**
         * Archivo con el conjunto de datos
         */
        private File dataset;

        /**
         * Carpeta temporal donde se guardan los fragmentos
         */
        private File tempDir;

        /**
         * Archivo filtrado con los resutlados de la consulta del usuario
         */
        private File filtered;

        /**
         * Archivo donde se escriben los resultado del análisis
         */
        private File results;

        /**
         * Construye un nuevo registro de información de archivos
         *
         * @param dataset es la ruta al archivo con el conjnuto de datos
         * @param tempDir es la ruta a la carpeta temporal
         * @param filtered es la ruta al archivo filtrado resultado de la
         * consulta del usuario
         * @param results es la ruta al archivo con los resultados obtenidos del
         * análisis
         */
        public FilesInfo(String dataset, String tempDir, String filtered, String results) {
            this.dataset = new File(dataset);
            this.tempDir = new File(tempDir);
            this.filtered = new File(filtered);
            this.results = new File(results);
        }

        /**
         * Regresa el archico con el conjunto de datos
         *
         * @return el archivo con el conjunto de datos
         */
        public File getDataset() {
            return dataset;
        }

        /**
         * Regresa la carpeta temporal
         *
         * @return la carpeta temporal
         */
        public File getTempDir() {
            return tempDir;
        }

        /**
         * Regresa el archivo filtrado
         *
         * @return el archivo filtrado
         */
        public File getFiltered() {
            return filtered;
        }

        /**
         * Regresa el archivo con los resultados del análisis
         *
         * @return el archivo con los resultados del análisis
         */
        public File getResults() {
            return results;
        }
        
    }

    /**
     * Representa toda la información de los parámetros del análisis que
     * requiere el programa
     */
    public static class AnalysisInfo {

        /**
         * Cabecera del conjunto de datos
         */
        private Header dataHeader;

        /**
         * Cabecera con las columnas de interés para formar grupos
         */
        private Header groupColumns;

        /**
         * Cantidad máxima de valores únicos a tomar por columna para formar
         * grupos
         */
        private int maxUniques;

        /**
         * Índice de la columna de donde se toma el tiempo para contar por
         * segmentos de tiempo
         */
        private int timeColIndex;

        /**
         * Índice de la columna que se usa como criterio para contar por
         * segmentos de tiempo
         */
        private int boolColIndex;

        /**
         * Número de segmentos de tiempo
         */
        private int numSegments;

        /**
         * Construye un nuevo registro de información del análisis
         *
         * @param dataHeader es la cabecera del conjnuto de datos
         * @param groupColumns una cabecera con las columnas de interés para
         * formar grupos
         * @param maxUniques es la máxima cantidad de valores únicos que se
         * toman por columna para formar grupos
         * @param timeColIndex es el índice de la columna de donde se toma el
         * tiempo para contar por segmentos de tiempo
         * @param boolColIndex es el índice de la columna con el valor booleano
         * que se toma como criterio para contar por segmentos de tiempo
         * @param numSegments es el número de segmentos en los que se divide el
         * periodo de tiempo
         */
        public AnalysisInfo(Header dataHeader, Header groupColumns, int maxUniques, int timeColIndex, int boolColIndex, int numSegments) {
            this.dataHeader = dataHeader;
            this.groupColumns = groupColumns;
            this.maxUniques = maxUniques;
            this.timeColIndex = timeColIndex;
            this.boolColIndex = boolColIndex;
            this.numSegments = numSegments;
        }

        /**
         * Regresa la cabecera del conjunto de datos
         *
         * @return la cabecera del conjunto de datos
         */
        public Header getDataHeader() {
            return dataHeader;
        }

        /**
         * Regresa las columnas de interés para formar grupos
         *
         * @return las columnas de interés para formar grupos
         */
        public Header getGroupColumns() {
            return groupColumns;
        }

        /**
         * Regresa la cantidad máxima de valores únicos a tomar por columna para
         * formar grupos
         *
         * @return la cantidad máxima de valores únicos a tomar por columna para
         * formar grupos
         */
        public int getMaxUniques() {
            return maxUniques;
        }

        /**
         * Regresa el índice de la columna de donde se toma el tiempo para
         * contar por segmentos de tiempo
         *
         * @return el índice de la columna de donde se toma el tiempo para
         * contar por segmentos de tiempo
         */
        public int getTimeColIndex() {
            return timeColIndex;
        }

        /**
         * Regresa el índice de la columna que se usa como criterio para contar
         * por segmentos de tiempo
         *
         * @return el índice de la columna que se usa como criterio para contar
         * por segmentos de tiempo
         */
        public int getBoolColIndex() {
            return boolColIndex;
        }

        /**
         * Regresa el número de segmentos de tiempo
         *
         * @return el número de segmentos de tiempo
         */
        public int getNumSegments() {
            return numSegments;
        }
        
    }
    
}
