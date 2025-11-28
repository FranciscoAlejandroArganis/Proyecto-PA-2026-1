/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.util.Pair;
import com.pa.util.Counter;
import com.pa.multithread.AbstractManager;
import com.pa.table.Cell;
import com.pa.table.Row;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Manager para el procesamiento en el programa concurrente
 * @author francisco-alejandro
 */
public class Manager extends AbstractManager {

    private Program program;
    private Worker[] workers;
    private int numCollected;
    private boolean firstPassComplete;

    /**
     * Construye un nuevo manager
     * @param program es el programa en donde se usa el manager
     */
    public Manager(Program program) {
        this.program = program;
        workers = new Worker[program.getNumFrags()];
        threads = new Thread[workers.length];
        useVirtualThreads = true;
        numCollected = 0;
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(this, i);
        }
        firstPassComplete = false;
    }

    /**
     * Regresa el worker con el identificador especificado
     * @param id es el identificador del worker
     * @return el worker con identificador <code>id</code>
     */
    @Override
    public Worker getWorker(int id) {
        return workers[id];
    }

    /**
     * Se manda a llamar cuando un worker termina su participación y ya no entregará más trabajo, aún cuando lo tuviera asignado
     * @param id el identificador del worker que deja de participar
     */
    @Override
    public void dismiss(int id) {
        synchronized (this) {
            program.setNumFrags(program.getNumFrags() - 1);
        }
    }

    /**
     * Se manda a llamar cuando un worker solicita trabajo al manager. Asigna trabajo al worker con identificador <code>id</code>.
     * @param id el identificador del worker que hace la solicitud.
     * @return <code>true</code> si y solo si se asignó trabajo
     */
    @Override
    public boolean assign(int id) {
        Worker worker = workers[id];
        switch (worker.getCurrentTask()) {
            case Worker.Task.FIRST_PASS:
                return true;
            case Worker.Task.SECOND_PASS:
                try {
                    synchronized (this) {
                        while (numCollected < program.getNumFrags()) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    Reviews.LOGGER.warning("Hilo " + id + " interruptido");
                    dismiss(id);
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * Se manda a llamar cuando un worker reporta que ha completado su trabajo asignado
     * @param id el identificador del worker que hace el reporte
     */ 
    @Override
    public void collect(int id) {
        Worker worker = workers[id];
        switch (worker.getCurrentTask()) {
            case Worker.Task.FIRST_PASS:
                synchronized (this) {
                    Counter<Cell>[] counters = worker.getUniqueCounters();
                    for (int j = 0; j < counters.length; j++) {
                        for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                            program.getUniqueCounters()[j].increase(entry.getKey(), entry.getValue());
                        }
                    }
                    LocalDateTime time = worker.getMinTime();
                    if (program.getMinTime() == null || (time != null && time.isBefore(program.getMinTime()))) {
                        program.setMinTime(time);
                    }
                    time = worker.getMaxTime();
                    if (program.getMaxTime() == null || (time != null && time.isAfter(program.getMaxTime()))) {
                        program.setMaxTime(time);
                    }
                    numCollected++;
                    if (numCollected >= program.getNumFrags()) {
                        if (!firstPassComplete) {
                            program.buildGroups();
                            firstPassComplete = true;
                        }
                        notifyAll();
                    }
                }
                worker.setCurrentTask(Worker.Task.SECOND_PASS);
                break;
            case Worker.Task.SECOND_PASS:
                synchronized (this) {
                    Counter<Pair<Row, Integer>> counter = worker.getBoolCounter();
                    for (Row rep : program.getGroupReps()) {
                        for (int j = 0; j < program.getAnalysisInfo().getNumSegments(); j++) {
                            Pair<Row, Integer> pair = new Pair<>(rep, j);
                            long count = counter.getCount(pair);
                            program.getBoolCounter().increase(pair, count);
                        }
                    }
                }
                worker.setCurrentTask(Worker.Task.DONE);
                break;
            default:
        }

    }

    /**
     * Regresa el programa del manager
     * @return el programa del manager
     */
    public Program getProgram() {
        return program;
    }

}
