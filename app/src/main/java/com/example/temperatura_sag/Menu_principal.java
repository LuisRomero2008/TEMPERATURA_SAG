package com.example.temperatura_sag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temperatura_sag.Common.Network;
import com.google.firebase.auth.FirebaseAuth;

public class Menu_principal extends AppCompatActivity {
    Button btningreso,btnsalida;
    private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu_principal);
        Auth = FirebaseAuth.getInstance();

        btnsalida=findViewById(R.id.btnTempsalida);

    }
    public void TI(View view){
        Intent i = new Intent(this, Registro_Temperatura.class);
        i.putExtra("Tipo","ingreso");
        startActivity(i);
    }
    public void TS(View view){
        Intent i = new Intent(this, Registro_Temperatura.class);
        i.putExtra("Tipo","salida");
        startActivity(i);
    }

    public void DJ(View view){
        Intent deju = new Intent(this, Declaracion_jurada.class);
        startActivity(deju);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(Network.isDataConnectionAvailable(this)){
        }else{
            Toast.makeText(this, "La aplicacion se cerrara", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                public void run() {

                    finishAffinity();
                }
            }, 2000);
        }
    }

    public void exit(View view){
        try {
            Toast.makeText(this, "La aplicacion se cerrara", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                public void run() {
                    Auth.signOut();
                    finishAffinity();
                }
            }, 2000);
        }catch (Exception vEx){
            Toast.makeText(this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
