/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.time;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author francisco-alejandro
 */
public class SegmentedTimePeriod {

    private LocalDateTime start;
    private LocalDateTime end;
    private Duration segmentDuration;
    private int numSegments;

    public SegmentedTimePeriod(LocalDateTime start, LocalDateTime end, int numSegments) {
        this.start = start;
        this.end = end;
        this.segmentDuration = Duration.between(start, end);
        this.numSegments = numSegments;
    }

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
