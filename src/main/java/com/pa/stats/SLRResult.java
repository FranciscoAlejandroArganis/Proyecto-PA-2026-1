/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.stats;

/**
 * Representa el resultado de una regresión lineal simple
 * @author francisco-alejandro
 */
public class SLRResult {

    private double slope;
    private double intercept;
    private double correlationCoefficient;

    /**
     * Construye un nuevo resultado de regresión lineal simple
     * @param slope es la pendiende de la recta ajustada
     * @param intercept es la ordenada al origen de la recta ajustada
     * @param correlationCoefficient es el coeficiente de correlación para la recta ajustada
     */
    public SLRResult(double slope, double intercept, double correlationCoefficient) {
        this.slope = slope;
        this.intercept = intercept;
        this.correlationCoefficient = correlationCoefficient;
    }

    /**
     * Regresa la pendiende
     * @return la pendiente
     */
    public double getSlope() {
        return slope;
    }

    /**
     * Regresa la ordenada al origen
     * @return la ordenada al origen
     */
    public double getIntercept() {
        return intercept;
    }

    /**
     * Regresa el coeficiente de correlación
     * @return el coeficiente de correlación
     */
    public double getCorrelationCoefficient() {
        return correlationCoefficient;
    }

    /**
     * Regresa el coeficiente de determinación
     * @return el coeficiente de determinación
     */
    public double getCoefficientOfDetermination() {
        return correlationCoefficient * correlationCoefficient;
    }

}
