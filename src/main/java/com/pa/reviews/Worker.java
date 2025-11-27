/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.consumers.Filter;
import com.pa.consumers.Tally;
import com.pa.multithread.AbstractWorker;
import com.pa.stats.Accumulator;
import com.pa.table.Cell;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author francisco-alejandro
 */
public class Worker extends AbstractWorker {

    public enum Task {
        FILTER_AND_TALLY,
        DONE
    }

    private Task currentTask;
    private File datasetFrag;
    private File filteredFrag;
    private Program program;
    private Tally tally;
    private Accumulator acc;

    public Worker(Manager manager, int id) {
        this.manager = manager;
        this.program = manager.getProgram();
        this.datasetFrag = new File(program.getTempDir(), program.getDataset().getName() + id);
        this.filteredFrag = new File(program.getTempDir(), program.getFiltered().getName() + id);
        this.id = id;
        currentTask = Task.FILTER_AND_TALLY;
    }

    @Override
    public void doWork() {
        switch (currentTask) {
            case Task.FILTER_AND_TALLY:
                filterAndTally();
                break;
            default:
                return;
        }
    }

    public void filterAndTally() {
        tally = new Tally(program.getColsToTally());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
            RowProcessor processor = new RowProcessor(datasetFrag);
            Filter filter = new Filter(program.getQuery(), writer);
            processor.process(filter.andThen(tally));
        } catch (IOException e) {
            
        }
        currentTask = Task.DONE;
    }

    public Task getCurrentTask() {
        return currentTask;
    }
    
    public UniqueCounter<Cell>[] getCounters(){
        return tally.getCounters();
    }

}
