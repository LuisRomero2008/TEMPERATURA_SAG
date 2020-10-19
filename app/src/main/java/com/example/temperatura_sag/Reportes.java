package com.example.temperatura_sag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Reportes extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        /*ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);*/
    }
    public void Reporte_fechas(View view){
        Intent fec = new Intent(this, Reporte_Fechas.class);
        startActivity(fec);
    }
    public void Declaracion_jurada(View view){
        Intent fec = new Intent(this, Declaracion_jurada.class);
        startActivity(fec);
    }
}
