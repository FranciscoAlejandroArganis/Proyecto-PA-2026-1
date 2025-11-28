/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.util.Pair;
import com.pa.util.Counter;
import com.pa.consumers.CountByTime;
import com.pa.consumers.Filter;
import com.pa.consumers.MinMaxTime;
import com.pa.consumers.Tally;
import com.pa.multithread.AbstractWorker;
import com.pa.query.SelectFromWhere;
import com.pa.stats.Accumulator;
import com.pa.table.Cell;
import com.pa.table.Row;
import com.pa.time.SegmentedTimePeriod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Representa un worker para el procesamiento en el programa concurrente
 * @author francisco-alejandro
 */
public class Worker extends AbstractWorker {

    /**
     * Tipo de tarea que realiza el worker
     */
    public enum Task {
        FIRST_PASS,
        SECOND_PASS,
        DONE
    }

    private Task currentTask;
    private File datasetFrag;
    private File filteredFrag;
    private Program program;
    private Tally tally;
    private MinMaxTime minMaxTime;
    private CountByTime countByTime;
    private Accumulator acc;

    /**
     * Construye un nuevo worker
     *
     * @param manager el manager del worker
     * @param id el identificador del worker
     */
    public Worker(Manager manager, int id) {
        this.manager = manager;
        this.program = manager.getProgram();
        this.datasetFrag = new File(program.getFilesInfo().getTempDir(), program.getFilesInfo().getDataset().getName() + id);
        this.filteredFrag = new File(program.getFilesInfo().getTempDir(), program.getFilesInfo().getFiltered().getName() + id);
        this.id = id;
        currentTask = Task.FIRST_PASS;
    }

    /**
     * Realiza el trabajo actualmente asignado por el manager
     */
    @Override
    public void doWork() {
        switch (currentTask) {
            case Task.FIRST_PASS:
                firstPass();
                break;
            case Task.SECOND_PASS:
                secondPass();
                break;
            default:
                return;
        }
    }

    /**
     * Realiza la primer pasada, local al fragmento que procesa el worker.
     * Filtra los datos según la consulta del usuario.
     * Cuenta valores únicos de las columnas en <code>colsToTally<c/ode>.
     * Encuentra el tiempo mínimo y máximo de la columna en el índice <code>timeColIndex</code>
     */
    private void firstPass() {
        tally = new Tally(program.getAnalysisInfo().getColsToTally());
        minMaxTime = new MinMaxTime(program.analysisInfo.getTimeColIndex());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            Filter filter = new Filter(program.getUserQuery(), writer);
            processor.process(filter.andThen(tally).andThen(minMaxTime));
        } catch (IOException e) {

        }
    }

    /**
     * Realiza la segunda pasada, local al fragmento que procesa el worker.
     * Cuenta la cantidad de filas tales que el valor de su columna en el índce <code>boolColIndex</code> es verdadero, por cada uno de los grupos formados.
     */
    private void secondPass() {
        SelectFromWhere selGroupCols = new SelectFromWhere(
            program.getAnalysisInfo().getColsToTally(),
            program.getAnalysisInfo().getDataHeader(),
            row -> row.getCell(program.getAnalysisInfo().getBoolColIndex()).getBool()
        );
        SegmentedTimePeriod period = new SegmentedTimePeriod(
            program.getMinTime(),
            program.getMaxTime(),
            program.getAnalysisInfo().getNumSegments()
        );
        countByTime = new CountByTime(
            selGroupCols,
            period, program.getGroupReps(),
            program.getAnalysisInfo().getTimeColIndex()
        );
        try {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            processor.process(countByTime);
        } catch (IOException e) {

        }
    }

    /**
     * Regresa la tarea actual del worker
     * @return la tarea actual del worker
     */
    public Task getCurrentTask() {
        return currentTask;
    }

    /**
     * Asigna la tarea del worker
     * @param currentTask la nueva tarea del worker
     */
    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    /**
     * Regresa el arreglo de contadores de valores únicos por columna, local al fragmento que procesa el worker
     * @return el arreglo de contadores de valores únicos por columna, local al fragmento que procesa el worker
     */
    public Counter<Cell>[] getUniqueCounters() {
        return tally.getCounters();
    }

    /**
     * Regresa el contador de las filas verdaderas por cada grupo, local al fragmento que procesa el worker
     * @return el contador de las filas verdaderas por cada grupo, local al fragmento que procesa el worker
     */
    public Counter<Pair<Row, Integer>> getBoolCounter() {
        return countByTime.getCounter();
    }

    /**
     * Regresa el tiempo mínimo encontrado, local al fragmento que procesa el worker
     * @return el tiempo mínimo encontrado, local al fragmento que procesa el worker
     */
    public LocalDateTime getMinTime() {
        return minMaxTime.getMin();
    }

    /**
     * Regresa el tiempo máximo encontrado, local al fragmento que procesa el worker
     * @return el tiempo máximo encontrado, local al fragmento que procesa el worker
     */
    public LocalDateTime getMaxTime() {
        return minMaxTime.getMax();
    }

}
