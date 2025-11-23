/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.pa.table;

/**
 *
 * @author francisco-alejandro
 */
public class CellTypeMismatchException extends RuntimeException {

    /**
     * Creates a new instance of <code>CellTypeMismatchException</code> without
     * detail message.
     */
    public CellTypeMismatchException() {
    }

    /**
     * Constructs an instance of <code>CellTypeMismatchException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CellTypeMismatchException(String msg) {
        super(msg);
    }
}
