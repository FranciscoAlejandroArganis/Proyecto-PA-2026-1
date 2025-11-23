/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.stats;

/**
 *
 * @author francisco-alejandro
 */
public class SLRResult {

    private double slope;
    private double intercept;
    private double correlationCoefficient;

    public SLRResult(double slope, double intercept, double correlationCoefficient) {
        this.slope = slope;
        this.intercept = intercept;
        this.correlationCoefficient = correlationCoefficient;
    }

    public double getSlope() {
        return slope;
    }

    public double getIntercept() {
        return intercept;
    }

    public double getCorrelationCoefficient() {
        return correlationCoefficient;
    }

    public double getCoefficientOfDetermination() {
        return correlationCoefficient * correlationCoefficient;
    }

}
