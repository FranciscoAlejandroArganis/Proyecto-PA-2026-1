/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.multithread.AbstractManager;
import com.pa.stats.Accumulator;
import com.pa.table.Cell;
import java.util.Map;

/**
 *
 * @author francisco-alejandro
 */
public class Manager extends AbstractManager {

    private Program program;
    private Worker[] workers;
    private Accumulator acc;

    public Manager(Program program) {
        this.program = program;
        workers = new Worker[program.getNumFrags()];
        threads = new Thread[workers.length];
        useVirtualThreads = true;
        acc = new Accumulator(12);
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(this, i);
        }
    }

    @Override
    public Worker getWorker(int id) {
        return workers[id];
    }

    @Override
    public void dismiss(int id) {
    }

    @Override
    public void partition() {
    }

    @Override
    public boolean assign(int id) {
        switch (workers[id].getCurrentTask()) {
            case Worker.Task.FILTER_AND_TALLY:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void collect(int id) {
        synchronized (this) {
            UniqueCounter<Cell>[] counters = workers[id].getCounters();
            for (int j = 0; j < counters.length; j++) {
                for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                    program.getCounters()[j].increase(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    
    public Program getProgram() {
        return program;
    }

}
