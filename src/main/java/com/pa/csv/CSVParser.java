/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Interpreta cadenas como registros en un archivo csv, separando los campos del registro por comas
 * @author francisco-alejandro
 */
public class CSVParser implements Iterator<String[]> {

    /**
     * Estado del autómata finito
     */
    private enum State {
        WHITE_SPACE_LEFT,
        WHITE_SPACE_RIGHT,
        OPENING_QUOTATION,
        CLOSING_QUOTATION,
        RAW_VALUE
    }

    private BufferedReader reader;
    private StringBuilder sb;
    private String line;
    private List<String> values;
    private State state;
    private int i, j;
    boolean done;

    /**
     * Construye un nuevo parser para el lector especificado
     * @param reader el lector que provee cadenas al parser
     */
    public CSVParser(BufferedReader reader) {
        this.reader = reader;
        sb = new StringBuilder();
        values = new ArrayList<>();
    }

    /**
     * Determina si se puede obtener otro registro del lector
     * @return <code>true</code> si y solo si el parser puede regresar otro registro
     */
    @Override
    public boolean hasNext() {
        try {
            line = reader.readLine();
            if (line == null) {
                return false;
            }
            reset();
            while (true) {
                processLine();
                if (done) {
                    return true;
                }
                i = 0;
                j = 0;
                line = reader.readLine();
                if (line == null) {
                    throw new CSVFormatException();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Regresa el siguiente registro obtenido del lector
     * @return un arreglo con los campos del registro que fueron separados por comas
     */
    @Override
    public String[] next() {
        return values.toArray(new String[0]);
    }

    /**
     * Reinicia el estado del autómata
     */
    private void reset() {
        state = State.WHITE_SPACE_LEFT;
        sb.delete(0, sb.length());
        values.clear();
        done = false;
        i = 0;
        j = 0;
    }

    /**
     * Procesa la siguiente línea obtenida del lector usando un autómata finito
     */
    private void processLine() {
        while (true) {
            switch (state) {
                case State.WHITE_SPACE_LEFT:
                    if (whiteSpaceLeft()) {
                        return;
                    }
                    break;
                case State.WHITE_SPACE_RIGHT:
                    if (whiteSpaceRight()) {
                        return;
                    }
                    break;
                case State.OPENING_QUOTATION:
                    if (openingQuotation()) {
                        return;
                    }
                    break;
                case State.CLOSING_QUOTATION:
                    if (closingQuotation()) {
                        return;
                    }
                    break;
                default:
                    if (rawValue()) {
                        return;
                    }
            }
        }
    }

    /**
     * Procesa un caracter cuando el autómata está en el estado <code>WHITE_SPACE_LEFT</code>
     * @return <code>true</code> si y solo si se alcanzó el final de la línea
     */
    private boolean whiteSpaceLeft() {
        if (j >= line.length()) {
            values.add("");
            done = true;
            return true;
        }
        char currentChar = line.charAt(j);
        if (currentChar == ',') {
            values.add("");
        } else if (currentChar == '"') {
            sb.delete(0, sb.length());
            i = j + 1;
            state = State.OPENING_QUOTATION;
        } else if (!Character.isWhitespace(currentChar)) {
            i = j;
            state = State.RAW_VALUE;
        }
        j++;
        return false;
    }

    /**
     * Procesa un caracter cuando el autómata está en el estado <code>WHITE_SPACE_RIGHT</code>
     * @return <code>true</code> si y solo si se alcanzó el final de la línea
     */
    private boolean whiteSpaceRight() {
        if (j >= line.length()) {
            done = true;
            return true;
        }
        char currentChar = line.charAt(j);
        if (currentChar == ',') {
            state = State.WHITE_SPACE_LEFT;
        } else if (!Character.isWhitespace(currentChar)) {
            throw new CSVFormatException();
        }
        j++;
        return false;
    }

    /**
     * Procesa un caracter cuando el autómata está en el estado <code>OPENING_QUOTATION</code>
     * @return <code>true</code> si y solo si se alcanzó el final de la línea
     */
    private boolean openingQuotation() {
        if (j >= line.length()) {
            sb.append(line.substring(i));
            sb.append('\n');
            done = false;
            return true;
        }
        char currentChar = line.charAt(j);
        if (currentChar == '"') {
            state = State.CLOSING_QUOTATION;
        }
        j++;
        return false;
    }

    /**
     * Procesa un caracter cuando el autómata está en el estado <code>CLOSING_QUOTATION</code>
     * @return <code>true</code> si y solo si se alcanzó el final de la línea
     */
    private boolean closingQuotation() {
        if (j >= line.length()) {
            sb.append(line.substring(i, line.length() - 1));
            values.add(sb.toString().replace("\"\"", "\""));
            sb.delete(0, sb.length());
            done = true;
            return true;
        }
        char currentChar = line.charAt(j);
        if (currentChar == ',') {
            sb.append(line.substring(i, j - 1));
            values.add(sb.toString().replace("\"\"", "\""));
            sb.delete(0, sb.length());
            state = State.WHITE_SPACE_LEFT;
        } else if (currentChar == '"') {
            state = State.OPENING_QUOTATION;
        } else if (!Character.isWhitespace(currentChar)) {
            throw new CSVFormatException();
        } else {
            sb.append(line.substring(i, j - 1));
            values.add(sb.toString().replace("\"\"", "\""));
            sb.delete(0, sb.length());
            state = State.WHITE_SPACE_RIGHT;
        }
        j++;
        return false;
    }

    /**
     * Procesa un caracter cuando el autómata está en el estado <code>RAW_VALUE</code>
     * @return <code>true</code> si y solo si se alcanzó el final de la línea
     */
    private boolean rawValue() {
        if (j >= line.length()) {
            values.add(line.substring(i));
            done = true;
            return true;
        }
        char currentChar = line.charAt(j);
        if (currentChar == ',') {
            values.add(line.substring(i, j));
            state = State.WHITE_SPACE_LEFT;
        } else if (currentChar == '"') {
            throw new CSVFormatException();
        } else if (Character.isWhitespace(currentChar)) {
            values.add(line.substring(i, j));
            state = State.WHITE_SPACE_RIGHT;
        }
        j++;
        return false;
    }

}
