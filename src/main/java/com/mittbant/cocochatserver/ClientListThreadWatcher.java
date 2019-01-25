/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mittbant.cocochatserver;

import java.net.Socket;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class ClientListThreadWatcher extends Thread{
    
    public Hashtable<String, Socket> clientes;
    
    public ClientListThreadWatcher(Hashtable clientes){
        this.clientes=clientes;
        start();
        
    }
    
    public void run(){
        while(true){
            SocketDeliverClientList deliverer;
            deliverer= new SocketDeliverClientList(this.clientes);
            deliverer.start();
            if( !deliverer.isAlive() ){
                
                deliverer= new SocketDeliverClientList(this.clientes);
                
                deliverer.start();
                
                
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientListThreadWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
