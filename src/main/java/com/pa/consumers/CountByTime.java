/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.consumers;

import com.pa.query.SelectFromWhere;
import com.pa.util.Pair;
import com.pa.util.Counter;
import com.pa.table.Row;
import com.pa.time.SegmentedTimePeriod;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Representa una operación de contar filas por segmentos de tiempo
 *
 * @author francisco-alejandro
 */
public class CountByTime implements Consumer<Row> {

    private SelectFromWhere selGroupCols;
    private SegmentedTimePeriod period;
    private Counter<Pair<Row, Integer>> counter;
    private Set<Row> groupReps;
    private int timeColIndex;

    /**
     * Construye una nueva operación
     *
     * @param selGroupCols es una consulta que selecciona columnas de interés
     * para la cuenta
     * @param period es el periodo de tiempo que define los segmentos
     * @param groupReps es un conjunto tal que solo se cuentan filas cuya
     * combinación de valores seleccionados por la consulta están en el conjunto
     * @param timeColIndex es el índice de la columna de donde se toma el tiempo
     */
    public CountByTime(SelectFromWhere selGroupCols, SegmentedTimePeriod period, Set<Row> groupReps, int timeColIndex) {
        this.selGroupCols = selGroupCols;
        this.period = period;
        this.groupReps = groupReps;
        this.timeColIndex = timeColIndex;
        counter = new Counter<>();
    }

    /**
     * Aplica la consulta sobre la fila. Si el resultado es <code>null<code> o la combinación de valores
     * seleccionados no están en <code>groupReps</code> se aumneta la cuenta de
     * los mismos.
     *
     * @param row
     */
    @Override
    public void accept(Row row) {
        Row rep = selGroupCols.apply(row);
        if (rep != null && groupReps.contains(rep)) {
            int segment = period.segmentOf(row.getCell(timeColIndex).getTime());
            if (segment >= 0) {
                counter.increase(new Pair<>(rep, segment));
            }
        }
    }

    /**
     * Regresa el contador de las combinaciones de valores seleccionados
     *
     * @return el contador de las combinaciones de valores seleccionados
     */
    public Counter<Pair<Row, Integer>> getCounter() {
        return counter;
    }

}
