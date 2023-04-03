package com.example.logingexampleback4app.Modelos;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("DatosUsuario")
public class DatosUsuario extends ParseObject {

    private String id;
    private String nombUsuario;
    private int caidasDia;
    private int caidasSemana;

    public String getId() {
        return getObjectId();
    }

    public String getNombUsuario() {
        return getString("nombreUsuario");
    }

    public void setNombUsuario(String nombUsuario) {
        put("nombreUsuario", nombUsuario);
    }

    public int getCaidasDia() {
        return getInt("Caida_Dia");
    }

    public void setCaidasDia(int caidasDia) {
        put("Caida_Dia", caidasDia);
    }

    public int getCaidasSemana() {
        return getInt("Caida_Semana");
    }

    public void setCaidasSemana(int caidasSemana) {
        put("Caida_Semana", caidasSemana);
    }
}
