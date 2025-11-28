/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa la información de las columnas de una tabla
 * @author francisco-alejandro
 */
public class Header {

    private Column[] columns;
    private Map<String, Integer> map;

    /**
     * Construye una nueva cabecera con las columnas especificadas
     * @param columns un arreglo con las columnas
     */
    public Header(Column[] columns) {
        this.columns = columns;
        map = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            String name = columns[i].getName();
            if (name == null) {
                throw new NullPointerException("Nombre de columna nulo");
            }
            if (map.put(name, i) != null) {
                throw new IllegalArgumentException("Nombres de columnas repetidos");
            }
        }
    }

    /**
     * Regresa la cantidad de columnas en la cabecera
     * @return la cantidad de columnas en la cabecera
     */
    public int size() {
        return columns.length;
    }

    /**
     * Regresa la columna en el índice especificado
     * @param i el índice de la columna
     * @return la columna en <code>i</code>
     */
    public Column column(int i) {
        return columns[i];
    }

    /**
     * Regresa el índice de la columna con el nombre especificado
     * @param columnName el nombre de la columna
     * @return el índice de la columna con nombre <code>columnName</code> o -1 si hay ninguna columna con ese nobmre
     */
    public int indexOf(String columnName) {
        return map.getOrDefault(columnName, -1);
    }

    /**
     * Regresa una nueva cabecera con el subcojunto de columnas en los índices especificados
     * @param indices los índices de las columnas que tendrá la cabecera resultante
     * @return la nueva cabecera con el subconjunto de columnas seleccionado por <code>indices</code>
     */
    public Header subset(int[] indices) {
        Column[] columns = new Column[indices.length];
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (0 <= j && j < this.columns.length) {
                columns[i] = this.columns[j];
            }
        }
        return new Header(columns);
    }

    /**
     * Regresa una nueva cabecera con el subcojunto de columnas con los nombres especificados
     * @param names los nombres de las columnas que tendrá la cabecera resultante
     * @return la nueva cabecera con el subconjunto de columnas seleccionado por <code>names</code>
     */
    public Header subset(String[] names) {
        Column[] columns = new Column[names.length];
        for (int i = 0; i < names.length; i++) {
            int j = indexOf(names[i]);
            if (0 <= j && j < this.columns.length) {
                columns[i] = this.columns[j];
            }
        }
        return new Header(columns);
    }

}
