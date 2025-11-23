/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.csv;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author francisco-alejandro
 */
public class CSVParser {

    private String line;
    private int start;
    private int end;
    private List<String> values;

    public CSVParser() {
        values = new ArrayList<>();
    }

    // start en la primera posición después de ,
    public String[] parseLine(String line) {
        this.line = line;
        values.clear();
        start = 0;
        while (true) {
            if (start >= line.length()) {
                values.add("");
                break;
            }
            if (line.charAt(start) == '\"') {
                start++;
                if (stringScan()) {
                    break;
                }
            }
            end = line.indexOf(",", start);
            if (end < 0) {
                values.add(line.substring(start));
                break;
            }
            values.add(line.substring(start, end));
            start = end + 1;
        }
        return values.toArray(new String[0]);
    }

    // start en la primera posición después de "
    private boolean stringScan() {
        end = start;
        while (true) {
            end = line.indexOf("\"", end);
            if (end < 0) {
                throw new IllegalArgumentException();
            }
            if (end + 1 >= line.length()) {
                values.add(line.substring(start, end).replace("\"\"", "\""));
                return true;
            }
            if (line.charAt(end + 1) == ',') {
                values.add(line.substring(start, end).replace("\"\"", "\""));
                start = end + 2;
                return false;
            }
            if (line.charAt(end + 1) != '\"') {
                throw new IllegalArgumentException();
            }
            end += 2;
        }
    }

}
