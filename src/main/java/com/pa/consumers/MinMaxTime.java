/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.consumers;

import com.pa.table.Row;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 *
 * @author francisco-alejandro
 */
public class MinMaxTime implements Consumer<Row>{
    
    private LocalDateTime min;
    private LocalDateTime max;
    private int timeColIndex;

    public MinMaxTime(int timeColIndex) {
        this.timeColIndex = timeColIndex;
    }
    
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

    public LocalDateTime getMin() {
        return min;
    }

    public LocalDateTime getMax() {
        return max;
    }
    
}
