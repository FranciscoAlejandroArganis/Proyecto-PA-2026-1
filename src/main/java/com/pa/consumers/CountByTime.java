/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.consumers;

import com.pa.query.SelectFromWhere;
import com.pa.reviews.Pair;
import com.pa.reviews.Counter;
import com.pa.table.Row;
import com.pa.time.SegmentedTimePeriod;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author francisco-alejandro
 */
public class CountByTime implements Consumer<Row> {

    private SelectFromWhere selGroupCols;
    private SegmentedTimePeriod period;
    private Counter<Pair<Row,Integer>> counter;
    private Set<Row> groupReps;
    private int timeColIndex;

    public CountByTime(SelectFromWhere selGroupCols, SegmentedTimePeriod period, Set<Row> groupReps, int timeColIndex) {
        this.selGroupCols = selGroupCols;
        this.period = period;
        this.groupReps = groupReps;
        this.timeColIndex = timeColIndex;
        counter = new Counter<>();
    }

    @Override
    public void accept(Row row) {
        Row rep = selGroupCols.apply(row);
        if (rep == null) {
            return;
        }
        if (groupReps.contains(rep)) {
            int segment = period.segmentOf(row.getCell(timeColIndex).getTime());
            if (segment >= 0) {
                counter.increase(new Pair<>(rep, segment));
            }
        }
    }

    public Counter<Pair<Row, Integer>> getCounter() {
        return counter;
    }

}
