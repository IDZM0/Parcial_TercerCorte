package com.example.proyecsql;

import java.io.Serializable;

public class Nota implements Serializable {
    private int id;
    private String texto;

    public Nota(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}