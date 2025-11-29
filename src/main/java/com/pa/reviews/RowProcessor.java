/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.csv.CSVParser;
import com.pa.table.Header;
import com.pa.table.Row;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Clase que procesa todos las filas de una tabla leida de un archivo csv y aplica una operación por cada fila.
 * @author francisco-alejandro
 */
public class RowProcessor {
    
    /**
     * Archivo csv de donde se leen las filas
     */
    private File file;
    
    /**
     * Cabecera de la tabla en el archivo csv
     */
    private Header header;

    /**
     * Construye un nuevo procesador de filas
     * @param file es el archivo de donde se leen las filas
     * @param header es la cabecera de los datos en el archivo
     */
    public RowProcessor(File file, Header header) {
        this.file = file;
        this.header = header;
    }
    
    /**
     * Inicia le procesamiento de todeas las filas en el archivo
     * @param consumer es la operación que se realiza por cada fila leida
     * @throws IOException 
     */
    public void process(Consumer<Row> consumer) throws IOException{
        Row row = new Row(header);
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
                    Reviews.LOGGER.warning("No se puede interpretar la fila del csv: " + Arrays.toString(values));
                }
            }
        }
    }
    
}
