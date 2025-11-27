/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author francisco-alejandro
 */
public class Header {

    private Column[] columns;
    private Map<String, Integer> map;

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

    public int size() {
        return columns.length;
    }

    public Column column(int i) {
        return columns[i];
    }

    public int indexOf(String columnName) {
        return map.getOrDefault(columnName, -1);
    }

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
