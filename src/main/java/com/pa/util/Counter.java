/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un contador de objetos únicos
 * @author francisco-alejandro
 * @param <T> es el tipo de los objetos que se cuentan
 */
public class Counter<T> {

    private Map<T, Long> map;

    /**
     * Construye un nuevo contador con cuentas inciales en 0
     */
    public Counter() {
        map = new HashMap<>();
    }

    /**
     * Incrementa en 1 la cuenta de <code>key</code>
     * @param key el objeto del que se aumenta la cuenta
     */
    public void increase(T key) {
        long count = map.getOrDefault(key, 0l) + 1;
        map.put(key, count);
    }

    /**
     * Incrementa en <coed>increment</code> la cuenta de <code>key</code>
     * @param key el objeto del que se aumenta la cuenta
     * @param increment es la cantidad que se suma al la cuenta actual
     */
    public void increase(T key, long increment) {
        long count = map.getOrDefault(key, 0l) + increment;
        map.put(key, count);
    }

    /**
     * Reinicia las cuentas de todos los objetos a 0
     */
    public void clear() {
        map.clear();
    }

    /**
     * Regresa la cuenta de <code>key</code>
     * @param key el objeto del que se regresa la cuenta
     * @return la cuenta de <code>key</code>
     */
    public long getCount(T key) {
        return map.getOrDefault(key, 0l);
    }

    /**
     * Asigna la cuenta de <code>key</code>
     * @param key el objeto al que se asigna la cuenta
     * @param count la nueva cuenta de <code>key</code>
     */
    public void setCount(T key, long count) {
        map.put(key, count);
    }

    /**
     * Regresa el map de objetos a cuentas
     * @return el mapa de objetos a cuentas
     */
    public Map<T, Long> getMap() {
        return map;
    }

}
