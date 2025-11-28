/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.consumers.CountByTime;
import com.pa.consumers.Filter;
import com.pa.consumers.MinMaxTime;
import com.pa.consumers.Tally;
import com.pa.query.SelectFromWhere;
import com.pa.table.Cell;
import com.pa.table.Row;
import com.pa.time.SegmentedTimePeriod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 *
 * @author francisco-alejandro
 */
public class SequentialProgram extends Program {

    public SequentialProgram(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        super(filesInfo, numFrags, analysisInfo);
    }

    @Override
    protected void frags() {
        firstPass();
        buildGroups();
        secondPass();
    }

    private void firstPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            File filteredFrag = new File(filesInfo.getTempDir(), filesInfo.getFiltered().getName() + i);
            Tally tally = new Tally(analysisInfo.getColsToTally());
            MinMaxTime minMaxTime = new MinMaxTime(analysisInfo.getTimeColIndex());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                Filter filter = new Filter(query, writer);
                processor.process(filter.andThen(tally).andThen(minMaxTime));
            } catch (IOException e) {

            }
            Counter<Cell>[] counters = tally.getCounters();
            for (int j = 0; j < counters.length; j++) {
                for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                    this.uniqueCounters[j].increase(entry.getKey(), entry.getValue());
                }
            }
            LocalDateTime time = minMaxTime.getMin();
            if (minTime == null || (time != null && time.isBefore(minTime))) {
                minTime = time;
            }
            time = minMaxTime.getMax();
            if (maxTime == null || (time != null && time.isAfter(maxTime))) {
                maxTime = time;
            }
        }
    }

    private void secondPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            SelectFromWhere selGroupCols = analysisInfo.selGroupCols();
            SegmentedTimePeriod period = new SegmentedTimePeriod(minTime, maxTime, analysisInfo.getNumSegments());
            CountByTime countByTime = new CountByTime(selGroupCols, period, groupReps, analysisInfo.getTimeColIndex());
            try {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                processor.process(countByTime);
            } catch (IOException e) {
            }
            for (Row rep : groupReps) {
                for (int j = 0; j < analysisInfo.getNumSegments(); j++) {
                    Pair<Row, Integer> pair = new Pair<>(rep, j);
                    long count = countByTime.getCounter().getCount(pair);
                    boolCounter.increase(pair, count);
                }
            }
        }
    }

}
