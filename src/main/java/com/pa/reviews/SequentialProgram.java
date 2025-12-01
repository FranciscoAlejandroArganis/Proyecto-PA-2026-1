/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.util.Counter;
import com.pa.reviews.consumers.DifferenceTimeGroup;
import com.pa.reviews.consumers.Filter;
import com.pa.reviews.consumers.MinMaxTimeGroup;
import com.pa.reviews.consumers.Tally;
import com.pa.table.Cell;
import com.pa.table.Row;
import com.pa.time.SegmentedPeriod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * Representa un programa que procesa los datos de forma secuencial
 *
 * @author francisco-alejandro
 */
public class SequentialProgram extends Program {

    /**
     * Construye un nuevo programa secuencial
     *
     * @param filesInfo contiene la información de los archivos a utilizar
     * @param numFrags es la cantidad deseada de fragmentos a generar
     * @param analysisInfo contiene la información de los parámetros usados para
     * el análisis de los datos
     */
    public SequentialProgram(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        super(filesInfo, numFrags, analysisInfo);
        System.out.println("Implementación: Secuencial");
    }

    /**
     * Procesa los fragmentos de forma secuencial
     */
    @Override
    protected void frags() {
        firstPass();
        buildGroups();
        secondPass();
        thirdPass();
    }

    /**
     * Realiza la primer pasada. Filtra los datos según la consulta del usuario.
     * Cuenta valores únicos de las columnas en <code>groupColumns<c/ode>.
     */
    private void firstPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            File filteredFrag = new File(filesInfo.getTempDir(), filesInfo.getFiltered().getName() + i);
            Tally tally = new Tally(analysisInfo.getGroupColumns());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                Filter filter = new Filter(userQuery, writer);
                processor.process(filter.andThen(tally));
            } catch (IOException e) {
                Reviews.LOGGER.severe("No se tiene acceso a los archivos temporales: " + datasetFrag.getName() + ", " + filteredFrag.getName());
            }
            Counter<Cell>[] counters = tally.getCounters();
            for (int j = 0; j < counters.length; j++) {
                for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                    columnCounters[j].increase(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * Realiza la segunda pasada. Encuentra el mínimo y máximo de la columna en
     * <code>timeColIndex</code>, por cada grupo.
     */
    private void secondPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            MinMaxTimeGroup minMaxTime = new MinMaxTimeGroup(groupQuery, analysisInfo.getTimeColIndex());
            try {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                processor.process(minMaxTime);
            } catch (IOException e) {
                Reviews.LOGGER.severe("No se puede leer el archivo temporal: " + datasetFrag.getName());
            }
            Map<Row, SegmentedPeriod> map = minMaxTime.getMap();
            for (Map.Entry<Row, SegmentedPeriod> entry : map.entrySet()) {
                Row rep = entry.getKey();
                LocalDateTime time = entry.getValue().getStart();
                SegmentedPeriod period = periodMap.get(rep);
                if (time.isBefore(period.getStart())) {
                    period.setStart(time);
                }
                time = entry.getValue().getEnd();
                if (time.isAfter(period.getEnd())) {
                    period.setEnd(time);
                }
            }
        }
    }

    /**
     * Realiza la tercera pasada. Construye la serie de tiempo de la cantidad de
     * filas tales que su valor en la columna <code>boolColIndex</code> es
     * verdadero menos las filas en las que es falso, por cada uno de los grupos
     * formados.
     */
    private void thirdPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            DifferenceTimeGroup diffTime = new DifferenceTimeGroup(
                    groupQuery,
                    periodMap,
                    analysisInfo.getTimeColIndex(),
                    analysisInfo.getBoolColIndex()
            );
            try {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                processor.process(diffTime);
            } catch (IOException e) {
                Reviews.LOGGER.severe("No se puede leer el archivo temporal: " + datasetFrag.getName());
            }
            Map<Row, Long[]> map = diffTime.getMap();
            for (Map.Entry<Row, Long[]> entry : map.entrySet()) {
                Row rep = entry.getKey();
                Long[] timeSeries = seriesMap.get(rep);
                for (int j = 0; j < timeSeries.length; j++) {
                    timeSeries[j] += entry.getValue()[j];
                }
            }
        }
    }

}
