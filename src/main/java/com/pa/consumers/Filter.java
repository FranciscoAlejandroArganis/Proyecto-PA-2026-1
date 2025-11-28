/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.consumers;

import com.pa.query.SelectFromWhere;
import com.pa.reviews.Reviews;
import com.pa.table.Row;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.Consumer;

/**
 *
 * @author francisco-alejandro
 */
public class Filter implements Consumer<Row> {
    
    private SelectFromWhere query;
    private BufferedWriter writer;
    
    public Filter(SelectFromWhere query, BufferedWriter writer){
        this.query = query;
        this.writer = writer;
    }

    @Override
    public void accept(Row row) {
        row = query.apply(row);
        if (row != null) {
            try {
                writer.write(row.toString());
                writer.newLine();
            } catch (IOException e) {
                Reviews.LOGGER.warning("No se puede escribir en el archivo de salida la línea " + row);
            }
        }
    }
    
}
