package com.example.temperatura_sag.Extra;

import java.util.Date;

public class TemperaturaBean {
    private String Documento;
    private String Fecha;
    private double Grados;

    public String getFecha() {
        return Fecha;
    }
    public void setFecha(String fecha) {
        Fecha = fecha;
    }
    public double getGrados() {
        return Grados;
    }
    public void setGrados(double grados) {
        Grados = grados;
    }
    public String getDocumento() {
        return Documento;
    }
    public void setDocumento(String documento) {
        Documento = documento;
    }
}
