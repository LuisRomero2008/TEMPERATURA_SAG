package com.example.temperatura_sag.Model;

public class Temperatura {
    public String Doc;
    public String Grado;
    public String Fecha;

    public Temperatura(String doc, String grado, String fecha) {
        Doc = doc;
        Grado = grado;
        Fecha = fecha;
    }

    public String getDoc() {
        return Doc;
    }

    public void setDoc(String doc) {
        Doc = doc;
    }

    public String getGrado() {
        return Grado;
    }

    public void setGrado(String grado) {
        Grado = grado;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }
}
