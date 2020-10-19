package com.example.temperatura_sag;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temperatura_sag.Common.Network;
import com.example.temperatura_sag.Model.Area;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registro_Personal extends AppCompatActivity {
    private Spinner sparea;
    private String valarea="";
    private String valor="";

    private EditText etdni,etnombre,etapellido,etdir,ettele;


    private DatabaseReference mDatabase;
    private DatabaseReference mDatabasePersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registro__personal);
        sparea=findViewById(R.id.spAREA);

        etdni=findViewById(R.id.txtdni);
        etnombre=findViewById(R.id.txtnombre);
        etapellido=findViewById(R.id.txtapellido);

        etdir=findViewById(R.id.txtdireccion);
        ettele=findViewById(R.id.txttele);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabasePersonal= FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_personal));
        LoadArea();
    }

    public void Guardar(View view){
        final String DNI=etdni.getText().toString();
        final String Nombre=etnombre.getText().toString();
        final String Apellido=etapellido.getText().toString();
        final String idarea= valor;
        final String direccion= etdir.getText().toString();
        final String telefono= ettele.getText().toString();
        final int[] Area = {0};
        final Query q= mDatabasePersonal.orderByChild(getString(R.string.campo_DNI)).equalTo(DNI);
        if(DNI.length()==8 && Nombre.length()!=0 && Apellido.length()!=0){
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Area[0] = Integer.parseInt(ds.child("Area").getValue().toString());
                            if (Area[0] > 0) {
                                Toast.makeText(Registro_Personal.this, "Ya existe un personal con ese documento", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(Area[0]==0){
                            Map<String, Object> datosPersonal = new HashMap<>();
                            datosPersonal.put("DNI", DNI);
                            datosPersonal.put("Nombres", Nombre);
                            datosPersonal.put("Apellidos", Apellido);
                            datosPersonal.put("Area", idarea);
                            datosPersonal.put("Direccion", direccion);
                            datosPersonal.put("Telefono", telefono);
                            mDatabase.child("Personal").push().setValue(datosPersonal);
                            Toast.makeText(Registro_Personal.this, "Se registro correctamente", Toast.LENGTH_SHORT).show();
                            etdni.setText("");
                            etnombre.setText("");
                            etapellido.setText("");
                            etdir.setText("");
                            ettele.setText("");
                        }
                    }catch(Exception vEx){
                        Toast.makeText(Registro_Personal.this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show();
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

    public void LoadArea(){
        try {
        final List<Area> area=new ArrayList<>();
        mDatabase.child("Area").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        String id =ds.getKey();
                      String nombre=ds.child("Nombre").getValue().toString();
                      area.add(new Area(id,nombre));
                    }
                    ArrayAdapter<Area> arrayAdapter=new ArrayAdapter<>(Registro_Personal.this,android.R.layout.simple_dropdown_item_1line,area);
                    sparea.setAdapter(arrayAdapter);
                    sparea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            valarea= parent.getItemAtPosition(position).toString();
                            //id se alamacena en la variable valor
                            valor=area.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }catch(Exception vEx){
            Toast.makeText(this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
