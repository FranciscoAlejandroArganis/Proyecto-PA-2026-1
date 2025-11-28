/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

/**
 * Representa la información de una columna en una tabla
 * @author francisco-alejandro
 */
public class Column {

    private String name;
    private Cell.Type type;

    /**
     * Construye una nueva columna con el nombre y tipo especificados
     * @param name el nombre de la columna
     * @param type el tipo de la columna
     */
    public Column(String name, Cell.Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Regresa el nombre de la columna
     * @return el nombre de la columna
     */
    public String getName() {
        return name;
    }

    /**
     * Regresa el tipo de la columna
     * @return el tipo de la columna
     */
    public Cell.Type getType() {
        return type;
    }

}
