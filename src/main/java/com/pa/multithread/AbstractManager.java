/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.multithread;

/**
 *
 * @author francisco-alejandro
 */
public abstract class AbstractManager {
    
    protected boolean useVirtualThreads;
    protected Thread[] threads;
    
    public void start() {
        Thread.Builder builder = useVirtualThreads ? Thread.ofVirtual() : Thread.ofPlatform();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = builder.start(getWorker(i));
        }
        for (int i = 0; i < threads.length; i++) {
            try{
                threads[i].join();
            } catch (InterruptedException e){
                dismiss(i);
            }
            
        }
    }
    
    public abstract AbstractWorker getWorker(int id);
    
    public abstract boolean assign(int id);
    
    public abstract void collect(int id);
    
    public abstract void dismiss(int id);
    
}
