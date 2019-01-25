/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mittbant.cocochatserver;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.ServerSocket;
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

/**
 *
 * @author admin
 */
public class SocketThread implements Runnable {
    
   public Connection conn;
   public String url="jdbc:mysql://localhost:3306/cocochat";
   public String user="root";
   public String password="123456";
   
   
   public PreparedStatement stmt=null;
   public  ResultSet rs=null;
   public String query;    
   
    public Hashtable<String, Socket> clientes;
    
    public String nombre;
    
    
    private Thread thread;
    private Socket client;
    byte[] datos = new byte[100];
    byte[] bytesmensaje = new byte[100];
    
    public String nombreusuario;
    
    Gson gson ;
    
    
    public SocketThread(Socket s, Hashtable connectedclients)
    {
        
        clientes = connectedclients;
        
        client=s;
        
        thread = new Thread(this);
        
        thread.start();
        
        nombre=null;
        
        gson=  new Gson();
    }
    
    @Override
    public void run() {
        
        //Bytes de respuesta que se le mandan al cliente en para su autenticacion
        byte[] respuesta = null;
        
        
        //Initaliza una conexion con la base de datos
        initialize();
        
        
        //Obtiene el nombre que el cliente mandad, su nombre de usuario
        nombre = getNombre();
        
        //Si el login coincide en la base de datos
        if(login(nombre)){
        
            //Mensaje de respuesta en bytes, codificacion UTF_8
            respuesta="Estas registrado".getBytes(StandardCharsets.UTF_8);
            
            //Agregamos el cliente con su socket a la HashTable
            clientes.put(nombre, client);
            
            nombreusuario=nombre;
            
            
            
            //Mandamos la respuesta al cliente
            try {
                
                client.getOutputStream().write(respuesta);
            } catch (IOException ex) {
                Logger.getLogger(SocketThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            imprimeListaDeClientes(clientes);
            
            
            while(true){
            
                Gson gson= new Gson();
                
                
            try{
                
                
                
                bytesmensaje[0]=-44;
                
                client.getInputStream().read(bytesmensaje);
                
                if(bytesmensaje[0]!=-44){
                    
                    String recibido= new String(bytesmensaje,StandardCharsets.UTF_8);
                    recibido=recibido.trim();
                    
                    
                    //Friend request
                    if(recibido.contains("|FR|"))
                    {
                        
                        System.out.println("Recibi solicitud de amistad !! " );
                        System.out.println(recibido);
                        System.out.println("");
                        
                        recibido=recibido.replace("|FR|", "");
                        
                        FriendRequest request = gson.fromJson(recibido, FriendRequest.class);
                        
                        if(!request.exists()){
                            
                            request.store();
                            
                            request.send(clientes.get(request.getTo()));
                            
                        }
                        
                        
                        
                        
                        
                    }
                    //Friend request acepted
                    else if(recibido.contains("|FRA|"))
                    {
                        
                        System.out.println("Reception de acpetacion de soliciud !!");
                        
                        System.out.println(recibido);
                        
                        System.out.println("Cool !");
                        
                        
                        recibido=recibido.replace("|FRA|", "");
                        
                        FriendRequest acceptedrequest = gson.fromJson(recibido, FriendRequest.class);
                        
                        acceptedrequest.state='A';
                        
                        String accepterrequestjson ="|FRA|"  + gson.toJson(acceptedrequest);
                        
                        System.out.println("Sending FR accepted response to " + acceptedrequest.from);
                        
                        System.out.println("Sent :" + accepterrequestjson);
                        
                        clientes.get(acceptedrequest.from).getOutputStream().write(accepterrequestjson.getBytes(StandardCharsets.UTF_8));
                        
                        
                        
                    }
                    //Freind request declined !
                    else if(recibido.contains("|FRD|"))
                    {
                        
                    }
                    else {
                    sendMessage(recibido);
                    }
                
                }
                bytesmensaje= new byte[100];
                bytesmensaje[0]=-44;
                
            } catch(IOException ex){
                
            }
            
            
            
            }
            
            
        }
        else{
            
            //Cuando no esta registrado
            respuesta="No Estas registrado".getBytes(StandardCharsets.UTF_8);
            
            try {
                client.getOutputStream().write(respuesta);
            } catch (IOException ex) {
                Logger.getLogger(SocketThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
            
 
    }
    
    public void sendMessage(String mensaje){
        
                
        
                //String mensaje= new String(bytesmensaje,StandardCharsets.UTF_8);
                
                //mensaje=mensaje.trim();
                System.out.println(nombreusuario + " dijo " + mensaje);
                
                Mensaje m = gson.fromJson(mensaje.trim(), Mensaje.class);
                
                    System.out.println("\n \n \n \n \n");
                
                    System.out.println(" " + m.getOrigen() + " ' " + m.getMensaje() + " ' a " + m.getDestinatario()  );
                    
                    Socket destinatario = clientes.get(m.getDestinatario());
                    
                    System.out.println(destinatario);
                    
                    System.out.println("\n \n \n \n \n");
                    
       try {
           destinatario.getOutputStream().write(m.getJson().getBytes(StandardCharsets.UTF_8));
       } catch (IOException ex) {
           Logger.getLogger(SocketThread.class.getName()).log(Level.SEVERE, null, ex);
       }
                    
                
        
        
    }
    
    
    public void imprimeListaDeClientes(Hashtable clientes){
        Enumeration clientnames;
       
        clientnames= clientes.keys();
        
        System.out.println(" \t \t Lista de clientes conectados  ");
        while(clientnames.hasMoreElements()){
            
            String name = (String) clientnames.nextElement();
            System.out.println("\t \t Connected "+ name + "  " +  client.getLocalAddress() + " " + clientes.get(name));
            
        }
    }
    
    public void mandaListaClientesConectados(Hashtable clientes){
        Enumeration clientnames;
        clientnames= clientes.keys();
        String clientesconectados="";
        while(clientnames.hasMoreElements()){
            
            clientesconectados += (String) clientnames.nextElement()+"|";
            
        }
       try {
           this.client.getOutputStream().write(clientesconectados.getBytes(StandardCharsets.UTF_8));
       } catch (IOException ex) {
           Logger.getLogger(SocketThread.class.getName()).log(Level.SEVERE, null, ex);
       }
        System.out.println("Lista de clientes : \n" + clientesconectados + "\n ");
        
        
    }
    
    
    
    public String getNombre(){
        
        
         //System.out.println("Cliente conectado :"+client.getInetAddress());
        
        try {
            
            
            
            client.getInputStream().read( datos );
            
            String message= new String(datos,"UTF-8");
            
            //System.out.println("Nombre de usuario  " +  message.trim());
            
            return message.trim();
            
        } catch (IOException ex) {
            Logger.getLogger(SocketThread.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        
       
    }
    
    public Boolean login(String usuario){
        
        query="select name from user where name=? ";
        //System.out.println(query);
 
            try {
                stmt= conn.prepareStatement(query);
                
                stmt.setString(1, usuario);
                //stmt.setString(2,password);                
                rs= stmt.executeQuery();
                
                if(rs.next())
                {
                    System.out.println("Usuario " + usuario + " esta registrado ");
                    return true;
                    
                }
                //System.out.println(date.toString()+"// Username "+email+" failed login !");
                    System.out.println("Usuario " + usuario  + " no esta registrado ");
                return false;
            } catch (SQLException ex) {
                System.out.println("Coulnt prepared statement to db !");
                //Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
     
    }
    
    
    
    
    
     public  void initialize(){
      
   
    
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            //System.out.println("MySQL driver loaded !");
            try
                {
                    //System.out.println("Attemting to establish connection");
                    conn=DriverManager.getConnection(url,user,password);
                    //System.out.println("Connection established: success !");

                }
                catch(SQLException e)
                {
                    System.out.println("Failed to establish connection ");
                    System.out.println("SQLException: " + e.getMessage());
                    System.out.println("SQLState: " + e.getSQLState());
                    System.out.println("VendorError: " + e.getErrorCode());
                 }
        }
        catch(Exception e){
            System.out.println("Something went wrong when attemting to load the driver");
            System.out.println(e.getMessage());
            
        }
                    
    
  
    }

    

    private void sendRequest(FriendRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
