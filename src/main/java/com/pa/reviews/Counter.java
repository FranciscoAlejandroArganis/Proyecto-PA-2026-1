/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author francisco-alejandro
 */
public class Counter<T> {

    private Map<T, Long> map;

    public Counter() {
        map = new HashMap<>();
    }

    public void increase(T key) {
        long count = map.getOrDefault(key, 0l) + 1;
        map.put(key, count);
    }

    public void increase(T key, long increment) {
        long count = map.getOrDefault(key, 0l) + increment;
        map.put(key, count);
    }

    public void clear() {
        map.clear();
    }

    public long getCount(T key) {
        return map.getOrDefault(key, 0l);
    }

    public void setCount(T key, long count) {
        map.put(key, count);
    }

    public Map<T, Long> getMap() {
        return map;
    }

}
