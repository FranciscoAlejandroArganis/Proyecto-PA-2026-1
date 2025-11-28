/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.query;

import com.pa.table.Header;
import com.pa.table.Row;
import java.util.function.Function;

/**
 * Representa una consulta de la forma select from
 * @author francisco-alejandro
 */
public class SelectFrom implements Function<Row, Row> {

    protected Header select;
    protected Header from;

    /**
     * Construye una nueva consulta con los parámetros especificados
     * @param select es la cabecera de la tabla resultante
     * @param from es la cabecera de la tabla objetivo
     */
    public SelectFrom(Header select, Header from) {
        this.select = select;
        this.from = from;
    }

    /**
     * Regresa la fila de la tabla resultante, dada la fila de la tabla objetivo
     * @param row la fila de la tabla objetivo
     * @return la fila correspondiente en la tabla resultante
     */
    @Override
    public Row apply(Row row) {
        Row result = new Row(select);
        for (int i = 0; i < select.size(); i++) {
            int j = from.indexOf(select.column(i).getName());
            result.setCell(i, row.getCell(j));
        }
        return result;
    }

}
