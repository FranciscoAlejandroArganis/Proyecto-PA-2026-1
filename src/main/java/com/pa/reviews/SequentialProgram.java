/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.consumers.Filter;
import com.pa.consumers.Tally;
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
public class SequentialProgram extends Program {

    public SequentialProgram(String dataset, String tempDir, String filtered, String results, Header colsToTally, int numFrags) {
        super(dataset, tempDir, filtered, results, colsToTally, numFrags);
    }

    @Override
    protected void frags() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(tempDir, dataset.getName() + i);
            File filteredFrag = new File(tempDir, filtered.getName() + i);
            Tally tally = new Tally(colsToTally);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
                RowProcessor processor = new RowProcessor(datasetFrag);
                Filter filter = new Filter(query, writer);
                processor.process(filter.andThen(tally));
            } catch (IOException e) {

            }
            UniqueCounter<Cell>[] counters = tally.getCounters();
            for (int j = 0; j < counters.length; j++) {
                for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                    this.counters[j].increase(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
