/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.multithread;

/**
 * Representa un worker en el patrón manager-workers
 * @author francisco-alejandro
 */
public abstract class AbstractWorker implements Runnable {
    
    protected AbstractManager manager;
    protected int id;
    
    /**
     * Ejecuta el código del worker
     */
    @Override
    public void run(){
        while(manager.assign(id)){
            doWork();
            manager.collect(id);
        }
    }
    
    /**
     * Regresa el identificador del worker
     * @return el identificador del worker
     */
    public int getId(){
        return id;
    }
    
    /**
     * Realiza el trabajo actualmente asignado por el manager
     */
    public abstract void doWork();
    
}
