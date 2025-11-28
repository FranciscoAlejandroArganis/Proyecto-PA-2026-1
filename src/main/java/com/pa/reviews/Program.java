/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

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
 *
 * @author francisco-alejandro
 */
public abstract class Program {

    protected FilesInfo filesInfo;
    protected int numFrags;
    protected SelectFromWhere query;
    protected AnalysisInfo analysisInfo;
    protected Counter<Cell>[] uniqueCounters;
    protected Set<Row> groupReps;
    protected Counter<Pair<Row, Integer>> boolCounter;
    protected LocalDateTime minTime;
    protected LocalDateTime maxTime;

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
        query = qr.readQuery();
        if (filesInfo.getTempDir().mkdir()) {
            try {
                long totalLines = FileUtils.countLines(filesInfo.getDataset());
                long fragLines = 1 + (totalLines - 1) / numFrags;
                numFrags = FileUtils.splitLines(filesInfo.getDataset(), filesInfo.getTempDir(), totalLines, fragLines, filesInfo.getDataset().getName());
            } catch (IOException e) {
                Reviews.LOGGER.severe("No se puede escribir los archivos en el directorio temporal");
            }
        } else {
            Reviews.LOGGER.severe("No se puede crear el directorio temporal");
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
        }
    }

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

    public int getNumFrags() {
        return numFrags;
    }

    public void setNumFrags(int numFrags) {
        this.numFrags = numFrags;
    }

    public SelectFromWhere getQuery() {
        return query;
    }

    public FilesInfo getFilesInfo() {
        return filesInfo;
    }

    public AnalysisInfo getAnalysisInfo() {
        return analysisInfo;
    }

    public Counter<Cell>[] getUniqueCounters() {
        return uniqueCounters;
    }

    public Counter<Pair<Row, Integer>> getBoolCounter() {
        return boolCounter;
    }

    public Set<Row> getGroupReps() {
        return groupReps;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public void setMinTime(LocalDateTime minTime) {
        this.minTime = minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(LocalDateTime maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Procesa los fragmentos
     */
    protected abstract void frags();

    public static class FilesInfo {

        private File dataset;
        private File tempDir;
        private File filtered;
        private File results;

        public FilesInfo(String dataset, String tempDir, String filtered, String results) {
            this.dataset = new File(dataset);
            this.tempDir = new File(tempDir);
            this.filtered = new File(filtered);
            this.results = new File(results);
        }

        public File getDataset() {
            return dataset;
        }

        public File getTempDir() {
            return tempDir;
        }

        public File getFiltered() {
            return filtered;
        }

        public File getResults() {
            return results;
        }

    }

    public static class AnalysisInfo {

        private Header dataHeader;
        private Header colsToTally;
        private int maxUniques;
        private int timeColIndex;
        private int boolColIndex;
        private int numSegments;

        public AnalysisInfo(Header dataHeader, Header colsToTally, int maxUniques, int timeColIndex, int boolColIndex, int numSegments) {
            this.dataHeader = dataHeader;
            this.colsToTally = colsToTally;
            this.maxUniques = maxUniques;
            this.timeColIndex = timeColIndex;
            this.boolColIndex = boolColIndex;
            this.numSegments = numSegments;
        }

        public Header getDataHeader() {
            return dataHeader;
        }

        public Header getColsToTally() {
            return colsToTally;
        }

        public int getMaxUniques() {
            return maxUniques;
        }

        public int getTimeColIndex() {
            return timeColIndex;
        }

        public int getBoolColIndex() {
            return boolColIndex;
        }

        public int getNumSegments() {
            return numSegments;
        }

        public SelectFromWhere selGroupCols() {
            return new SelectFromWhere(colsToTally, dataHeader, row -> row.getCell(boolColIndex).getBool());
        }

    }

}
