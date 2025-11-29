/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

import java.util.Arrays;
import java.util.Objects;

/**
 * Representa una fila de celdas en una tabla
 * @author francisco-alejandro
 */
public class Row {

    /**
     * Cabecera de la tabla a la que pertenece la fila
     */
    private Header header;
    
    /**
     * Arreglo de celdas en la fila
     */
    private Cell[] cells;

    /**
     * Construye una nueva fila vacía para una tabla con la cabecera especificada
     * @param header la cabecera de la tabla a la que pertenece la nueva fila
     */
    public Row(Header header) {
        this.header = header;
        cells = new Cell[header.size()];
    }
    
    /**
     * Construye una nueva fila llena de celdas para una tabla con la cabecera especificada
     * @param header la cabecera de la tabla a la que pertenece la nueva fila
     * @param cells las celdas de la fila
     */
    public Row(Header header, Cell[] cells){
        this.header = header;
        this.cells = cells;
    }

    /**
     * Regresa la cabecera de la tabla a la que pertence la fila
     * @return la cabecera de la tabla a la que pertence la fila
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Regresa la celda en el índice especificado
     * @param i el índice de la celda
     * @return la celda en <code>i</code>
     */
    public Cell getCell(int i) {
        return cells[i];
    }

    /**
     * Asigna la celda en el índice especificado
     * @param i el índice de la celda
     * @param cell la nueva celda que estará en <code>i</code>
     */
    public void setCell(int i, Cell cell) {
        if (cell.getType() != header.column(i).getType()) {
            throw new CellTypeMismatchException();
        }
        cells[i] = cell;
    }

    /**
     * Llena la fila con las celdas interpretadas a partir de un conjunto de cadenas
     * @param array las cadenas a partir de las cuales se interpretan los valores de las celdas
     */
    public void fill(String[] array) {
        for (int i = 0; i < header.size(); i++) {
            cells[i] = Cell.parseCell(array[i], header.column(i).getType());
        }
    }

    /**
     * Regresa una representación en cadena de la fila
     * @return una represetnación en cadena de la fila
     */
    @Override
    public String toString() {
        if (header.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(cells[0]);
        for (int i = 1; i < header.size(); i++) {
            sb.append(',');
            sb.append(cells[i]);
        }
        return sb.toString();
    }

    /**
     * Regresa el hash de la fila
     * @return el hash de la fila
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.header);
        hash = 89 * hash + Arrays.deepHashCode(this.cells);
        return hash;
    }

    /**
     * Determina si la fila es igual a otro objeto
     * @param obj el objeto contra el que se compara la fila
     * @return <code>true</code> si y solo si la fila es igual a <code>obj</code>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Row other = (Row) obj;
        if (!Objects.equals(this.header, other.header)) {
            return false;
        }
        return Arrays.deepEquals(this.cells, other.cells);
    }

}
