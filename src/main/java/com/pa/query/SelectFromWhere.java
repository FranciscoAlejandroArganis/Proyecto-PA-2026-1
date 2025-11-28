/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.query;

import com.pa.table.Header;
import com.pa.table.Row;
import java.util.function.Predicate;

/**
 * Representa una consulta de la forma select from where
 * @author francisco-alejandro
 */
public class SelectFromWhere extends SelectFrom {

    protected Predicate<Row> where;

    /**
     * Construye una nueva consulta con los parámetros especificados
     * @param select es la cabecera de la tabla resultante
     * @param from es la cabecera de la tabla objetivo
     * @param where es el predicado que deben cumplir las filas para ser seleccionadas
     */
    public SelectFromWhere(Header select, Header from, Predicate<Row> where) {
        super(select, from);
        this.where = where;
    }

    /**
     * Regresa la fila de la tabla resultante, dada la fila de la tabla objetivo
     * @param row la fila de la tabla objetivo
     * @return la fila correspondiente en la tabla resultante o <code>null</code> si la fila no cumple el predicado
     */
    @Override
    public Row apply(Row row) {
        if (!where.test(row)) {
            return null;
        }
        return super.apply(row);
    }

}
