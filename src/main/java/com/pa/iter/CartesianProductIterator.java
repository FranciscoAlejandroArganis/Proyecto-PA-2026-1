/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.iter;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Iterador de las tuplas de un producto cartesino
 * @author francisco-alejandro
 * @param <T> es el tipo de los elementos en los conjuntos del producto cartesiano
 */
public class CartesianProductIterator<T> implements Iterator<T[]> {

    private T[][] sets;
    private int[] indices;
    private boolean done;

    /**
     * Construye un nuevo iterador a partir de los conjuntos especificados
     * @param sets es un arreglo donde cada entrada es un arreglo con un conjunto incluido en el producto cartesiano
     */
    public CartesianProductIterator(T[][] sets) {
        this.sets = sets;
        indices = new int[sets.length];
        if (sets.length == 0) {
            done = true;
        }
        for (int i = 0; i < sets.length; i++) {
            if (sets[i].length == 0) {
                done = true;
            }
        }
    }

    /**
     * Determina si hay otra tupla en el producto cartesiano
     * @return <code>true</code> si y solo si hay otra tupla en el producto cartesiano
     */
    @Override
    public boolean hasNext() {
        return !done;
    }

    /**
     * Regresa la siguiente tupla en el producto cartesiano
     * @return un arreglo con los valores de la siguiente tupla en el producto cartesiano
     */
    @Override
    public T[] next() {
        T[] tuple = Arrays.copyOf(sets[0], sets.length);
        for (int i = 0; i < sets.length; i++) {
            tuple[i] = sets[i][indices[i]];
        }
        int i = 0;
        while (true) {
            if (i == sets.length) {
                done = true;
                break;
            }
            indices[i]++;
            if (indices[i] < sets[i].length) {
                break;
            }
            indices[i] = 0;
            i++;
        }
        return tuple;
    }

}
