/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.multithread;

/**
 *
 * @author francisco-alejandro
 */
public abstract class AbstractWorker implements Runnable {
    
    protected AbstractManager manager;
    protected int id;
    
    @Override
    public void run(){
        while(manager.assign(id)){
            doWork();
            manager.collect(id);
        }
    }
    
    public int getId(){
        return id;
    }
    
    public abstract void doWork();
    
}
