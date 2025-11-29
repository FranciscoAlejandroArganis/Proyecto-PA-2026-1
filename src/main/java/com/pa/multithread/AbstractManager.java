/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pa.multithread;

/**
 * Representa un manager en el patrón manager-workers
 * @author francisco-alejandro
 */
public abstract class AbstractManager {
    
    /**
     * Indica si se usan hilos virtuales o de plataforma
     */
    protected boolean useVirtualThreads;
    
    /**
     * Arreglo de los hilos correspondientes a los workers
     */
    protected Thread[] threads;
    
    /**
     * Inicia la ejecución del trabajo y espera hasta que todos los hilos terminan
     */
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
    
    /**
     * Regresa el worker con el identificador especificado
     * @param id es el identificador del worker
     * @return el worker con identificador <code>id</code>
     */
    public abstract AbstractWorker getWorker(int id);
    
    /**
     * Se manda a llamar cuando un worker solicita trabajo al manager. Asigna trabajo al worker con identificador <code>id</code>.
     * @param id el identificador del worker que hace la solicitud.
     * @return <code>true</code> si y solo si se asignó trabajo
     */
    public abstract boolean assign(int id);
    
    /**
     * Se manda a llamar cuando un worker reporta que ha completado su trabajo asignado
     * @param id el identificador del worker que hace el reporte
     */    
    public abstract void collect(int id);
    
    /**
     * Se manda a llamar cuando un worker termina su participación y ya no entregará más trabajo, aún cuando lo tuviera asignado
     * @param id el identificador del worker que deja de participar
     */
    public abstract void dismiss(int id);
    
}
