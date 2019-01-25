/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mittbant.cocochatserver;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author admin
 */
public class SocketDeliverClientList extends Thread {
    
    Hashtable clientes;
    
   public Connection conn;
   public String url="jdbc:mysql://localhost:3306/cocochat?autoReconnect=true&useSSL=false";
   public String user="root";
   public String password="123456";
   
   
   public PreparedStatement stmt=null;
   public  ResultSet rs=null;
   public String query;    
   
    
    public SocketDeliverClientList(Hashtable clientes){
    
        this.clientes=clientes;
        initialize();
        
        
    }
    
    public ResultSet todosClientes() 
    {
        openConnection();
        
        try {
            stmt=conn.prepareStatement("SELECT * FROM User");
        } catch (SQLException e) {
                    System.out.println("Failed to establish connection ");
//                    System.out.println("SQLException: " + e.getMessage());
//                    System.out.println("SQLState: " + e.getSQLState());
//                    System.out.println("VendorError: " + e.getErrorCode());
        }
        
        try {
            return stmt.executeQuery();
        } catch (SQLException e) {
                    System.out.println("Failed to establish connection ");
//                    System.out.println("SQLException: " + e.getMessage());
//                    System.out.println("SQLState: " + e.getSQLState());
//                    System.out.println("VendorError: " + e.getErrorCode());
                    return null;
        }
        
        
    }
    
    public String obtenClientesConectados(){
        
        
        
        Enumeration clientnames;
        clientnames= clientes.keys();
        
        String clientes="";
        
        while(clientnames.hasMoreElements()){
            clientes+= (String) clientnames.nextElement() + "|" ;
            
        }
        
        
        
        return clientes;
        
    }
    
    public String generaListaClientes(){
        
        String clientesconectados=this.obtenClientesConectados();
        
        ResultSet todosclientes;
        
        
            
            todosclientes = this.todosClientes();
            
            if(todosclientes==null){
                System.out.println("Lista de dotos los clientes esta vacia !");
                closeConnection();
                return null;
            }
                

        
        String listaclientes="";
        
        try {
            
            while(todosclientes.next()){
                
                String cliente= todosclientes.getString("name");
                
                if(clientesconectados.contains(cliente)){
                    listaclientes+=cliente + ":C";
                }
                else{
                    listaclientes+=cliente + ":D";
                }
                listaclientes+="|";
                
            }
            
            //System.out.println("\t \t " +listaclientes);
            closeConnection();
            return listaclientes;
        } catch (SQLException e) {
//                    System.out.println("Failed to establish connection ");
//                    System.out.println("SQLException: " + e.getMessage());
//                    System.out.println("SQLState: " + e.getSQLState());
//                    System.out.println("VendorError: " + e.getErrorCode());
             closeConnection();
            return null;
        }
        
        
        
        
        
        
        
    }
    
    public void run(){
        
        String listadeclientes="";
        
        Enumeration clientnames;
        
        String clientesconectados;
        
        while (true){
        
        clientnames= clientes.keys();
        
        clientesconectados=this.obtenClientesConectados();
        
        
        String[] listadeentrega= clientesconectados.split(Pattern.quote("|"));
        
        
        Socket clienteenturno;
        
        
        
        listadeclientes= this.generaListaClientes();
        
            //System.out.println("Lista de clientes" + listadeclientes);
        
        
        
            for (int i = 0; i < listadeentrega.length; i++) {
                
                clienteenturno=(Socket) clientes.get(listadeentrega[i]);
                
            try {
                clienteenturno.getOutputStream().write(listadeclientes.getBytes(StandardCharsets.UTF_8));
                //System.out.println("Lista mandada a " + listadeentrega[i]);
            } catch (IOException ex) {
                System.out.println("No se pudo mandar la lista de clientes al cliente " + listadeentrega[i]);
                System.out.println("Eliminando a " + listadeentrega[i] + " de la lista, se asume desconexion");
                
                clientes.remove(listadeentrega[i]);
            }
                
                
                
            }
            
            try {
                Thread.sleep(1000);
                System.out.println(this.generaListaClientes());
            } catch (InterruptedException ex) {
                //Logger.getLogger(SocketDeliverClientList.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        
    
        }
        
    }
    
    
    
    public void openConnection(){
        
        try
                {
                    //System.out.println("Attemting to establish connection");
                    conn=DriverManager.getConnection(url,user,password);
                    //System.out.println("Connection established: success !");

                }
                catch(SQLException e)
                {
                    System.out.println("Failed to establish connection ");
//                    System.out.println("SQLException: " + e.getMessage());
//                    System.out.println("SQLState: " + e.getSQLState());
//                    System.out.println("VendorError: " + e.getErrorCode());
                 }
        
    }
    
    public void closeConnection(){
        try {
            this.conn.close();
        } catch (SQLException ex) {
            //Logger.getLogger(SocketDeliverClientList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public  void initialize(){
      
   
    
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            //System.out.println("MySQL driver loaded !");
//            try
//                {
//                    //System.out.println("Attemting to establish connection");
//                    conn=DriverManager.getConnection(url,user,password);
//                    //System.out.println("Connection established: success !");
//
//                }
//                catch(SQLException e)
//                {
//                    System.out.println("Failed to establish connection ");
//                    System.out.println("SQLException: " + e.getMessage());
//                    System.out.println("SQLState: " + e.getSQLState());
//                    System.out.println("VendorError: " + e.getErrorCode());
//                 }
        }
        catch(Exception e){
            System.out.println("Something went wrong when attemting to load the driver");
            //System.out.println(e.getMessage());
            
        }
                    
    
  
    }
    
    
}
