/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.pa.table;

/**
 * Excepción que ocurre cuando el tipo del valor de una celda no es adecuado
 * @author francisco-alejandro
 */
public class CellTypeMismatchException extends RuntimeException {

    /**
     * Construye un nuevo ejemplar de <code>CellTypeMismatchException</code> sin mensaje
     */
    public CellTypeMismatchException() {
    }

    /**
     * Construye un nuevo ejemplar de <code>CellTypeMismatchException</code> con el mensaje especificado
     *
     * @param msg el mensaje
     */
    public CellTypeMismatchException(String msg) {
        super(msg);
    }
}
