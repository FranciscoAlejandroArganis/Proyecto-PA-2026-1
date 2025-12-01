/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.util.Counter;
import com.pa.multithread.AbstractManager;
import com.pa.table.Cell;
import com.pa.table.Row;
import com.pa.time.SegmentedPeriod;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Manager para el procesamiento en el programa concurrente
 *
 * @author francisco-alejandro
 */
public class Manager extends AbstractManager {

    /**
     * Programa en el que se usa el manager
     */
    private Program program;

    /**
     * Arreglo de workers
     */
    private Worker[] workers;

    /**
     * Cantidad de workers que han reportado sus resultados en la pasada actual
     */
    private int numCollected;

    /**
     * Número de la pasada actual que está administrando el worker
     */
    private int pass;

    /**
     * Construye un nuevo manager
     *
     * @param program es el programa en donde se usa el manager
     */
    public Manager(Program program) {
        this.program = program;
        workers = new Worker[program.getNumFrags()];
        threads = new Thread[workers.length];
        useVirtualThreads = true;
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(this, i);
        }
        pass = 1;
    }

    /**
     * Regresa el worker con el identificador especificado
     *
     * @param id es el identificador del worker
     * @return el worker con identificador <code>id</code>
     */
    @Override
    public Worker getWorker(int id) {
        return workers[id];
    }

    /**
     * Se manda a llamar cuando un worker termina su participación y ya no
     * entregará más trabajo, aún cuando lo tuviera asignado
     *
     * @param id el identificador del worker que deja de participar
     */
    @Override
    public void dismiss(int id) {
        Reviews.LOGGER.warning("Hilo " + id + " interruptido");
        synchronized (this) {
            program.setNumFrags(program.getNumFrags() - 1);
        }
    }

    /**
     * Se manda a llamar cuando un worker solicita trabajo al manager. Asigna
     * trabajo al worker con identificador <code>id</code>.
     *
     * @param id el identificador del worker que hace la solicitud.
     * @return <code>true</code> si y solo si se asignó trabajo
     */
    @Override
    public boolean assign(int id) {
        Worker worker = workers[id];
        barrier(id);
        if (pass < 4) {
            worker.setPass(pass);
            return true;
        }
        return false;
    }

    /**
     * Se manda a llamar cuando un worker reporta que ha completado su trabajo
     * asignado
     *
     * @param id el identificador del worker que hace el reporte
     */
    @Override
    public void collect(int id) {
        Worker worker = workers[id];
        synchronized (this) {
            switch (pass) {
                case 1:
                    firstPass(worker);
                    break;
                case 2:
                    secondPass(worker);
                    break;
                case 3:
                    thirdPass(worker);
                    break;
                default:
            }
            checkTransition();
        }
    }

    /**
     * Hace que el worker que solicita trabajo espere hasta que termina la
     * pasada actual
     *
     * @param id el identificador del worker que solicta trabajo pero debe
     * esperar
     */
    private void barrier(int id) {
        try {
            synchronized (this) {
                while (workers[id].getPass() >= pass && numCollected < program.getNumFrags()) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            dismiss(id);
        }
    }

    /**
     * Revisa si se ha terminado la pasada actual y se puede hacer una
     * transición a la siguiente pasada
     */
    private void checkTransition() {
        numCollected++;
        if (numCollected >= program.getNumFrags()) {
            if (pass == 1) {
                program.buildGroups();
            }
            numCollected = 0;
            pass++;
            notifyAll();
        }
    }

    /**
     * Recolecta los resultados de un worker en la primer pasada
     *
     * @param worker el trabajador que reporta completo su trabajo asignado
     */
    private void firstPass(Worker worker) {
        Counter<Cell>[] counters = worker.getTally().getCounters();
        for (int j = 0; j < counters.length; j++) {
            for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                program.getColumnCounters()[j].increase(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Recolecta los resultados de un worker en la segunda pasada
     *
     * @param worker el trabajador que reporta completo su trabajo asignado
     */
    private void secondPass(Worker worker) {
        Map<Row, SegmentedPeriod> map = worker.getMinMaxTime().getMap();
        for (Map.Entry<Row, SegmentedPeriod> entry : map.entrySet()) {
            Row rep = entry.getKey();
            LocalDateTime time = entry.getValue().getStart();
            SegmentedPeriod period = program.getPeriodMap().get(rep);
            if (time.isBefore(period.getStart())) {
                period.setStart(time);
            }
            time = entry.getValue().getEnd();
            if (time.isAfter(period.getEnd())) {
                period.setEnd(time);
            }
        }
    }

    /**
     * Recolecta los resultados de un worker en la tercer pasada
     *
     * @param worker el trabajador que reporta completo su trabajo asignado
     */
    private void thirdPass(Worker worker) {
        Map<Row, Long[]> map = worker.getDiffTime().getMap();
        for (Map.Entry<Row, Long[]> entry : map.entrySet()) {
            Row rep = entry.getKey();
            Long[] timeSeries = program.getSeriesMap().get(rep);
            for (int j = 0; j < timeSeries.length; j++) {
                timeSeries[j] += entry.getValue()[j];
            }
        }
    }

    /**
     * Regresa el programa del manager
     *
     * @return el programa del manager
     */
    public Program getProgram() {
        return program;
    }

}
