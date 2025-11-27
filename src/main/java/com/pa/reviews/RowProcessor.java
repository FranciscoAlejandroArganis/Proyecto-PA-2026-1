/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.csv.CSVParser;
import com.pa.table.Row;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 *
 * @author francisco-alejandro
 */
public class RowProcessor {
    
    private File file;

    public RowProcessor(File file) {
        this.file = file;
    }
    
    public void process(Consumer<Row> consumer) throws IOException{
        Row row = new Row(Reviews.HEADER);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            CSVParser parser = new CSVParser(reader);
            while (true) {
                String[] values = new String[0];
                try {
                    if (!parser.hasNext()) {
                        break;
                    }
                    values = parser.next();
                    row.fill(values);
                    consumer.accept(row);
                } catch (Exception e) {
                    Reviews.LOGGER.warning("No se puede interpretar la fila del csv " + Arrays.toString(values));
                }
            }
        }
    }
    
}
