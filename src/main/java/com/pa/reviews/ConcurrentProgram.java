/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.reviews;

import com.pa.table.Header;

/**
 *
 * @author francisco-alejandro
 */
public class ConcurrentProgram extends Program {

    public ConcurrentProgram(String dataset, String tempDir, String filtered, String results, Header colsToTally, int numFrags) {
        super(dataset, tempDir, filtered, results, colsToTally, numFrags);
    }

    @Override
    protected void frags() {
        Manager manager = new Manager(this);
        manager.start();
    }
    
}
