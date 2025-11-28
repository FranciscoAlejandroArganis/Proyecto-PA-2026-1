/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author francisco-alejandro
 */
public class Row {

    private Header header;
    private Cell[] cells;

    public Row(Header header) {
        this.header = header;
        cells = new Cell[header.size()];
    }
    
    public Row(Header header, Cell[] cells){
        this.header = header;
        this.cells = cells;
    }

    public Header getHeader() {
        return header;
    }

    public Cell getCell(int i) {
        return cells[i];
    }

    public void setCell(int i, Cell cell) {
        if (cell.getType() != header.column(i).getType()) {
            throw new CellTypeMismatchException();
        }
        cells[i] = cell;
    }

    public void fill(String[] array) {
        for (int i = 0; i < header.size(); i++) {
            cells[i] = Cell.parseCell(array[i], header.column(i).getType());
        }
    }

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.header);
        hash = 89 * hash + Arrays.deepHashCode(this.cells);
        return hash;
    }

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
