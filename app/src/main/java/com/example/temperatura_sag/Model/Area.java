package com.example.temperatura_sag.Model;

public class Area {

    String Id;
    String Nombre;

    public Area(String id, String nombre) {
        Id = id;
        Nombre = nombre;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    @Override
    public String toString() {
        return Nombre;
    }
}
