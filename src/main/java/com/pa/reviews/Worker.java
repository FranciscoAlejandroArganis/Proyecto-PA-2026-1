/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.csv.CSVParser;
import com.pa.multithread.AbstractWorker;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.table.Row;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.Arrays;

/**
 *
 * @author francisco-alejandro
 */
public class Worker extends AbstractWorker {

    public enum Task {
        Filter,
        Done
    }

    private Task currentTask;
    private File inputFile;
    private File queryOutputFile;
    private SelectFromWhere query;
    private Accumulator acc;

    public Worker(Manager manager, int id, SelectFromWhere query) {
        this.manager = manager;
        this.id = id;
        this.query = query;
        acc = new Accumulator(12);
        currentTask = Task.Filter;
    }

    @Override
    public void doWork() {
        Row row = new Row(Reviews.HEADER);
        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFile)); BufferedWriter writer = new BufferedWriter(new FileWriter(queryOutputFile))) {
            CSVParser parser = new CSVParser(reader);
            int i = 0;
            while (true) {
                String[] values = new String[0];
                try {
                    if (!parser.hasNext()) {
                        break;
                    }
                    values = parser.next();
                    row.fill(values);
                } catch (Exception e) {
                    Reviews.LOGGER.warning(Arrays.toString(values));
                }
                double[] vls = {
                    row.getCell(4).getInt(),
                    row.getCell(5).getInt(),
                    row.getCell(6).getInt(),
                    row.getCell(7).getInt(),
                    row.getCell(8).getInt(),
                    row.getCell(9).getTime().toEpochSecond(ZoneOffset.UTC),
                    row.getCell(12).getTime().toEpochSecond(ZoneOffset.UTC),
                    row.getCell(13).getTime().toEpochSecond(ZoneOffset.UTC),
                    row.getCell(15).getInt(),
                    row.getCell(16).getInt(),
                    row.getCell(17).getFloat(),
                    row.getCell(18).getInt()
                };
                acc.add(vls);
                Row result = query.apply(row);
                if (result != null) {
                    writer.write(result.toString());
                    writer.newLine();
                }

            }
        } catch (IOException e) {

        }
        currentTask = Task.Done;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public Accumulator getAccumulator() {
        return acc;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }
    
    public void setQueryOutputFile(File queryOutputFile) {
        this.queryOutputFile = queryOutputFile;
    }

}
