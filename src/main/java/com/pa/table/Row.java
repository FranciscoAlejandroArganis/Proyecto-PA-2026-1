/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

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

}
