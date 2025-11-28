/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.pa.csv;

/**
 * Excepción que ocurren cuando el formato de un registro de un csv no es adecuado
 * @author francisco-alejandro
 */
public class CSVFormatException extends RuntimeException {

    /**
     * Construye un nuevo ejemplar de <code>CSVFormatException</code> sin mensaje
     */
    public CSVFormatException() {
    }

    /**
     * Construye un nuevo ejemplar de <code>CSVFormatException</code> con el mensaje especificado
     *
     * @param msg el mensaje
     */
    public CSVFormatException(String msg) {
        super(msg);
    }
}
