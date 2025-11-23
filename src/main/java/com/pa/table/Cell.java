/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author francisco-alejandro
 */
public class Cell {

    public enum Type {
        Bool,
        Int,
        Float,
        Str,
        Time
    }

    private Type type;
    private Object value;

    public Cell(boolean value) {
        type = Type.Bool;
        this.value = value;
    }

    public Cell(long value) {
        type = Type.Int;
        this.value = value;
    }

    public Cell(double value) {
        type = Type.Float;
        this.value = value;
    }

    public Cell(String value) {
        type = Type.Str;
        this.value = value;
    }

    public Cell(LocalDateTime value) {
        type = Type.Time;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public boolean getBool() {
        if (type != Type.Bool) {
            throw new CellTypeMismatchException();
        }
        return (Boolean) value;
    }

    public long getInt() {
        if (type != Type.Int) {
            throw new CellTypeMismatchException();
        }
        return (Long) value;
    }

    public double getFloat() {
        if (type != Type.Float) {
            throw new CellTypeMismatchException();
        }
        return (Double) value;
    }

    public String getStr() {
        if (type != Type.Str) {
            throw new CellTypeMismatchException();
        }
        return (String) value;
    }

    public LocalDateTime getTime() {
        if (type != Type.Time) {
            throw new CellTypeMismatchException();
        }
        return (LocalDateTime) value;
    }

    @Override
    public String toString() {
        switch (type) {
            case Type.Bool:
                if (getBool()) {
                    return "1";
                }
                return "0";
            case Type.Int:
                return Long.toString(getInt());
            case Cell.Type.Float:
                return Double.toString(getFloat());
            case Cell.Type.Str:
                return '\"' + getStr().replaceAll("\"", "\"\"") + '\"';
            case Cell.Type.Time:
                return Long.toString(getTime().toEpochSecond(ZoneOffset.UTC));
        }
        throw new CellTypeMismatchException();
    }

    public static Cell parseCell(String string, Type type) {
        switch (type) {
            case Type.Bool:
                boolean boolVal = Integer.parseInt(string) != 0;
                return new Cell(boolVal);
            case Cell.Type.Int:
                long intVal = Long.parseLong(string);
                return new Cell(intVal);
            case Cell.Type.Float:
                double floatVal = Double.parseDouble(string);
                return new Cell(floatVal);
            case Cell.Type.Str:
                return new Cell(string);
            case Cell.Type.Time:
                LocalDateTime timeVal = LocalDateTime.ofEpochSecond(Long.parseLong(string), 0, ZoneOffset.UTC);
                return new Cell(timeVal);
        }
        throw new CellTypeMismatchException();
    }

}
