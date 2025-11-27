/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.consumers;

import com.pa.reviews.UniqueCounter;
import com.pa.table.Cell;
import com.pa.table.Header;
import com.pa.table.Row;
import java.util.function.Consumer;

/**
 *
 * @author francisco-alejandro
 */
public class Tally implements Consumer<Row> {

    private Header colsToTally;
    private UniqueCounter<Cell>[] counters;
    
    public Tally(Header colsToTally){
        this.colsToTally = colsToTally;
        counters = new UniqueCounter[colsToTally.size()];
        for (int i = 0; i < counters.length; i ++){
            counters[i] = new UniqueCounter<>();
        }
    }
    
    @Override
    public void accept(Row row) {
        Header header = row.getHeader();
        for (int i = 0; i < counters.length; i++){
            int j = header.indexOf(colsToTally.column(i).getName());
            counters[i].increase(row.getCell(j));
        }
    }
    
    public UniqueCounter<Cell>[] getCounters(){
        return counters;
    }
    
}
