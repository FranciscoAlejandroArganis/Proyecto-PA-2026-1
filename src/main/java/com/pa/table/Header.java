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
    private Map<Column, Integer> map;

    public Header(Column[] columns) {
        this.columns = columns;
        map = new HashMap<>();
        for (int i = 0; i < columns.length; i++) {
            map.put(columns[i], i);
        }
    }

    public int size() {
        return columns.length;
    }

    public Column column(int i) {
        return columns[i];
    }

    public int indexOf(Column column) {
        return map.get(column);
    }

}
