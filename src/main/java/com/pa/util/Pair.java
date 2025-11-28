/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.util;

import java.util.Objects;

/**
 * Representa un par de objetos
 * @author francisco-alejandro
 * @param <A> es el tipo del primer objeto
 * @param <B> es el tipo del segundo objeto
 */
public class Pair <A,B> {
    
    private A first;
    private B second;

    /**
     * Construye un nuevo par
     * @param first es el primer objeto
     * @param second es el segundo objeto
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Regresa el primer objeto en el par
     * @return el primer objeto
     */
    public A getFirst() {
        return first;
    }
    
    /**
     * Asigna el primer objeto en el par
     * @param first el nuevo valor del primer objeto
     */
    public void setFirst(A first) {
        this.first = first;
    }

    /**
     * Regresa el segundo objeto en el par
     * @return el segundo objeto
     */
    public B getSecond() {
        return second;
    }

    /**
     * Asigna el segundo objeto en el par
     * @param first el nuevo valor del segundo objeto
     */
    public void setSecond(B second) {
        this.second = second;
    }

    /**
     * Regresa una represetación en cadena del par
     * @return una represetnación en cadena del par
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Regresa el hash del par
     * @return el hash del par
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.first);
        hash = 97 * hash + Objects.hashCode(this.second);
        return hash;
    }

    /**
     * Determina si el par es igual a otro objeto
     * @param obj el objeto contra el que se compara el par
     * @return <code>true</code> si y solo si el par es igual a <code>obj</code>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        return Objects.equals(this.second, other.second);
    }
    
}
