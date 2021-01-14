package com.example.temperatura_sag;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Declaracion_jurada extends AppCompatActivity  {
    EditText txtdni,txtmedi,txtenfer;
    ImageButton img;
    DatabaseReference mDatabasePersonal,mDatabase,mDatabaseDeclaracion2,mDatabaseArea;
    String NOMBRE, APELLIDO, DIRECCION, TELEFONO,IDAREA,NAMEAREA,DNI,doc,Medicamentos,enfermedad,fecha;
    CheckBox CK1,CK2,CK3,CK4,CK5,CK6,CK7;
    Button btnguardar;
    ArrayList<String> respuestas =new ArrayList<>();
    Bitmap b=null,firma=null;
    Date myDate = new Date();
    private String fichero="/APK_SAG/",file_path="";
    ArrayList<String> lsfec = new ArrayList<>();
    int val=0;
    final String fechapdf = new SimpleDateFormat("dd-MM-yyyy").format(myDate);
    String sEmail,sPssw;
    String pauth,phost,pport,ptls;
    String dauth,dhost,dport,dtls;
    String filName;
    ArrayList<String> mails=new ArrayList<>();
    DatabaseReference sendmail,pendmail,dendmail,fendmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_declaracion_jurada);
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE},
                PackageManager.PERMISSION_GRANTED);

        CK1=findViewById(R.id.ck1);
        CK2=findViewById(R.id.ck2);
        CK3=findViewById(R.id.ck3);
        CK4=findViewById(R.id.ck4);
        CK5=findViewById(R.id.ck5);
        CK6=findViewById(R.id.ck6);
        CK7=findViewById(R.id.ck7);
        txtdni=findViewById(R.id.etDNI);
        txtmedi=findViewById(R.id.etmedi);
        txtenfer=findViewById(R.id.etenfermedad);


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
        btnguardar=findViewById(R.id.btnsave);
        mDatabasePersonal= FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_personal));
        mDatabaseArea = FirebaseDatabase.getInstance().getReference("Area");
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabaseDeclaracion2= FirebaseDatabase.getInstance().getReference((getString(R.string.nodo_declaracion)));
        txtdni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 DNI=txtdni.getText().toString();
                Query q= mDatabasePersonal.orderByChild(getString(R.string.campo_DNI)).equalTo(DNI);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (DNI.length()==8){
                                for(DataSnapshot ds : dataSnapshot.getChildren()){
                                    NOMBRE=ds.child("Nombres").getValue().toString();
                                    APELLIDO=ds.child("Apellidos").getValue().toString();
                                    DIRECCION=ds.child("Direccion").getValue().toString();
                                    TELEFONO=ds.child("Telefono").getValue().toString();
                                    IDAREA=ds.child("Area").getValue().toString();
                                    Area(IDAREA);
                                }
                            }
                            else{
                                NOMBRE="";
                                APELLIDO="";
                                DIRECCION="";
                                TELEFONO="";
                                IDAREA="";
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
        txtmedi.setVisibility(View.GONE);
        txtenfer.setVisibility(View.GONE);

        CK6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((CK6).isChecked()==true){
                    txtmedi.setVisibility(View.VISIBLE);
                }else {
                    txtmedi.setVisibility(View.GONE);
                    txtmedi.setText("");
                }
            }
        });
        CK7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((CK7).isChecked()==true){
                    txtenfer.setVisibility(View.VISIBLE);
                }else {
                    txtenfer.setVisibility(View.GONE);
                    txtenfer.setText("");
                }
            }
        });
    }

    public void Area(final String id){

            Query q =mDatabaseArea.orderByKey().equalTo(id);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        NAMEAREA=ds.child("Nombre").getValue().toString();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


    }

    public void Eliminar(String DNI,final String Dia){
        Query q=mDatabaseDeclaracion2.orderByChild("DNI").equalTo(DNI);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            int cont=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    cont++;
                    String dia= ds.child("Fecha").getValue().toString();
                    if(Dia.equals(dia)){
                        ds.getRef().removeValue();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void Val(View view){
        if((txtdni.getText()).length()==8 && NOMBRE.length()>0){
        val=0;
        final String DNI=txtdni.getText().toString();
        Query q=mDatabaseDeclaracion2.orderByChild("DNI").equalTo(DNI);
        lsfec.clear();
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    doc=ds.child("DNI").getValue().toString();
                    fecha=ds.child("Fecha").getValue().toString();
                    lsfec.add(fecha);
                }
                if(dataSnapshot.getValue()==null){
                    Encuesta();
                }else{
                    for (int i=0;i<lsfec.size();i++){
                        if(lsfec.get(i).equals(fechapdf)){
                            val=1;
                        }
                    }
                     if(val==1){
                         Toast.makeText(Declaracion_jurada.this, "Ya se realizo una declaracion jurada de este personal el dia de hoy", Toast.LENGTH_SHORT).show();
                        //Msg(doc,fecha);
                    }else{
                        Encuesta();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        }else{
            Toast.makeText(this, "No se encontro registro", Toast.LENGTH_SHORT).show();
        }
    }

    public void Msg( final String doc,final String fecha){
        AlertDialog.Builder msg=new AlertDialog.Builder(Declaracion_jurada.this);
        msg.setTitle("Importante");
        msg.setMessage("Se encontro una declaracion de este personal, ¿ Desea actualizar ?");
        msg.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Encuesta();
                    }
                },3000);
                Eliminar(doc,fecha);
            }
        });
        msg.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Declaracion_jurada.this, "Solicitud cancelada", Toast.LENGTH_SHORT).show();
            }
        });
        msg.show();
    }

    public void  Encuesta(){
        respuestas.clear();
        if((CK1).isChecked()==true){
            respuestas.add("1");
        }else{
            respuestas.add("0");
        }
        if((CK2).isChecked()==true){
            respuestas.add("1");
        }else{
            respuestas.add("0");
        }
        if((CK3).isChecked()==true){
            respuestas.add("1");
        }else{
            respuestas.add("0");
        }
        if((CK4).isChecked()==true){
            respuestas.add("1");
        }else{
            respuestas.add("0");
        }
        if((CK5).isChecked()==true){
            respuestas.add("1");
        }else{
            respuestas.add("0");
        }
        if((CK6).isChecked()==true){
            respuestas.add("1");
        }else{
            respuestas.add("0");
        }
        if(respuestas.size()==6){
            Date myDate = new Date();
            final String fecha = new SimpleDateFormat("dd-MM-yyyy").format(myDate);
            DatabaseReference gradosref = mDatabase.child("Declaracion");
            DNI=txtdni.getText().toString();
            Medicamentos=txtmedi.getText().toString();
            enfermedad=txtenfer.getText().toString();
            Map<String, Object> datosDeclaracion = new HashMap<>();
            datosDeclaracion.put("DNI", DNI);
            datosDeclaracion.put("rpt1", respuestas.get(0));
            datosDeclaracion.put("rpt2", respuestas.get(1));
            datosDeclaracion.put("rpt3", respuestas.get(2));
            datosDeclaracion.put("rpt4", respuestas.get(3));
            datosDeclaracion.put("rpt5", respuestas.get(4));
            datosDeclaracion.put("rpt6", respuestas.get(5));
            datosDeclaracion.put("Medicamentos", Medicamentos);
            datosDeclaracion.put("Enfermedad", enfermedad);
            datosDeclaracion.put("Fecha", fecha);

            gradosref.child(DNI+"-"+fecha).setValue(datosDeclaracion);
        }
        delcaracionfirmada();
    }

    public void Pasar_datos(View view){
        Intent i = new Intent(this, Personal_Datos.class);
        i.putExtra("Nombre",NOMBRE);
        i.putExtra("Apellido",APELLIDO);
        i.putExtra("Direccion",DIRECCION);
        i.putExtra("Telefono",TELEFONO);
        startActivity(i);
    }

    public void delcaracionfirmada(){
        firma=Toma_defirma();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firma==null){
                    firma=Toma_defirma();
                    declaracioonpdf();
                }else{
                    declaracioonpdf();
                }
            }
        },3000);
    }

    public void declaracioonpdf(){
        try {
            if(txtdni.getText().length()>0 || txtdni.getText()!=null){
            //Toma_defirma();
            PdfDocument myPdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
            PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
            Canvas canvas = myPage1.getCanvas();
            Paint myPaint = new Paint();
            myPaint.setTextSize(11);
            canvas.drawText("He recibido explicación del objetivo de esta evalaución y me comprometo a responder con la verdad",10,60,myPaint);
            myPaint.setAntiAlias(true);
            myPaint.setColor(Color.BLACK);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            canvas.drawRect(150,10,canvas.getWidth()-150,30,myPaint);
            myPaint.setStrokeWidth(1);
            canvas.drawRect(450,270,350,250,myPaint);
            canvas.drawRect(550,270,450,250,myPaint);
            int t=285,b=270;
            int tx=280;
            for(int i=0;i<respuestas.size();i++){
                int l=450,r=350,lx=500;
                myPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(l,t,r,b,myPaint);
                l=l+100;
                r=r+100;
                canvas.drawRect(l,t,r,b,myPaint);
                if(respuestas.get(i)=="0"){
                    canvas.drawText("X",lx,tx,myPaint);
                    tx=tx+15;
                }else{
                    lx=lx-100;
                    canvas.drawText("X",lx,tx,myPaint);
                    tx=tx+15;
                }
                t=t+15;
                b=b+15;
            }
            myPaint.setAntiAlias(true);
            myPaint.setColor(Color.parseColor("#2E98D5"));
            myPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(151,11,canvas.getWidth()-151,29,myPaint);
            myPaint.setColor(Color.WHITE);
            canvas.drawText("Ficha de sintomatología COVID-19 Para Regreso al Trabajo-Declaración Jurada",230,25,myPaint);
            myPaint.setColor(Color.BLACK);
            canvas.drawText("Empresa o Entidad Pública: SERVICIOS ANALITICOS GENERALES SAC",10,90,myPaint);
            canvas.drawText("RUC : 20514746355 ",490,90,myPaint);
            canvas.drawText("Apellidos y Nombres : "+NOMBRE+" "+APELLIDO ,10,120,myPaint);
            myPaint.setAntiAlias(true);
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(1);
            myPaint.setColor(Color.BLACK);
            canvas.drawLine(120, 122, 600, 122, myPaint);
            canvas.drawLine(120, 152, 340, 152, myPaint);
            canvas.drawLine(380, 152, 600, 152, myPaint);
            canvas.drawLine(65, 182, 340, 182, myPaint);
            canvas.drawLine(410, 182, 600, 182, myPaint);
            canvas.drawLine(50, 540, 150, 540, myPaint);
            //canvas.drawLine(550, 540, 650, 540, myPaint);
            myPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Area de trabajo :         "+NAMEAREA,10,150,myPaint);
            canvas.drawText("DNI :   "+doc,350,150,myPaint);
            myPaint.setTextSize(8);
            canvas.drawText("Direccion : "+DIRECCION,10,180,myPaint);
            myPaint.setTextSize(11);
            canvas.drawText("N° Celular :   "+TELEFONO,350,180,myPaint);
            canvas.drawText("En los últimos 14 días calendario ha tenido alguno de los síntomas siguientes:",10,230,myPaint);
            canvas.drawText("1. Sensación de alza térmica o fiebre",10,280,myPaint);
            canvas.drawText("2. Tos,estornudos o dificultad para respirar.",10,295,myPaint);
            canvas.drawText("3. Expectoración o flema amarila o verdosa.",10,310,myPaint);
            canvas.drawText("4. Pérdida del gusto y/o del olfato.",10,325,myPaint);
            canvas.drawText("5. Contacto con personas(s) con un caso confirmado de COVID-19",10,340,myPaint);
            canvas.drawText("6. Está tomando alguna medicación (detallar cuál o cuales)? ",10,355,myPaint);
            canvas.drawText("SI",395,265,myPaint);
            canvas.drawText("NO",495,265,myPaint);
            canvas.drawText(""+Medicamentos,10,370,myPaint);
            canvas.drawText("Todos los datos expresados en esta ficha constituyen declaración jurada de mi parte.",10,410,myPaint);
            canvas.drawText("He sido informado que de omitir o falsear información puedo perjudicar la salud de mis compañeros, y la mía propia,lo cual de constituir una falta grave a la salud",10,425,myPaint);
            canvas.drawText("En caso de que usted cuente con otro tipo de enfermedad de importancia, por favor amplie la infromación de dicha enfermedad:",10,440,myPaint);
            canvas.drawText(""+enfermedad,10,470,myPaint);
            canvas.drawText("Fecha:         "+fechapdf  ,10,538,myPaint);
            canvas.drawText("Firma:         ",510,538,myPaint);
            //firma=Toma_defirma();
            canvas.drawBitmap(firma,550,490,myPaint);

            myPdfDocument.finishPage(myPage1);
            filName = doc+"_fec_"+fechapdf+ ".pdf";
            File file;
            Toast.makeText(this, "Declaracion guardada", Toast.LENGTH_SHORT).show();
            this.file_path=(Environment.getExternalStorageDirectory()+fichero);
            File localFile = new File(this.file_path);
            if(!localFile.exists()){
                localFile.mkdir();
            }
            file=new File(localFile,filName);
            myPdfDocument.writeTo(new FileOutputStream(file));
            myPdfDocument.close();

            Send_Mail(file_path+filName);
            }else {
                Toast.makeText(this, "Ingrese un docuemtno", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception vEx){

            Toast.makeText(this, "Mensaje de error"+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap Toma_defirma(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference picRef=storage.getReference()
                .child("Firmas")
                .child(txtdni.getText()+".png");
        picRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                      b = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        //dowloandimg.setImageBitmap(b);
                    }
                });
        return b;
    }

    public void Send_Mail(String file){
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
            Address formail = new InternetAddress("programador@sagperu.com");

            Message m= new MimeMessage(s);
            System.out.println("sE");
            m.setFrom(new InternetAddress(sEmail));

            m.setRecipient(Message.RecipientType.TO, formail);

            String email="";
            for(int i=0; i<mails.size();i++){
                if(i==0){
                    email=mails.get(i);
                }else{
                    email=email+","+mails.get(i);
                }
            }

            InternetAddress[] cc = InternetAddress.parse(email);

            m.setRecipients(Message.RecipientType.CC, cc);
            BodyPart pdfMail = new MimeBodyPart();

            pdfMail.setDataHandler(new DataHandler(new FileDataSource(file)));

            pdfMail.setFileName(filName);

            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(pdfMail);
            m.setSubject("FICHA DE SINTOMATOLOGIA DE "+NOMBRE+" "+APELLIDO);
            m.setText("El personal realizo su declaracion jurada se adjunta archivo con los datos");
            m.setContent(mp);
            new SendMail().execute(m);
        }catch (Exception vEx){
            System.out.println("catch send mail "+vEx.getMessage());
            Toast.makeText(Declaracion_jurada.this, "catch send mail "+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private class SendMail extends AsyncTask<Message,String,String> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(Declaracion_jurada.this,"Enviando Informacion por correo","Enviando mensaje",true,false);
        }
        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Listo";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "doInBackground "+e.toString();
            }
        }
        @Override
        protected void onPostExecute(String s) {
            System.out.println("error "+s);
            super.onPostExecute(s);
            try{
                progressDialog.dismiss();
                if(s.equals("Listo")){
                    android.app.AlertDialog.Builder build=new android.app.AlertDialog.Builder(Declaracion_jurada.this);
                    build.setCancelable(false);
                    build.setTitle(Html.fromHtml("<font color='#509324'>Listo<font/>"));
                    build.setMessage("Mail enviado y completo.");
                    build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Pass();
                            dialog.dismiss();
                        }
                    });
                    build.show();
                }else{
                    System.out.println("Error inseperado "+s);
                    Toast.makeText(Declaracion_jurada.this, "Error inesperado"+s, Toast.LENGTH_SHORT).show();
                }
            }catch(Exception vEx){
                Toast.makeText(Declaracion_jurada.this, "Catch send "+vEx.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Pass(){
        String dni=txtdni.getText().toString();
        Intent i = new Intent(this, Registro_Temperatura.class);
        i.putExtra("Tipo","ingreso");
        i.putExtra("DNI",dni);
        i.putExtra("Nombre",NOMBRE);
        i.putExtra("Apellido",APELLIDO);
        startActivity(new Intent(i));
    }
}