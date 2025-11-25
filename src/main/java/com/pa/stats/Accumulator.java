/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.stats;

/**
 *
 * @author francisco-alejandro
 */
public class Accumulator {

    private double[] single;
    private double[] pair;
    private int numData;

    /**
     * Construye un nuevo acumulador para una cantidad fija de variables
     *
     * @param numVars es la cantidad de variables del acumulador
     */
    public Accumulator(int numVars) {
        single = new double[numVars];
        pair = new double[numVars * (numVars + 1) >> 1];
    }

    /**
     * Agrega al acumulador un nuevo dato por cada variable
     *
     * @param values es un arreglo tal que values[i] es el valor agregado de la
     * variable i
     */
    public void add(double[] values) {
        for (int i = 0; i < single.length; i++) {
            single[i] += values[i];
        }
        for (int i = 0; i < single.length; i++) {
            for (int j = i; j < single.length; j++) {
                int k = i * single.length - (i * (i + 1) >> 1) + j;
                pair[k] += values[i] * values[j];
            }
        }
        numData++;
    }

    /**
     * Regresa la cantidad de datos agregados al acumulador
     *
     * @return la cantidad de datos de cada variable
     */
    public int getNumData() {
        return numData;
    }

    /**
     * Asigna la cantidad de datos agregados al acumulador
     * @param numData 
     */
    public void setNumData(int numData) {
        this.numData = numData;
    }

    /**
     * Regresa el arreglo con los valores de las sumas acumuladas de cada variable
     * @return el arreglo de las sumas acumuladas
     */
    public double[] getSingle() {
        return single;
    }

    /**
     * Regresa el arreglo con los valores de las sumas acumuladas de productos de pares variables
     * @return el arreglo de las sumas acumuladas de productos por pares
     */
    public double[] getPair() {
        return pair;
    }

    /**
     * Regresa el total acumulado de los datos agregados al acumulador de una
     * variable
     *
     * @param i es el índice de la variable
     * @return la suma de los valores de la variable i
     */
    public double sum(int i) {
        return single[i];
    }

    /**
     * Regresa el total acumulado del producto por pares de los datos agregados
     * al acumulador de dos variables
     *
     * @param i es el índice de la primera variable
     * @param j es el índice de la segunda variable
     * @return la suma de los productos de pares de valores de las variables i y
     * j
     */
    public double sum(int i, int j) {
        int k = i * single.length - (i * (i + 1) >> 1) + j;
        return pair[k];
    }

    /**
     * Elimina todos los datos agregados al acumulador
     */
    public void clear() {
        for (int i = 0; i < single.length; i++) {
            single[i] = 0;
        }
        for (int k = 0; k < pair.length; k++) {
            pair[k] = 0;
        }
        numData = 0;
    }

    /**
     * Regresa la media de los datos agregados al acumulador de una variable
     *
     * @param i es el índice de la variable
     * @return el promedio de los datos de la variable i
     */
    public double mean(int i) {
        return sum(i) / numData;
    }

    /**
     * Regresa la varianza de los datos agregados al acumulador de una variable
     *
     * @param i es el índice de la variable
     * @return la varianza muestral corregida de los datos de la variable i
     */
    public double variance(int i) {
        return (sum(i, i) - sum(i) * sum(i) / numData) / (numData - 1);
    }

    /**
     * Regresa la desviación estándar de los datos agregados al acumulador de
     * una variable
     *
     * @param i es el índice de la variable
     * @return la desviación estándar muestral corregida de los datos de la
     * variable i
     */
    public double standardDeviation(int i) {
        return Math.sqrt(variance(i));
    }

    /**
     * Regresa una regresión lineal simple para los datos agregados al
     * acumulador de dos variable
     *
     * @param i es el índice de la variable independiente
     * @param j es el índice de la variable dependiente
     * @return el resultado de una regresión lineal simple para los datos de las
     * variables i y j
     */
    public SLRResult simpleLinearRegression(int i, int j) {
        double numerator = sum(i, j) * numData - sum(i) * sum(j);
        double denominator = sum(i, i) * numData - sum(i) * sum(i);
        double slope = numerator / denominator;
        numerator = sum(j) * sum(i, i) - sum(i) * sum(i, j);
        double intercept = numerator / denominator;
        numerator = sum(i, j) * numData - sum(i) * sum(j);
        denominator = Math.sqrt(denominator * (sum(j, j) * numData - sum(j) * sum(j)));
        double correlationCoefficient = numerator / denominator;
        return new SLRResult(slope, intercept, correlationCoefficient);
    }

}
