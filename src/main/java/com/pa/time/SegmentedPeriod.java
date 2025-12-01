/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.time;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Representa un periodo de tiempo divido en segmentos consecutivos
 * @author francisco-alejandro
 */
public class SegmentedPeriod {

    /**
     * Tiempo donde incia el periodo
     */
    private LocalDateTime start;
    
    /**
     * Tiempo donde termina el periodo
     */
    private LocalDateTime end;
    
    /**
     * Duración de cada segmento del periodo
     */
    private Duration segmentDuration;
    
    /**
     * Cantidad de segmentos en los que se divide el periodo
     */
    private int numSegments;
    
    /**
     * Construye un nuevo periodo sin segmentos y sin duración
     */
    public SegmentedPeriod() {
        start = LocalDateTime.MAX;
        end = LocalDateTime.MIN;
        segmentDuration = Duration.between(start, end);
    }

    /**
     * Construye un nuevo periodo con los segmentos especificados y sin duración
     * @param numSegments es el número de segmentos en los que está dividio el periodo
     */
    public SegmentedPeriod(int numSegments) {
        this();
        this.numSegments = numSegments;
    }
    
    /**
     * Construye un nuevo periodo con tiempos de inicio, fin y número de segmentos especificados
     * @param start es el tiempo donde inicia el periodo
     * @param end es el tiempo donde termina el periodo
     * @param numSegments es el número de segmentos en los que está dividio el periodo
     */
    public SegmentedPeriod(LocalDateTime start, LocalDateTime end, int numSegments){
        this.start = start;
        this.end = end;
        segmentDuration = Duration.between(start, end);
        this.numSegments = numSegments;
    }

    /**
     * Determina el índice del segmento al que pertence un tiempo
     * @param time es el tiempo del cual se busca determinar su periodo
     * @return el índice del segmento al que pertenece <code>time</code> o -1 si está fuera del periodo
     */
    public int segmentOf(LocalDateTime time) {
        if (time.equals(end)){
            return numSegments - 1;
        }
        if (time.isBefore(start) || time.isAfter(end)) {
            return -1;
        }
        double sinceStart = Duration.between(start, time).toSeconds();
        return (int) (numSegments * sinceStart / segmentDuration.toSeconds());
    }

    /**
     * Regresa el tiempo donde inicia el periodo
     * @return el tiempo donde inicia el periodo
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Asigna el tiempo donde incia el periodo
     * @param start el nuevo tiempo donde inicia el periodo
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
        segmentDuration = Duration.between(start, end);
    }

    /**
     * Regresa el tiempo donde termina el periodo
     * @return el tiempo donde termina el periodo
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Asigna el tiempo donde termina el periodo
     * @param end el nuevo tiempo donde termina el periodo
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
        segmentDuration = Duration.between(start, end);
    }

    /**
     * Regresa el número de segmentos del periodo
     * @return el número de segmentos del periodo
     */
    public int getNumSegments() {
        return numSegments;
    }

    /**
     * Asigna el número de segmentos del periodo
     * @param numSegments el nuevo número de segmentos del periodo
     */
    public void setNumSegments(int numSegments) {
        this.numSegments = numSegments;
    }

}
