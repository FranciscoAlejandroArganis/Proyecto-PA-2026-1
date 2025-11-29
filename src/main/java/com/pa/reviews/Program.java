/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.util.Pair;
import com.pa.util.Counter;
import com.pa.io.FileUtils;
import com.pa.iter.CartesianProductIterator;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.stats.SLRResult;
import com.pa.table.Cell;
import com.pa.table.Header;
import com.pa.table.Row;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa un programa que procesa los datos
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
     * La consulta proporcionada por el usuario para filtrar
     */
    protected SelectFromWhere userQuery;
    
    /**
     * Registro con la información de los parámetros del análisis
     */
    protected AnalysisInfo analysisInfo;
    
    /**
     * Arreglo de contadores de valores únicos por cada columna de interés
     */
    protected Counter<Cell>[] uniqueCounters;
    
    /**
     * Conjunto de combinaciones de valores de cada grupo
     */
    protected Set<Row> groupReps;
    
    /**
     * Contador de filas verdaderas por cada grupo y segmento
     */
    protected Counter<Pair<Row, Integer>> boolCounter;
    
    /**
     * Mínimo tiempo encontrado hasta el momento
     */
    protected LocalDateTime minTime;
    
    /**
     * Máximo tiempo encontrado hasta el momento
     */
    protected LocalDateTime maxTime;

    /**
     * Construye un nuevo programa
     * @param filesInfo contiene la información de los archivos a utilizar
     * @param numFrags es la cantidad deseada de fragmentos a generar
     * @param analysisInfo contiene la información de los parámetros usados para el análisis de los datos
     */
    public Program(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        this.filesInfo = filesInfo;
        this.numFrags = numFrags;
        this.analysisInfo = analysisInfo;
        uniqueCounters = new Counter[analysisInfo.getColsToTally().size()];
        for (int i = 0; i < uniqueCounters.length; i++) {
            uniqueCounters[i] = new Counter<>();
        }
        boolCounter = new Counter<>();
    }

    /**
     * Inicia la ejecución del programa
     */
    public void execute() {
        init();
        frags();
        results();
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
            sb.append(minTime);
            sb.append(" : ");
            sb.append(maxTime);
            sb.append("\n\n");
            for (Row rep : groupReps) {
                Accumulator acc = new Accumulator(2);
                sb.append(rep);
                sb.append("\n[");
                for (int i = 0; i < analysisInfo.getNumSegments(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    Pair<Row, Integer> pair = new Pair<>(rep, i);
                    long count = boolCounter.getCount(pair);
                    sb.append(count);
                    acc.add(new double[]{i, count});
                }
                sb.append("]\n");
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
     * Construye el conjunto de grupos a partir de los valores más frecuentes
     */
    protected void buildGroups() {
        Cell[][] uniqueValuesPerColumn = new Cell[analysisInfo.getColsToTally().size()][];
        for (int i = 0; i < uniqueCounters.length; i++) {
            Counter<Cell> counter = uniqueCounters[i];
            Cell[] uniques = counter.getMap().keySet().toArray(new Cell[0]);
            Arrays.sort(uniques, (a, b) -> {
                long countA = counter.getCount(a);
                long countB = counter.getCount(b);
                if (countA == countB) {
                    return a.compareTo(b);
                }
                return countA < countB ? -1 : 1;
            });
            uniqueValuesPerColumn[i] = Arrays.copyOf(uniques, Math.min(analysisInfo.getMaxUniques(), uniques.length));
        }
        uniqueCounters = null;
        groupReps = new HashSet<>();
        CartesianProductIterator<Cell> iter = new CartesianProductIterator<>(uniqueValuesPerColumn);
        while (iter.hasNext()) {
            Row rep = new Row(analysisInfo.getColsToTally(), iter.next());
            groupReps.add(rep);
        }
    }

    /**
     * Regresa el número de fragmentos
     * @return el número de fragmentos
     */
    public int getNumFrags() {
        return numFrags;
    }

    /**
     * Asigna el número de fragmentos
     * @param numFrags el número de fragmentos
     */
    public void setNumFrags(int numFrags) {
        this.numFrags = numFrags;
    }

    /**
     * Regresa la consulta del usuario
     * @return la consulta del usuario
     */
    public SelectFromWhere getUserQuery() {
        return userQuery;
    }

    /**
     * Regresa la información de los archivos
     * @return la información de los archivos
     */
    public FilesInfo getFilesInfo() {
        return filesInfo;
    }

    /**
     * Regresa la información de los parámetros del análisis
     * @return la información de los parámetros del análisis
     */
    public AnalysisInfo getAnalysisInfo() {
        return analysisInfo;
    }

    /**
     * Regresa el arreglo de contadores de valores únicos por columna
     * @return el arreglo de contadores de valores únicos por columna
     */
    public Counter<Cell>[] getUniqueCounters() {
        return uniqueCounters;
    }

    /**
     * Regresa el contador de las filas verdaderas por cada grupo
     * @return el contador de las filas verdaderas por cada grupo
     */
    public Counter<Pair<Row, Integer>> getBoolCounter() {
        return boolCounter;
    }

    /**
     * Regresa el conjunto de grupos
     * @return el conjunto de grupos
     */
    public Set<Row> getGroupReps() {
        return groupReps;
    }

    /**
     * Regresa el tiempo mínimo encontrado
     * @return el tiempo mínimo
     */
    public LocalDateTime getMinTime() {
        return minTime;
    }

    /**
     * Asigna el tiempo mínimo encontrado
     * @param minTime el nuevo tiempo mínimo
     */
    public void setMinTime(LocalDateTime minTime) {
        this.minTime = minTime;
    }
    
    /**
     * Regresa el tiempo máximo encontrado
     * @return el tiempo máximo
     */
    public LocalDateTime getMaxTime() {
        return maxTime;
    }
    
    /**
     * Asigna el tiempo máximo encontrado
     * @param maxTime el nuevo tiempo máximo
     */
    public void setMaxTime(LocalDateTime maxTime) {
        this.maxTime = maxTime;
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
         * @param dataset es la ruta al archivo con el conjnuto de datos
         * @param tempDir es la ruta a la carpeta temporal
         * @param filtered es la ruta al archivo filtrado resultado de la consulta del usuario
         * @param results es la ruta al archivo con los resultados obtenidos del análisis
         */
        public FilesInfo(String dataset, String tempDir, String filtered, String results) {
            this.dataset = new File(dataset);
            this.tempDir = new File(tempDir);
            this.filtered = new File(filtered);
            this.results = new File(results);
        }

        /**
         * Regresa el archico con el conjunto de datos
         * @return el archivo con el conjunto de datos
         */
        public File getDataset() {
            return dataset;
        }

        /**
         * Regresa la carpeta temporal
         * @return la carpeta temporal
         */
        public File getTempDir() {
            return tempDir;
        }

        /**
         * Regresa el archivo filtrado
         * @return el archivo filtrado
         */
        public File getFiltered() {
            return filtered;
        }

        /**
         * Regresa el archivo con los resultados del análisis
         * @return el archivo con los resultados del análisis
         */
        public File getResults() {
            return results;
        }

    }

    /**
     * Representa toda la información de los parámetros del análisis que requiere el programa
     */
    public static class AnalysisInfo {

        /**
         * Cabecera del conjunto de datos
         */
        private Header dataHeader;
        
        /**
         * Cabecera con las columnas de las que se cuentan valores únicos
         */
        private Header colsToTally;
        
        /**
         * Cantidad máxima de valores únicos a tomar por columna para formar grupos
         */
        private int maxUniques;
        
        /**
         * Índice de la columna de donde se toma el tiempo para contar por segmentos de tiempo
         */
        private int timeColIndex;
        
        /**
         * Índice de la columna que se usa como criterio para contar por segmentos de tiempo
         */
        private int boolColIndex;
        
        /**
         * Número de segmentos de tiempo
         */
        private int numSegments;

        /**
         * Construye un nuevo registro de información del análisis
         * @param dataHeader es la cabecera del conjnuto de datos
         * @param colsToTally es una cabecera con las columnas de las que se cuentan los valores únicos
         * @param maxUniques es la máxima cantidad de valores únicos que se toman por columna para formar grupos
         * @param timeColIndex es el índice de la columna de donde se toma el tiempo para contar por segmentos de tiempo
         * @param boolColIndex es el índice de la columna con el valor booleano que se toma como criterio para contar por segmentos de tiempo
         * @param numSegments es el número de segmentos en los que se divide el periodo de tiempo
         */
        public AnalysisInfo(Header dataHeader, Header colsToTally, int maxUniques, int timeColIndex, int boolColIndex, int numSegments) {
            this.dataHeader = dataHeader;
            this.colsToTally = colsToTally;
            this.maxUniques = maxUniques;
            this.timeColIndex = timeColIndex;
            this.boolColIndex = boolColIndex;
            this.numSegments = numSegments;
        }

        /**
         * Regresa la cabecera del conjunto de datos
         * @return la cabecera del conjunto de datos
         */
        public Header getDataHeader() {
            return dataHeader;
        }

        /**
         * Regresa las columnas de las que se cuentan valores únicos
         * @return las columnas de las que se cuentan valores únicos
         */
        public Header getColsToTally() {
            return colsToTally;
        }

        /**
         * Regresa la cantidad máxima de valores únicos a tomar por columna para formar grupos
         * @return la cantidad máxima de valores únicos a tomar por columna para formar grupos
         */
        public int getMaxUniques() {
            return maxUniques;
        }

        /**
         * Regresa el índice de la columna de donde se toma el tiempo para contar por segmentos de tiempo
         * @return el índice de la columna de donde se toma el tiempo para contar por segmentos de tiempo
         */
        public int getTimeColIndex() {
            return timeColIndex;
        }

        /**
         * Regresa el índice de la columna que se usa como criterio para contar por segmentos de tiempo
         * @return el índice de la columna que se usa como criterio para contar por segmentos de tiempo
         */
        public int getBoolColIndex() {
            return boolColIndex;
        }

        /**
         * Regresa el número de segmentos de tiempo
         * @return el número de segmentos de tiempo
         */
        public int getNumSegments() {
            return numSegments;
        }

    }

}
