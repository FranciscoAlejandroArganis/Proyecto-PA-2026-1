/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

/**
 *
 * @author francisco-alejandro
 */
public class Column {

    private String name;
    private Cell.Type type;

    public Column(String name, Cell.Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Cell.Type getType() {
        return type;
    }

}
