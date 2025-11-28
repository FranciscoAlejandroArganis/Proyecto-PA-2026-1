/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.consumers.CountByTime;
import com.pa.consumers.Filter;
import com.pa.consumers.MinMaxTime;
import com.pa.consumers.Tally;
import com.pa.multithread.AbstractWorker;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.table.Cell;
import com.pa.table.Row;
import com.pa.time.SegmentedTimePeriod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 *
 * @author francisco-alejandro
 */
public class Worker extends AbstractWorker {

    public enum Task {
        FIRST_PASS,
        SECOND_PASS,
        DONE
    }

    private Task currentTask;
    private File datasetFrag;
    private File filteredFrag;
    private Program program;
    private Tally tally;
    private MinMaxTime minMaxTime;
    private CountByTime countByTime;
    private Accumulator acc;

    public Worker(Manager manager, int id) {
        this.manager = manager;
        this.program = manager.getProgram();
        this.datasetFrag = new File(program.getFilesInfo().getTempDir(), program.getFilesInfo().getDataset().getName() + id);
        this.filteredFrag = new File(program.getFilesInfo().getTempDir(), program.getFilesInfo().getFiltered().getName() + id);
        this.id = id;
        currentTask = Task.FIRST_PASS;
    }

    @Override
    public void doWork() {
        switch (currentTask) {
            case Task.FIRST_PASS:
                firstPass();
                break;
            case Task.SECOND_PASS:
                secondPass();
                break;
            default:
                return;
        }
    }

    private void firstPass() {
        tally = new Tally(program.getAnalysisInfo().getColsToTally());
        minMaxTime = new MinMaxTime(program.analysisInfo.getTimeColIndex());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            Filter filter = new Filter(program.getQuery(), writer);
            processor.process(filter.andThen(tally).andThen(minMaxTime));
        } catch (IOException e) {
            
        }
    }
    
    private void secondPass(){
        SelectFromWhere selGroupCols = program.analysisInfo.selGroupCols();
        SegmentedTimePeriod period = new SegmentedTimePeriod(program.getMinTime(), program.getMaxTime(), program.getAnalysisInfo().getNumSegments());
        countByTime = new CountByTime(selGroupCols, period, program.getGroupReps(), program.getAnalysisInfo().getTimeColIndex());
        try {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            processor.process(countByTime);
        } catch (IOException e) {
            
        }
    }
    
    public void countByTime(){
        currentTask = Task.DONE;
    }

    public Task getCurrentTask() {
        return currentTask;
    }
    
    public void setCurrentTask(Task currentTask){
        this.currentTask = currentTask;
    }
    
    public Counter<Cell>[] getCounters(){
        return tally.getCounters();
    }
    
    public Counter<Pair<Row,Integer>> getCounter(){
        return countByTime.getCounter();
    }

    public LocalDateTime getMinTime() {
        return minMaxTime.getMin();
    }
    
    public LocalDateTime getMaxTime() {
        return minMaxTime.getMax();
    }

}
