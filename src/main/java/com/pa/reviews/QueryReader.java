/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.query.SelectFromWhere;
import com.pa.table.Cell;
import com.pa.table.Header;
import com.pa.table.Row;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Clase que solicita al usuario y lee una consulta por la entrada estándar
 * @author francisco-alejandro
 */
public class QueryReader {

    /**
     * Tipo de operador del criterio de búsqueda de la consulta
     */
    public enum Operator {
        LESS,
        LESS_OR_EQUAL,
        EQUAL,
        GREATER_OR_EQUAL,
        GREATER
    }

    private Header header;
    private int[] indexList;
    private int index;
    private Cell.Type type;
    private Cell value;
    private Operator op;
    
    /**
     * Construye un nuevo lector de consultas
     * @param header es la cabecera de los datos sobre los que se va a aplicar la consulta
     */
    public QueryReader(Header header){
        this.header = header;
    }

    /**
     * Regresa la consulta ingresada por el usuario
     * @return la consulta ingresada por el usuario
     */
    public SelectFromWhere readQuery() {
        try (Scanner sc = new Scanner(System.in).useDelimiter("\n")) {
            while (true) {
                System.out.print("Columnas a seleccionar: ");
                if (readIndexList(sc)) {
                    break;
                }
                System.out.println("Por favor ingresa * o una lista no vacía de índices (0 a " + (header.size() - 1) + ") separados por comas");
            }
            while (true) {
                System.out.print("Columna del criterio de búsqueda: ");
                if (readIndex(sc)) {
                    break;
                }
                System.out.println("Por favor ingresa un índice (0 a " + (header.size() - 1) + ")");
            }
            while (true) {
                System.out.print("Operador del criterio de búsqueda: ");
                if (readOperator(sc)) {
                    break;
                }
                System.out.println("Por favor ingresa <, <=, ==, >= o >");
            }
            while (true) {
                System.out.print("Valor de comparación del criterio de búsqueda: ");
                if (readValue(sc)) {
                    break;
                }
                System.out.println("Por favor ingresa un valor válido para el tipo " + type);
            }
        }
        return buildQueryObject();
    }

    /**
     * Lee la lista de índices de columnas por seleccionar
     * @param sc es el escáner usado para leer la entrada estándar
     * @return <code>true</code> si y solo si el usuario ingresó una lista válida
     */
    private boolean readIndexList(Scanner sc) {
        try {
            String input = sc.next();
            if (input.equals("*")) {
                indexList = new int[header.size()];
                for (int i = 0; i < header.size(); i++) {
                    indexList[i] = i;
                }
                return true;
            }
            String[] indices = input.split(",");
            if (indices.length == 0) {
                return false;
            }
            indexList = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                indexList[i] = Integer.parseInt(indices[i]);
                if (indexList[i] < 0 || indexList[i] >= header.size()) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Lee el índice de la columna del criterio de búsqueda
     * @param sc es el escáner usado para leer la entrada estándar
     * @return <code>true</code> si y solo si el usuario ingresó un índice válido
     */
    private boolean readIndex(Scanner sc) {
        try {
            int index = Integer.parseInt(sc.next());
            if (index < 0 || index >= header.size()) {
                return false;
            }
            this.index = index;
        } catch (Exception e) {
            return false;
        }
        type = header.column(index).getType();
        return true;
    }

    /**
     * Lee el operador del criterio de búsqueda
     * @param sc es el escáner usado para leer la entrada estándar
     * @return <code>true</code> si y solo si el usuario ingresó un operador válido
     */
    private boolean readOperator(Scanner sc) {
        try {
            String input = sc.next();
            if (input.equals("<")) {
                op = Operator.LESS;
                return true;
            }
            if (input.equals("<=")) {
                op = Operator.LESS_OR_EQUAL;
                return true;
            }
            if (input.equals("==")) {
                op = Operator.EQUAL;
                return true;
            }
            if (input.equals(">=")) {
                op = Operator.GREATER_OR_EQUAL;
                return true;
            }
            if (input.equals(">")) {
                op = Operator.GREATER;
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Lee el valor del criterio de búsqueda
     * @param sc es el escáner usado para leer la entrada estándar
     * @return <code>true</code> si y solo si el usuario ingresó un valor válido
     */
    private boolean readValue(Scanner sc) {
        try {
            value = Cell.parseCell(sc.next(), type);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Construye el objeto de la consulta
     * @return la consulta ingresada por el usuario
     */
    private SelectFromWhere buildQueryObject() {
        Header select = header.subset(indexList);
        Header from = header;
        Predicate<Row> where;
        switch (op) {
                    case Operator.LESS:
                        where = row -> row.getCell(index).compareTo(value) < 0;
                        break;
                    case Operator.LESS_OR_EQUAL:
                        where = row -> row.getCell(index).compareTo(value) <= 0;
                        break;
                    case Operator.EQUAL:
                        where = row -> row.getCell(index).compareTo(value) == 0;
                        break;
                    case Operator.GREATER_OR_EQUAL:
                        where = row -> row.getCell(index).compareTo(value) >= 0;
                        break;
                    default:
                        where = where = row -> row.getCell(index).compareTo(value) > 0;
                }
        return new SelectFromWhere(select, from, where);
    }

}
