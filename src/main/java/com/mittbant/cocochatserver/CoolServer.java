/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mittbant.cocochatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author admin
 */
public class CoolServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
            ServerSocket serve=null;
            
            Socket client=null;
    
            Hashtable<String, Socket> clientes;
            
            clientes = new Hashtable();
            
            ClientListThreadWatcher watcher= new ClientListThreadWatcher(clientes);
            
            
            serve= new ServerSocket(5001);
        
        while(true){    
            
            
        try {
           
            
            client=serve.accept();
            SocketThread sh= new SocketThread(client, clientes);
            
            
            
            
            
        } catch (IOException ex) {   
            Logger.getLogger(CoolServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    };
    
}
