package com.example.temperatura_sag;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Registro_Temperatura extends AppCompatActivity {
    private EditText etdni,ettemperatura;
    private TextView txtnombre,txtapellido;
    private DatabaseReference mdatabaseper;
    private DatabaseReference mdatabasetemp;
    private DatabaseReference mdatabase;

    String sEmail,sPssw;

    String pauth,phost,pport,ptls;

    String dauth,dhost,dport,dtls;

    ArrayList<String> mails=new ArrayList<>();

    DatabaseReference sendmail;
    DatabaseReference pendmail;
    DatabaseReference dendmail;
    DatabaseReference fendmail;
    String grados;
    TextView valor;
    Button btnregistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registro__temperatura);

        etdni=findViewById(R.id.txtDNI);
        ettemperatura=findViewById(R.id.txttemperatura);

        txtnombre=findViewById(R.id.lblnombre);
        txtapellido=findViewById(R.id.lblapellido);
        btnregistro=findViewById(R.id.btnregistrar);
        valor=findViewById(R.id.idtipo);

        sendmail= FirebaseDatabase.getInstance().getReference("SEmail");
        pendmail= FirebaseDatabase.getInstance().getReference("PEmail");
        dendmail= FirebaseDatabase.getInstance().getReference("DEmail");
        fendmail= FirebaseDatabase.getInstance().getReference("FEmail");

        Query qs= sendmail.orderByKey().equalTo("sEmail");
        qs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    sEmail  =ds.child("email").getValue().toString();
                    sPssw   =ds.child("pss").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query qp=pendmail.orderByKey().equalTo("Propierties");
        qp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    pauth=ds.child("auth").getValue().toString();
                    phost=ds.child("host").getValue().toString();
                    pport=ds.child("port").getValue().toString();
                    ptls=ds.child("tls").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Query qd=dendmail.orderByKey().equalTo("Data");
        qd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    dauth=ds.child("auth").getValue().toString();
                    dhost=ds.child("host").getValue().toString();
                    dport=ds.child("port").getValue().toString();
                    dtls=ds.child("tls").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query qf=fendmail.orderByChild("email");
        qf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if((ds.child("email").getValue())!=null){
                        String email=ds.child("email").getValue().toString();
                        mails.add(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mdatabase=FirebaseDatabase.getInstance().getReference();
        mdatabaseper= FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_personal));
        mdatabasetemp=FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_temperatura));
        etdni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String DNI=etdni.getText().toString();
                Query q= mdatabaseper.orderByChild(getString(R.string.campo_DNI)).equalTo(DNI);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try{
                        if (DNI.length()!=8){
                            txtnombre.setText("");
                            txtapellido.setText("");
                        }else{
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                               String NOMBRE=ds.child("Nombres").getValue().toString();
                               String APELLIDO=ds.child("Apellidos").getValue().toString();
                               txtnombre.setText(NOMBRE);
                               txtapellido.setText(APELLIDO);
                            }
                            if(txtnombre.getText()=="" || txtapellido.getText()==""){
                            Toast.makeText(Registro_Temperatura.this, "El personal no se encuentra registrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                        }catch (Exception vEx){
                            Toast.makeText(Registro_Temperatura.this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final String Tipo = getIntent().getStringExtra("Tipo");
        valor.setText(Tipo);
    }

    public void Registro(View view){
        String value= valor.getText().toString();
        if(value.equals("ingreso")){

            TemperaturaI();
        }
        if(value.equals("salida")){
            TemperaturaS();
        }
        Salir();
    }

    public void TemperaturaS(){
        final String dni=etdni.getText().toString();
        grados=ettemperatura.getText().toString();
        Date date = new Date();
        final String fecha=new SimpleDateFormat("dd-MM-yy").format(date);
        final DatabaseReference gs = mdatabase.child("Temperatura/"+dni+"-"+fecha);
        final Query q=mdatabasetemp.orderByKey().equalTo(dni+"-"+fecha);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String temperaS=ds.child("GradosS").getValue().toString();
                    if(temperaS.equals("")){
                        Map<String,Object> datosGrados=new HashMap<>();
                        datosGrados.put("GradosS",grados);
                        gs.updateChildren(datosGrados);
                        Double tp = Double.parseDouble(grados);
                        if(tp>=37.5){
                            Send_Mail();
                            Toast.makeText(Registro_Temperatura.this, "Se genero una alerta", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Registro_Temperatura.this, "Se registro su temperatura de salida", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Registro_Temperatura.this, "Ya se marco su temperatura de salida el dia de hoy", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void TemperaturaI(){
        final String dni=etdni.getText().toString();
        grados=ettemperatura.getText().toString();
        Date myDate=new Date();
        final String fecha= new SimpleDateFormat("dd-MM-yy").format(myDate);
        final Query q = mdatabasetemp.orderByChild("Documento").equalTo(dni);
        final int[] cont = {0};
        final String[] FECHA = {""};
        if(dni.length()==8 && grados.length()!=0){
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    FECHA[0] =ds.child("Fecha").getValue().toString();
                    if((FECHA[0]).trim().equals(fecha.trim())){
                        cont[0]++;
                        if(cont[0] >0 ){
                            Toast.makeText(Registro_Temperatura.this, "El personal ya se registro el dia de hoy", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if(!((FECHA[0]).trim()).equals(fecha.trim())){
                    if(cont[0]==0){
                        Map<String,Object> datosGrados=new HashMap<>();
                        datosGrados.put("Documento",dni);
                        datosGrados.put("GradosI",grados);
                        datosGrados.put("GradosS","");
                        datosGrados.put("Fecha",fecha);
                        DatabaseReference gradosref = mdatabase.child("Temperatura");
                        gradosref.child(dni+"-"+fecha).setValue(datosGrados);
                        Toast.makeText(Registro_Temperatura.this, "Se guardo su temperatura correctamente", Toast.LENGTH_SHORT).show();
                        etdni.setText("");
                        ettemperatura.setText("");
                        double tp=Double.parseDouble(grados);
                        if(tp>=37.5){
                            Send_Mail();
                        }else{
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }else{
            Toast.makeText(this, "Llene correctamente los campos", Toast.LENGTH_SHORT).show();
        }
    }


    public void Send_Mail(){
        Properties p = new Properties();
        p.put(pauth,dauth);
        p.put(phost,dhost);
        p.put(pport,dport);
        p.put(ptls,dtls);

        Session s = Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sEmail,sPssw);
            }
        });

        try {
            Message m= new MimeMessage(s);
            m.setFrom(new InternetAddress(sEmail));
            InternetAddress[] to = InternetAddress.parse("programador@sagperu.com");
            m.setRecipients(Message.RecipientType.TO, to);
            String email="";
            for(int i=0; i<mails.size();i++){
                email=email+","+mails.get(i);
            }
            InternetAddress[] cc = InternetAddress.parse(email);
            m.setRecipients(Message.RecipientType.CC, cc);

            m.setSubject("IMPORTANTE");

            m.setText("El personal "+txtnombre.getText()+" "+txtapellido.getText()+" cuenta con una temperatura de : "+grados+
                    "  siendo esta superior a lo normal.\nSe comunica para que se tomen las precauciones del caso");

            new SendMail().execute(m);

        }catch (Exception vEx){
            System.out.println(vEx.getMessage());
        }
    }

    private class SendMail extends AsyncTask<Message,String,String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(Registro_Temperatura.this,"Enviando Informacion por correo","Enviando mensaje",true,false);

        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Listo";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error"+e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                progressDialog.dismiss();
                if(s.equals("Listo")){
                    AlertDialog.Builder build=new AlertDialog.Builder(Registro_Temperatura.this);
                    build.setCancelable(false);
                    build.setTitle(Html.fromHtml("<font color='#509324'>Listo<font/>"));
                    build.setMessage("Mail enviado y completo.");
                    build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Salir();
                        }
                    });
                    build.show();
                }else{
                    System.out.println("Error"+s);
                    Toast.makeText(Registro_Temperatura.this, "Error"+s, Toast.LENGTH_SHORT).show();
                }
            }catch(Exception vEx){
                System.out.println(vEx.getMessage());
            }
        }
    }

    public void Salir(){
        Toast.makeText(this, "La aplicacion se cerrara", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {

                finishAffinity();
            }
        }, 2000);
    }
}
