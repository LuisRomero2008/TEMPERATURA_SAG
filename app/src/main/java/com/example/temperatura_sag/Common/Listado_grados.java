package com.example.temperatura_sag.Common;
import com.example.temperatura_sag.Extra.Grados_reporte;
import com.example.temperatura_sag.Extra.TemperaturaBean;
import com.google.firebase.database.DataSnapshot;

import java.util.*;

public class Listado_grados {
    public Grados_reporte DataToList(DataSnapshot valores){
        Grados_reporte vResponse=new Grados_reporte();
        TemperaturaBean vElement=new TemperaturaBean();
        List<TemperaturaBean> myList=new ArrayList<>();
        try {
            for(DataSnapshot ds:valores.getChildren()){
                vElement.setDocumento(ds.child("Documento").getValue().toString());
                vElement.setFecha((ds.child("Fecha").getValue().toString()));
                vElement.setGrados(Double.parseDouble(ds.child("Grados").getValue().toString()));
                myList.add(vElement);
            }
            vResponse.setValores(myList);

        }catch (Exception e){
            String vMessage=e.getMessage();
            System.out.println(vMessage);
        }
        //System.out.println("Listado Grados"+vElement.getGrados());
        return vResponse;
    }
}
