/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mittbant.cocochatserver;

/**
 *
 * @author admin
 */
public class Mensaje {
    
    private String origen;
    private String mensaje;
    private String destinatario;

    public Mensaje(String json) {
        
        
           
    }

    public Mensaje() {
       this.origen = "";
        this.mensaje = "";
        this.destinatario = ""; 
    }

    public String getJson(){
        
        return "{" +
                "origen : '" + this.origen + "' , " +
                "mensaje : '" + this.mensaje +  "' , " +
                "destinatario : '" + this.destinatario +  "' " 
                + "}";
        
    }
    
    
    
    
    
    
    
    
    public Mensaje(String origen, String mensaje, String destinatario) {
        this.origen = origen;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
    }
    
    

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
    
    
    
}
