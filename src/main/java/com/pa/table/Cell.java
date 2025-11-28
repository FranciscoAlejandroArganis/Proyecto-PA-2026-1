/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Representa una celda en una tabla
 * @author francisco-alejandro
 */
public class Cell implements Comparable<Cell> {

    /**
     * Tipos que se pueden almacenar en una celda
     */
    public enum Type {
        Bool,
        Int,
        Float,
        Str,
        Time
    }

    private Type type;
    private Object value;

    /**
     * Construye una nueva celda con un booleano
     * @param value el valor que tendrá la nueva celda
     */
    public Cell(boolean value) {
        type = Type.Bool;
        this.value = value;
    }

    /**
     * Construye una nueva celda con un entero
     * @param value el valor que tendrá la nueva celda
     */
    public Cell(long value) {
        type = Type.Int;
        this.value = value;
    }

    /**
     * Construye una nueva celda con un flotante
     * @param value el valor que tendrá la nueva celda
     */
    public Cell(double value) {
        type = Type.Float;
        this.value = value;
    }

    /**
     * Construye una nueva celda con una cadena
     * @param value el valor que tendrá la nueva celda
     */
    public Cell(String value) {
        type = Type.Str;
        this.value = value;
    }

    /**
     * Construye una nueva celda con un tiempo
     * @param value el valor que tendrá la nueva celda
     */
    public Cell(LocalDateTime value) {
        type = Type.Time;
        this.value = value;
    }

    /**
     * Regresa el tipo de la celda
     * @return el tipo del valor actual en la celda
     */
    public Type getType() {
        return type;
    }

    /**
     * Regresa el valor en la celda
     * @return el booleano en la celda
     */
    public boolean getBool() {
        if (type != Type.Bool) {
            throw new CellTypeMismatchException();
        }
        return (Boolean) value;
    }

    /**
     * Regresa el valor en la celda
     * @return el entero en la celda
     */
    public long getInt() {
        if (type != Type.Int) {
            throw new CellTypeMismatchException();
        }
        return (Long) value;
    }

    /**
     * Regresa el valor en la celda
     * @return el flotante en la celda
     */
    public double getFloat() {
        if (type != Type.Float) {
            throw new CellTypeMismatchException();
        }
        return (Double) value;
    }

    /**
     * Regresa el valor en la celda
     * @return la cadena en la celda
     */
    public String getStr() {
        if (type != Type.Str) {
            throw new CellTypeMismatchException();
        }
        return (String) value;
    }

    /**
     * Regresa el valor en la celda
     * @return el tiempo en la celda
     */
    public LocalDateTime getTime() {
        if (type != Type.Time) {
            throw new CellTypeMismatchException();
        }
        return (LocalDateTime) value;
    }

    /**
     * Compara el valor de la celda con el valor en otra celda
     * @param other la otra celda con la que se hace la comparación
     * @return un entero negativo, cero o positivo si el valor en la celda es menor, igual o mayor, respectivamente, al de <code>other</code>
     */
    @Override
    public int compareTo(Cell other) {
        if (type != other.type) {
            throw new CellTypeMismatchException();
        }
        switch (type) {
            case Type.Bool:
                boolean boolA = getBool();
                boolean boolB = other.getBool();
                if (boolA == boolB) {
                    return 0;
                }
                return boolB ? -1 : 1;
            case Type.Int:
                long intA = getInt();
                long intB = other.getInt();
                if (intA == intB) {
                    return 0;
                }
                return intA < intB ? -1 : 1;
            case Cell.Type.Float:
                double floatA = getFloat();
                double floatB = other.getFloat();
                if (floatA == floatB) {
                    return 0;
                }
                return floatA < floatB ? -1 : 1;
            case Cell.Type.Str:
                return getStr().compareTo(other.getStr());
            default:
                return getTime().compareTo(other.getTime());
        }
    }

    /**
     * Regresa el hash de la celda
     * @return el hash de la celda
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.type);
        hash = 29 * hash + Objects.hashCode(this.value);
        return hash;
    }

    /**
     * Determina si la celda es igual a otro objeto
     * @param obj el objeto contra el que se compara la celda
     * @return <code>true</code> si y solo si la celda es igual a <code>obj</code>
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
        final Cell other = (Cell) obj;
        if (this.type != other.type) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }

    /**
     * Regresa una represetnación en cadena del valor de la celda
     * @return una representación en cadena del valor la celda
     */
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
            default:
                return Long.toString(getTime().toEpochSecond(ZoneOffset.UTC));
        }
    }

    /**
     * Regresa una nueva celda a partir de una cadena y un tipo especificado
     * @param string la cadena que se interpretará como el valor de la celda
     * @param type el tipo que tendrá la celda
     * @return la celda con el valor interpretado
     */
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
            default:
                LocalDateTime timeVal = LocalDateTime.ofEpochSecond(Long.parseLong(string), 0, ZoneOffset.UTC);
                return new Cell(timeVal);
        }
    }

}
