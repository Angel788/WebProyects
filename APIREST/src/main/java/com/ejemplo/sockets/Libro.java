package com.ejemplo.sockets;

import java.io.Serializable;

public class Libro implements Serializable{
    private static final long serialVersionUID = 1L;
    int id,existencias;
    String titulo;
    String caracteristicas;
    String imagen;
    public Libro(int id,String tittulo, String caracteristicas, int existencias){
        this.id = id;
        this.titulo = tittulo;
        this.caracteristicas = caracteristicas;
        this.existencias = existencias;
    }
    @Override
    public String toString() {
        return titulo+". Caracteristicas: "+caracteristicas+", ID "+id;
    }    
}
