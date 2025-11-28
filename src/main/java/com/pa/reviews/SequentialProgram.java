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
import com.pa.query.SelectFromWhere;
import com.pa.table.Cell;
import com.pa.table.Row;
import com.pa.time.SegmentedTimePeriod;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Representa un programa que procesa los datos de forma secuencial
 * @author francisco-alejandro
 */
public class SequentialProgram extends Program {

    /**
     * Construye un nuevo programa secuencial
     * @param filesInfo contiene la información de los archivos a utilizar
     * @param numFrags es la cantidad deseada de fragmentos a generar
     * @param analysisInfo contiene la información de los parámetros usados para el análisis de los datos
     */
    public SequentialProgram(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        super(filesInfo, numFrags, analysisInfo);
    }

    /**
     * Procesa los fragmentos de forma secuencial
     */
    @Override
    protected void frags() {
        firstPass();
        buildGroups();
        secondPass();
    }

    /**
     * Realiza la primer pasada.
     * Filtra los datos según la consulta del usuario.
     * Cuenta valores únicos de las columnas en <code>colsToTally<c/ode>.
     * Encuentra el tiempo mínimo y máximo de la columna en el índice <code>timeColIndex</code>
     */
    private void firstPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            File filteredFrag = new File(filesInfo.getTempDir(), filesInfo.getFiltered().getName() + i);
            Tally tally = new Tally(analysisInfo.getColsToTally());
            MinMaxTime minMaxTime = new MinMaxTime(analysisInfo.getTimeColIndex());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filteredFrag))) {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                Filter filter = new Filter(userQuery, writer);
                processor.process(filter.andThen(tally).andThen(minMaxTime));
            } catch (IOException e) {

            }
            Counter<Cell>[] counters = tally.getCounters();
            for (int j = 0; j < counters.length; j++) {
                for (Map.Entry<Cell, Long> entry : counters[j].getMap().entrySet()) {
                    this.uniqueCounters[j].increase(entry.getKey(), entry.getValue());
                }
            }
            LocalDateTime time = minMaxTime.getMin();
            if (minTime == null || (time != null && time.isBefore(minTime))) {
                minTime = time;
            }
            time = minMaxTime.getMax();
            if (maxTime == null || (time != null && time.isAfter(maxTime))) {
                maxTime = time;
            }
        }
    }

    /**
     * Realiza la segunda pasada.
     * Cuenta la cantidad de filas tales que el valor de su columna en el índce <code>boolColIndex</code> es verdadero, por cada uno de los grupos formados.
     */
    private void secondPass() {
        for (int i = 0; i < numFrags; i++) {
            File datasetFrag = new File(filesInfo.getTempDir(), filesInfo.getDataset().getName() + i);
            SelectFromWhere selGroupCols = new SelectFromWhere(
                analysisInfo.getColsToTally(),
                analysisInfo.getDataHeader(),
                row -> row.getCell(analysisInfo.getBoolColIndex()).getBool()
            );
            SegmentedTimePeriod period = new SegmentedTimePeriod(
                minTime,
                maxTime,
                analysisInfo.getNumSegments()
            );
            CountByTime countByTime = new CountByTime(
                selGroupCols,
                period,
                groupReps,
                analysisInfo.getTimeColIndex()
            );
            try {
                RowProcessor processor = new RowProcessor(datasetFrag, analysisInfo.getDataHeader());
                processor.process(countByTime);
            } catch (IOException e) {
            }
            for (Row rep : groupReps) {
                for (int j = 0; j < analysisInfo.getNumSegments(); j++) {
                    Pair<Row, Integer> pair = new Pair<>(rep, j);
                    long count = countByTime.getCounter().getCount(pair);
                    boolCounter.increase(pair, count);
                }
            }
        }
    }

}
