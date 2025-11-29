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
public class SegmentedTimePeriod {

    /**
     * Tiempo donde incia el periodo
     */
    private LocalDateTime start;
    
    /**
     * Tiempo donde termina el periodo
     */
    private LocalDateTime end;
    
    /**
     * Duración del segmento
     */
    private Duration segmentDuration;
    
    /**
     * Cantidad de segmentos en los que se divide el periodo
     */
    private int numSegments;

    /**
     * Construye un nuevo periodo
     * @param start es el tiempo donde incia el periodo
     * @param end es el tiempo donde termina el periodo
     * @param numSegments es el número de segmentos del periodo
     */
    public SegmentedTimePeriod(LocalDateTime start, LocalDateTime end, int numSegments) {
        this.start = start;
        this.end = end;
        this.segmentDuration = Duration.between(start, end);
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

}
