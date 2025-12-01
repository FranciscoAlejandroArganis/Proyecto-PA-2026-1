/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews.consumers;

import com.pa.query.SelectFromWhere;
import com.pa.table.Row;
import com.pa.time.SegmentedPeriod;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Representa una operación de buscar el tiempo mínimo y máximo, por grupos
 *
 * @author francisco-alejandro
 */
public class MinMaxTimeGroup implements Consumer<Row> {

    /**
     * Consulta usada para seleccionar filas que pertenecen a uno de los grupos
     * de interés
     */
    private SelectFromWhere query;

    /**
     * Mapa de grupos al mejor periodo de tiempo encontrado hasta el momento
     */
    private Map<Row, SegmentedPeriod> map;

    /**
     * Índice de la columna de donde se obtiene el tiempo
     */
    private int timeColIndex;

    /**
     * Construye una nueva operación
     *
     * @param query es la consulta usada para seleccionar filas que pertenecen a
     * un grupo
     * @param timeColIndex es el índice de la columna de donde se obtiene el
     * tiempo, para el cual se busca el mínimo y máximo
     */
    public MinMaxTimeGroup(SelectFromWhere query, int timeColIndex) {
        this.query = query;
        this.timeColIndex = timeColIndex;
        map = new HashMap<>();
    }

    /**
     * Aplica la consulta sobre la fila. Si la fila pertence a uno de los grupos
     * de interés, actualiza el mínimo y/o máximo valor de tiempo encontrado, en
     * el periodo del grupo
     *
     * @param row es la fila que se está procesando
     */
    @Override
    public void accept(Row row) {
        Row rep = query.apply(row);
        if (rep != null) {
            LocalDateTime time = row.getCell(timeColIndex).getTime();
            SegmentedPeriod period = map.getOrDefault(rep, new SegmentedPeriod());
            if (time.isBefore(period.getStart())) {
                period.setStart(time);
                map.put(rep, period);
            }
            if (time.isAfter(period.getEnd())) {
                period.setEnd(time);
                map.put(rep, period);
            }
        }
    }

    /**
     * Regresa el mapa de grupos a periodos con el mínimo y máximo tiempos
     *
     * @return el mapa de grupos a periodos con el mínimo y máximo tiempos
     */
    public Map<Row, SegmentedPeriod> getMap() {
        return map;
    }

}
