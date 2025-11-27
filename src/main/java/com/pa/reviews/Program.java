/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.io.FileUtils;
import com.pa.query.SelectFromWhere;
import com.pa.table.Cell;
import com.pa.table.Header;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author francisco-alejandro
 */
public abstract class Program {

    protected File dataset;
    protected File tempDir;
    protected File filtered;
    protected File results;
    protected int numFrags;
    protected SelectFromWhere query;
    protected Header colsToTally;
    protected UniqueCounter<Cell>[] counters;
    
    public Program(String dataset, String tempDir, String filtered, String results, Header colsToTally, int numFrags){
        this.dataset = new File(dataset);
        this.tempDir = new File(tempDir);
        this.filtered = new File(filtered);
        this.results = new File(results);
        this.colsToTally = colsToTally;
        this.numFrags = numFrags;
        counters = new UniqueCounter[colsToTally.size()];
        for (int i = 0; i < counters.length; i++){
            counters[i] = new UniqueCounter<>();
        }
    }
    
    public void execute(){
        part();
        frags();
        merge();
    }

    private void part() {
        QueryReader qr = new QueryReader();
        query = qr.readQuery();
        if (!tempDir.mkdir()){
            Reviews.LOGGER.severe("No se puede crear el directorio temporal");
            return;
        }
        try {
            long totalLines = FileUtils.countLines(dataset);
            long fragLines = 1 + (totalLines - 1) / numFrags;
            FileUtils.splitLines(dataset, tempDir, totalLines, fragLines, dataset.getName());
        } catch (IOException e){
            Reviews.LOGGER.severe("No se puede escribir los archivos en el directorio temporal");
        }
    }
    
    private void merge(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(results))) {
            for (int i = 0; i < counters.length; i++){
                writer.write(colsToTally.column(i).getName());
                writer.newLine();
                for (Map.Entry<Cell, Long> entry : counters[i].getMap().entrySet()){
                    writer.write(entry.getKey() + " : " + entry.getValue());
                    writer.newLine();
                }
                writer.newLine();
            }
            FileUtils.mergeLines(filtered, tempDir, filtered.getName());
            FileUtils.recursiveDelete(tempDir);
        } catch (IOException e){
            
        }
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

    public int getNumFrags() {
        return numFrags;
    }

    public SelectFromWhere getQuery() {
        return query;
    }

    public Header getColsToTally() {
        return colsToTally;
    }

    public UniqueCounter<Cell>[] getCounters() {
        return counters;
    }
    
    protected abstract void frags();

}
