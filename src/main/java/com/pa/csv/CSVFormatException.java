/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.pa.csv;

/**
 *
 * @author francisco-alejandro
 */
public class CSVFormatException extends RuntimeException {

    /**
     * Creates a new instance of <code>CSVFormatException</code> without detail
     * message.
     */
    public CSVFormatException() {
    }

    /**
     * Constructs an instance of <code>CSVFormatException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CSVFormatException(String msg) {
        super(msg);
    }
}
