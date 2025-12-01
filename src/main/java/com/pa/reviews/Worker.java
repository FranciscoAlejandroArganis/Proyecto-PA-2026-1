/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.reviews.consumers.DifferenceTimeGroup;
import com.pa.reviews.consumers.Filter;
import com.pa.reviews.consumers.MinMaxTimeGroup;
import com.pa.reviews.consumers.Tally;
import com.pa.multithread.AbstractWorker;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Representa un worker para el procesamiento en el programa concurrente
 *
 * @author francisco-alejandro
 */
public class Worker extends AbstractWorker {

    /**
     * Número de la pasada que tiene asignada actualmente el worker
     */
    private int pass;

    /**
     * Fragmento del conjunto de datos que procesa el worker
     */
    private File datasetFrag;

    /**
     * Fragmento del archivo filtrado que genera el worker
     */
    private File filteredFrag;

    /**
     * Programa donde se usa el worker
     */
    private Program program;

    /**
     * Operación local de contar valores únicos aplicada por el worker
     */
    private Tally tally;

    /**
     * Operación local de buscar tiempos mínimo y máximo aplicada por el worker
     */
    private MinMaxTimeGroup minMaxTime;

    /**
     * Operación local de acumular diferencias por segmentos de tiempo aplicada
     * por el worker
     */
    private DifferenceTimeGroup diffTime;

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
    }

    /**
     * Realiza el trabajo actualmente asignado por el manager
     */
    @Override
    public void doWork() {
        switch (pass) {
            case 1:
                firstPass();
                break;
            case 2:
                secondPass();
                break;
            case 3:
                thirdPass();
                break;
            default:
        }
    }

    /**
     * Realiza la primer pasada, local al fragmento que procesa el worker.
     * Filtra los datos según la consulta del usuario. Cuenta valores únicos de
     * las columnas en <code>groupColumns<c/ode>.
     */
    private void firstPass() {
        tally = new Tally(program.getAnalysisInfo().getGroupColumns());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            Filter filter = new Filter(program.getUserQuery(), writer);
            processor.process(filter.andThen(tally));
        } catch (IOException e) {
            Reviews.LOGGER.severe("No se tiene acceso a los archivos temporales: " + datasetFrag.getName() + ", " + filteredFrag.getName());
        }
    }

    /**
     * Realiza la segunda pasada, local al fragmento que procesa el worker.
     * Encuentra el mínimo y máximo de la columna en <code>timeColIndex</code>,
     * por cada grupo.
     */
    private void secondPass() {
        minMaxTime = new MinMaxTimeGroup(program.getGroupQuery(), program.getAnalysisInfo().getTimeColIndex());
        try {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            processor.process(minMaxTime);
        } catch (IOException e) {
            Reviews.LOGGER.severe("No se puede leer el archivo temporal: " + datasetFrag.getName());
        }
    }

    /**
     * Realiza la tercer pasada, local al fragmento que procesa el worker.
     * Construye la serie de tiempo de la cantidad de filas tales que su valor
     * en la columna <code>boolColIndex</code> es verdadero menos las filas en
     * las que es falso, por cada uno de los grupos formados.
     */
    private void thirdPass() {
        diffTime = new DifferenceTimeGroup(
                program.getGroupQuery(),
                program.getPeriodMap(),
                program.getAnalysisInfo().getTimeColIndex(),
                program.getAnalysisInfo().getBoolColIndex()
        );
        try {
            RowProcessor processor = new RowProcessor(datasetFrag, program.getAnalysisInfo().getDataHeader());
            processor.process(diffTime);
        } catch (IOException e) {
            Reviews.LOGGER.severe("No se puede leer el archivo temporal: " + datasetFrag.getName());
        }
    }

    /**
     * Regresa la operación local de contar valores únicos
     *
     * @return la operación local de contar valores únicos
     */
    public Tally getTally() {
        return tally;
    }

    /**
     * Regresa la operación local de buscar tiempos mínimo y máximo
     *
     * @return la operación local de buscar tiempos mínimo y máximo
     */
    public MinMaxTimeGroup getMinMaxTime() {
        return minMaxTime;
    }

    /**
     * Regresa la operación local de acumular diferencias por segmentos de
     * tiempo
     *
     * @return la operación local de acumular diferencias por segmentos de
     * tiempo
     */
    public DifferenceTimeGroup getDiffTime() {
        return diffTime;
    }

    /**
     * Regresa el número de pasada que tiene asignada actualmente el worker
     *
     * @return el número de pasada que tiene asignada actualmente el worker
     */
    public int getPass() {
        return pass;
    }

    /**
     * Asigna el número de pasada del worker
     *
     * @param pass el nuevo número de pasada del worker
     */
    public void setPass(int pass) {
        this.pass = pass;
    }

}
