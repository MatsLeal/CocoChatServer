/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mittbant.cocochatserver;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class FriendRequest {
    
    String from;
    String to;
    char state;

    public FriendRequest(String from, String to, char state) {
        this.from = from;
        this.to = to;
        this.state = state;
    }
    
    
    

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public boolean exists() {
        
    Connection conn=getConnection();
    
    if(conn==null){
        System.out.println("Connection was null fuck uck ");
        return false;
    }
        
    
        try {
            
            PreparedStatement stmt =conn.prepareStatement("select * from Friends where ID_User=? and ID_Friend=?");
            
            System.out.println("Prepared statment" );
            System.out.println("select * from Friends where ID_User=? and ID_Friend=?");
            
            stmt.setString(1,this.to);
            stmt.setString(2,this.from);
            
            if(stmt.executeQuery().next()){
                System.out.println("The request already existed ");
                conn.close();
                return true;
            }
            System.out.println("Request exists not ");
            
                
            
        } catch (SQLException ex) {
            Logger.getLogger(FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("\nExeption found \n");
            
        }
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    return false;
   
        
    }

    public void store() {
        Connection conn= getConnection();
        try {
            PreparedStatement stmt=conn.prepareStatement(" insert into Friends values (?,?,?)");
            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setString(3, "P");
            
            stmt.executeUpdate();
            
            
            System.out.println("Request saved ok !!");
            
            return;
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
    
    
    public  Connection  getConnection(){
      
   
    
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn=null;
            String url="jdbc:mysql://localhost:3306/cocochat";
            String user="root";
            String password="123456";
            //System.out.println("MySQL driver loaded !");
            try
                {
                    //System.out.println("Attemting to establish connection");
                    conn=DriverManager.getConnection(url,user,password);
                    //System.out.println("Connection established: success !");
                    
                    return conn;

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
                    
        return null;
  
    }

    void send(Socket to) {
        Gson gson= new Gson();
        try {
            to.getOutputStream().write( 
                    ("|FR|"
                            +
                     gson.toJson(this))
                    .getBytes(StandardCharsets.UTF_8));
            System.out.println("Request sent ok !");
        } catch (IOException ex) {
            System.out.println("Could not send requesr");
            Logger.getLogger(FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
