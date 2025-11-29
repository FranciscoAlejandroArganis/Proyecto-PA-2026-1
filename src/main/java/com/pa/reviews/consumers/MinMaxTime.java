/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews.consumers;

import com.pa.table.Row;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Representa una operación de buscar el tiempo mínimo y máximo de una columna
 * @author francisco-alejandro
 */
public class MinMaxTime implements Consumer<Row>{
    
    /**
     * Mínimo tiempo encontrado hasta el momento
     */
    private LocalDateTime min;
    
    /**
     * Máximo tiempo encontrado hasta el momento
     */
    private LocalDateTime max;
    
    /**
     * Índice de la columna de donde se obtiene el valor del tiempo
     */
    private int timeColIndex;

    /**
     * Construye una nueva operación
     * @param timeColIndex es el índice de la columna para la que se busca el mínimo y máximo
     */
    public MinMaxTime(int timeColIndex) {
        this.timeColIndex = timeColIndex;
    }
    
    /**
     * Actualiza el mínimo y/o máximo si es que se encuentra un tiempo menor y/o mayor en la fila
     * @param row es la fila que se está procesando
     */
    @Override
    public void accept(Row row) {
        LocalDateTime time = row.getCell(timeColIndex).getTime();
        if (min == null || time.isBefore(min)){
            min = time;
        }
        if (max == null || time.isAfter(max)){
            max = time;
        }
    }

    /**
     * Regresa el tiempo mínimo encontrado
     * @return el tiempo mínimo encontrado
     */
    public LocalDateTime getMin() {
        return min;
    }

    /**
     * Regresa el tiempo máximo encontrado
     * @return el tiempo máximo encontrado
     */
    public LocalDateTime getMax() {
        return max;
    }
    
}
