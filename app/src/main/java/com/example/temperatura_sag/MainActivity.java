package com.example.temperatura_sag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.temperatura_sag.Common.Network;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private EditText etuser,etpss;

    private String email="";
    private String pass="";
    private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etuser=findViewById(R.id.txtUser);
        etpss=findViewById(R.id.txtPass);

        Auth = FirebaseAuth.getInstance();
    }
    public void Validar(){
        Auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent mp=new Intent(MainActivity.this,Menu_principal.class);
                    startActivity(mp);
                }else{
                    Toast.makeText(MainActivity.this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void Ingresar(View view){
        try{
            email=etuser.getText().toString();
            pass=etpss.getText().toString();

            if(email.isEmpty()||pass.isEmpty()){
                Toast.makeText(this, "Ingrese los datos", Toast.LENGTH_SHORT).show();
            }else{
                Validar();
            }
        }catch (Exception vEx){
            Toast.makeText(this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Auth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this,Menu_principal.class));
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(Network.isDataConnectionAvailable(this)){
        }else{
            Toast.makeText(this, "Sin acceso a Internet", Toast.LENGTH_SHORT).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                public void run() {

                    finishAffinity();
                }
            }, 2000);
        }
    }
}
