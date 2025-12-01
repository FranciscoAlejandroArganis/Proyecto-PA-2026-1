/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews.consumers;

import com.pa.query.SelectFromWhere;
import com.pa.table.Row;
import com.pa.time.SegmentedPeriod;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Representa una operación de acumular diferencias de verdaderos menos falsos
 * por segmentos de tiempo, por grupos
 *
 * @author francisco-alejandro
 */
public class DifferenceTimeGroup implements Consumer<Row> {

    /**
     * Consulta usada para seleccionar filas que pertenecen a uno de los grupos
     * de interés
     */
    private SelectFromWhere query;

    /**
     * Mapa de los grupos a los periodos segmentados
     */
    private Map<Row, SegmentedPeriod> periodMap;

    /**
     * Mapa de los grupos a las series de tiempo
     */
    private Map<Row, Long[]> seriesMap;

    /**
     * Índice de la columna de donde se obtiene el tiempo
     */
    private int timeColIndex;

    /**
     * Índice de la columna de donde se obtiene el valor booleano
     */
    private int boolColIndex;

    /**
     * Construye una nueva operación
     *
     * @param query es la consulta usada para seleccionar filas que pertenecen a
     * un grupo
     * @param periodMap es el mapa usado para obtener el periodo segmentado de
     * un grupo
     * @param timeColIndex es el índice de la columna de donde se obtiene el
     * tiempo
     * @param boolColIndex es el índice de la columna de donde se obtiene el
     * valor booleano
     */
    public DifferenceTimeGroup(SelectFromWhere query, Map<Row, SegmentedPeriod> periodMap, int timeColIndex, int boolColIndex) {
        this.query = query;
        this.timeColIndex = timeColIndex;
        this.boolColIndex = boolColIndex;
        this.periodMap = periodMap;
        seriesMap = new HashMap<>();
    }

    /**
     * Aplica la consulta sobre la fila. Si la fila pertence a uno de los grupos
     * de interés, se toman los valores en la columna de tiempo y la columna
     * booleana dadas por los índices. Se suma <code>1</code> o <code>-1</code>
     * a la serie del grupo, en el segmento de tiempo correspondiente, si el
     * valor booleano es verdado o falso.
     *
     * @param row es la fila que se está procesando
     */
    @Override
    public void accept(Row row) {
        Row rep = query.apply(row);
        if (rep != null) {
            LocalDateTime time = row.getCell(timeColIndex).getTime();
            SegmentedPeriod period = periodMap.get(rep);
            int index = period.segmentOf(time);
            long update = row.getCell(boolColIndex).getBool() ? 1 : -1;
            Long[] timeSeries = seriesMap.get(rep);
            if (timeSeries == null) {
                timeSeries = new Long[period.getNumSegments()];
                Arrays.fill(timeSeries, 0l);
                timeSeries[index] = update;
                seriesMap.put(rep, timeSeries);
            } else {
                timeSeries[index] += update;
            }
        }
    }

    /**
     * Regresa el mapa de grupos a series de tiempo
     *
     * @return el mapa de grupos a series de tiempo
     */
    public Map<Row, Long[]> getMap() {
        return seriesMap;
    }

}
