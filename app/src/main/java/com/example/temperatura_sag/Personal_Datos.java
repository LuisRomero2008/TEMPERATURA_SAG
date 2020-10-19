package com.example.temperatura_sag;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Personal_Datos extends AppCompatActivity {
    TextView etnombre,etapellido,etdireccion,etfono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal__datos);

        etnombre=findViewById(R.id.txtname);
        etapellido=findViewById(R.id.txtlastname);
        etdireccion=findViewById(R.id.txtdirection);
        etfono=findViewById(R.id.txtphone);

        String Nombre = getIntent().getStringExtra("Nombre");
        String Apellido = getIntent().getStringExtra("Apellido");
        String Direccion = getIntent().getStringExtra("Direccion");
        String Telefono = getIntent().getStringExtra("Telefono");

        etnombre.setText(Nombre);
        etapellido.setText(Apellido);
        etdireccion.setText(Direccion);
        etfono.setText(Telefono);
    }
    public void rewind(View view){
        finish();
    }
}
