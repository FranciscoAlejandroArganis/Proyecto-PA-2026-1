/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.consumers;

import com.pa.util.Counter;
import com.pa.table.Cell;
import com.pa.table.Header;
import com.pa.table.Row;
import java.util.function.Consumer;

/**
 * Representa la operación de contar valores únicos
 * @author francisco-alejandro
 */
public class Tally implements Consumer<Row> {

    private Header colsToTally;
    private Counter<Cell>[] counters;
    
    /**
     * Construye una nueva operación
     * @param colsToTally es la cabecera con las columnas de los valores que se van a contar
     */
    public Tally(Header colsToTally){
        this.colsToTally = colsToTally;
        counters = new Counter[colsToTally.size()];
        for (int i = 0; i < counters.length; i ++){
            counters[i] = new Counter<>();
        }
    }
    
    /**
     * Aumenta la cuenta de los valores de la fila en las columnas de <code>colsToTally</code>
     * @param row es la fila que se está procesando
     */
    @Override
    public void accept(Row row) {
        Header header = row.getHeader();
        for (int i = 0; i < counters.length; i++){
            int j = header.indexOf(colsToTally.column(i).getName());
            counters[i].increase(row.getCell(j));
        }
    }
    
    /**
     * Regresa el arreglo de contadores de los valores únicos de las columnas
     * @return un arreglo tal que la entrada en <code>i</code> es el contador de la columna de <code>colsToTally</code> en <code>i</code>
     */
    public Counter<Cell>[] getCounters(){
        return counters;
    }
    
}
