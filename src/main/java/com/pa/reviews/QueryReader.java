/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.query.SelectFromWhere;
import com.pa.table.Cell;
import com.pa.table.Column;
import com.pa.table.Header;
import com.pa.table.Row;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 *
 * @author francisco-alejandro
 */
public class QueryReader {

    public enum Operator {
        LESS,
        LESS_OR_EQUAL,
        EQUAL,
        GREATER_OR_EQUAL,
        GREATER
    }

    private int[] indexList;
    private int index;
    private Cell.Type type;
    private Cell value;
    private Operator op;

    public SelectFromWhere readQuery() {
        try (Scanner sc = new Scanner(System.in).useDelimiter("\n")) {
            while (true) {
                System.out.print("Columnas a seleccionar: ");
                if (readIndexList(sc)) {
                    break;
                }
                System.out.println("Por favor ingresa * o una lista no vacía de índices (0 a " + (Reviews.HEADER.size() - 1) + ") separados por comas");
            }
            while (true) {
                System.out.print("Columna del criterio de búsqueda: ");
                if (readIndex(sc)) {
                    break;
                }
                System.out.println("Por favor ingresa un índice (0 a " + (Reviews.HEADER.size() - 1) + ")");
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

    private boolean readIndexList(Scanner sc) {
        try {
            String input = sc.next();
            if (input.equals("*")) {
                indexList = new int[Reviews.HEADER.size()];
                for (int i = 0; i < Reviews.HEADER.size(); i++) {
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
                if (indexList[i] < 0 || indexList[i] >= Reviews.HEADER.size()) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean readIndex(Scanner sc) {
        try {
            int index = Integer.parseInt(sc.next());
            if (index < 0 || index >= Reviews.HEADER.size()) {
                return false;
            }
            this.index = index;
        } catch (Exception e) {
            return false;
        }
        type = Reviews.HEADER.column(index).getType();
        return true;
    }

    private boolean readOperator(Scanner sc) {
        try {
            String input = sc.next();
            if (input.equals("==")) {
                op = Operator.EQUAL;
                return true;
            }
            if (!(type == Cell.Type.Int || type == Cell.Type.Float)) {
                System.out.println("Operador no válido para la columna seleccionada de tipo " + type);
                return false;
            }
            if (input.equals("<")) {
                op = Operator.LESS;
                return true;
            }
            if (input.equals("<=")) {
                op = Operator.LESS_OR_EQUAL;
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

    private boolean readValue(Scanner sc) {
        try {
            value = Cell.parseCell(sc.next(), type);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private SelectFromWhere buildQueryObject() {
        Column[] columns = new Column[indexList.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = Reviews.HEADER.column(indexList[i]);
        }
        Header select = new Header(columns);
        Header from = Reviews.HEADER;
        Predicate<Row> where;
        switch (type) {
            case Cell.Type.Bool:
                where = row -> row.getCell(index).getBool() == value.getBool();
                break;
            case Cell.Type.Int:
                switch (op) {
                    case Operator.LESS:
                        where = row -> row.getCell(index).getInt() < value.getInt();
                        break;
                    case Operator.LESS_OR_EQUAL:
                        where = row -> row.getCell(index).getInt() <= value.getInt();
                        break;
                    case Operator.EQUAL:
                        where = row -> row.getCell(index).getInt() == value.getInt();
                        break;
                    case Operator.GREATER_OR_EQUAL:
                        where = row -> row.getCell(index).getInt() >= value.getInt();
                        break;
                    default:
                        where = row -> row.getCell(index).getInt() > value.getInt();
                }
                break;
            case Cell.Type.Float:
                switch (op) {
                    case Operator.LESS:
                        where = row -> row.getCell(index).getFloat() < value.getFloat();
                        break;
                    case Operator.LESS_OR_EQUAL:
                        where = row -> row.getCell(index).getFloat() <= value.getFloat();
                        break;
                    case Operator.EQUAL:
                        where = row -> row.getCell(index).getFloat() == value.getFloat();
                        break;
                    case Operator.GREATER_OR_EQUAL:
                        where = row -> row.getCell(index).getFloat() >= value.getFloat();
                        break;
                    default:
                        where = row -> row.getCell(index).getFloat() > value.getFloat();
                }
                break;
            case Cell.Type.Str:
                where = row -> row.getCell(index).getStr().equals(value.getStr());
                break;
            default:
                where = row -> row.getCell(index).getTime().equals(value.getTime());
        }
        return new SelectFromWhere(select, from, where);
    }

}
