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
 * Representa la operación de escribir las filas filtradas
 * @author francisco-alejandro
 */
public class Filter implements Consumer<Row> {
    
    private SelectFromWhere query;
    private BufferedWriter writer;
    
    /**
     * Construye una nueva operación
     * @param query es la consultada usada para filtrar
     * @param writer es el escritor usado para escribir las filas
     */
    public Filter(SelectFromWhere query, BufferedWriter writer){
        this.query = query;
        this.writer = writer;
    }

    /**
     * Escribe la fila resultante de la consulta si no es <code>null</code>
     * @param row es la fila que se está procesando
     */
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
