/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.io.FileUtils;
import com.pa.multithread.AbstractManager;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.stats.SLRResult;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author francisco-alejandro
 */
public class Manager extends AbstractManager {

    private File inputFile;
    private File tempDir;
    private File queryOutputFile;
    private File statsOutputFile;
    private Worker[] workers;
    private Accumulator acc;

    public Manager(int numWorkers, File inputFile, File tempDir, File queryOutputFile, File statsOutputFile, SelectFromWhere query) {
        this.inputFile = inputFile;
        this.tempDir = tempDir;
        this.queryOutputFile = queryOutputFile;
        this.statsOutputFile = statsOutputFile;
        workers = new Worker[numWorkers];
        threads = new Thread[numWorkers];
        useVirtualThreads = true;
        acc = new Accumulator(12);
        for (int i = 0; i < numWorkers; i++) {
            workers[i] = new Worker(this, i, query);
        }
    }

    @Override
    public Worker getWorker(int id) {
        return workers[id];
    }

    @Override
    public void dismiss(int id) {
    }

    @Override
    public void partition() {
        try {
            long totalLines = FileUtils.countLines(inputFile);
            long fragLines = 1 + (totalLines - 1) / workers.length;
            tempDir.mkdir();
            FileUtils.splitLines(inputFile, tempDir, totalLines, fragLines, inputFile.getName());
        } catch (IOException e) {
        }
    }

    @Override
    public boolean assign(int id) {
        Worker worker = workers[id];
        switch (worker.getCurrentTask()) {
            case Worker.Task.Filter:
                worker.setInputFile(new File(tempDir, inputFile.getName() + id));
                worker.setQueryOutputFile(new File(tempDir, queryOutputFile.getName() + id));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void collect(int id) {
        synchronized (this) {
            acc.addAll(workers[id].getAccumulator());
        }
    }

    @Override
    public void manageWorkers() {
        super.manageWorkers();
        try {
            FileUtils.mergeLines(queryOutputFile, tempDir, queryOutputFile.getName());
            FileUtils.recursiveDelete(tempDir);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(statsOutputFile))) {
                for (int i = 0; i < 12; i++) {
                    for (int j = 0; j < 12; j++) {
                        if (i != j) {
                            SLRResult slr = acc.simpleLinearRegression(i, j);
                            StringBuilder sb = new StringBuilder();
                            sb.append("x = ");
                            sb.append(Reviews.HEADER.column(Reviews.HEADER.indexOf(Reviews.NUMERIC_VARS[i])).getName());
                            sb.append("\ny = ");
                            sb.append(Reviews.HEADER.column(Reviews.HEADER.indexOf(Reviews.NUMERIC_VARS[j])).getName());
                            sb.append("\nm = ");
                            sb.append(slr.getSlope());
                            sb.append("\nb = ");
                            sb.append(slr.getIntercept());
                            sb.append("\nr² = ");
                            sb.append(slr.getCoefficientOfDetermination());
                            sb.append('\n');
                            writer.write(sb.toString());
                            writer.newLine();
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
    }

}
