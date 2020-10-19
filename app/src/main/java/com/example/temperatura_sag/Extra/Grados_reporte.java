package com.example.temperatura_sag.Extra;

import java.util.Date;
import java.util.List;

public class Grados_reporte{
        private String Codigo; //DNI
        private List<TemperaturaBean> Valores;

    public List<TemperaturaBean> getValores() {
        return Valores;
    }

    public void setValores(List<TemperaturaBean> valores) {
        Valores = valores;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }
}
