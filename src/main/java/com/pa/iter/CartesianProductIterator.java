/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.iter;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author francisco-alejandro
 */
public class CartesianProductIterator<T> implements Iterator<T[]> {

    private T[][] sets;
    private int[] indices;
    private boolean done;

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

    @Override
    public boolean hasNext() {
        return !done;
    }

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
