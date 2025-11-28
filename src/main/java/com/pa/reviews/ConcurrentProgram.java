/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

/**
 * Representa un programa que procesa los datos de forma concurrente
 * @author francisco-alejandro
 */
public class ConcurrentProgram extends Program {

    /**
     * Construye un nuevo programa concurrente
     * @param filesInfo contiene la información de los archivos a utilizar
     * @param numFrags es la cantidad deseada de fragmentos a generar
     * @param analysisInfo contiene la información de los parámetros usados para el análisis de los datos
     */
    public ConcurrentProgram(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        super(filesInfo, numFrags, analysisInfo);
    }

    /**
     * Procesa los fragmentos de forma concurrente
     */
    @Override
    protected void frags() {
        Manager manager = new Manager(this);
        manager.start();
    }
    
}
