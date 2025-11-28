/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

/**
 *
 * @author francisco-alejandro
 */
public class ConcurrentProgram extends Program {

    public ConcurrentProgram(FilesInfo filesInfo, int numFrags, AnalysisInfo analysisInfo) {
        super(filesInfo, numFrags, analysisInfo);
    }

    @Override
    protected void frags() {
        Manager manager = new Manager(this);
        manager.start();
    }
    
}
