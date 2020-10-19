package com.example.temperatura_sag;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class prueba extends AppCompatActivity {
    //1.carga de imagen
    ImageView dowloandimg;
    //3.envio de correos con cuenta en variables
    //String sEmail="luisferowo@gmail.com",sPssw="elfutboleslomejor20";

    private String fichero="/APK_SAG/",file_path="";
    Bitmap firma = null;
    String sEmail,sPssw;

    String pauth,phost,pport,ptls;
    Bitmap b=null;
    String dauth,dhost,dport,dtls;

    ArrayList<String> mails=new ArrayList<>();

    DatabaseReference sendmail;
    DatabaseReference pendmail;
    DatabaseReference dendmail;
    DatabaseReference fendmail;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);
        dowloandimg=findViewById(R.id.firma);

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
                        email=ds.child("email").getValue().toString();
                        mails.add(email);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void delcaracionfirmada(View view){
        firma=Descarga_firma();
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                firma=Descarga_firma();
                if(firma==null){
                    declaracioonpdf();
                }else{
                    declaracioonpdf();
                }
            }
        },3000);

    }
    public void declaracioonpdf(){
        try {
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

                myPaint.setAntiAlias(true);
                myPaint.setColor(Color.parseColor("#2E98D5"));
                myPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(151,11,canvas.getWidth()-151,29,myPaint);
                myPaint.setColor(Color.WHITE);
                canvas.drawText("Ficha de sintomatología COVID-19 Para Regreso al Trabajo-Declaración Jurada",230,25,myPaint);
                myPaint.setColor(Color.BLACK);
                canvas.drawText("Empresa o Entidad Pública: SERVICIOS ANALITICOS GENERALES SAC",10,90,myPaint);
                canvas.drawText("RUC : 20514746355 ",490,90,myPaint);
                canvas.drawText("Apellidos y Nombres : "+" ",10,120,myPaint);
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
                canvas.drawLine(550, 540, 650, 540, myPaint);
                myPaint.setStyle(Paint.Style.FILL);
                canvas.drawText("Area de trabajo :         ",10,150,myPaint);
                canvas.drawText("DNI :   ",350,150,myPaint);
                canvas.drawText("Direccion : ",10,180,myPaint);
                canvas.drawText("N° Celular :   ",350,180,myPaint);
                canvas.drawText("En los últimos 14 días calendario ha tenido alguno de los síntomas siguientes:",10,230,myPaint);
                canvas.drawText("1. Sensación de alza térmica o fiebre",10,280,myPaint);
                canvas.drawText("2.Tos,estornudos o dificultad para respirar.",10,295,myPaint);
                canvas.drawText("3. Expectoración o flema amarila o verdosa.",10,310,myPaint);
                canvas.drawText("4.Contacto con personas(s) con un caso confirmado de COVID-19",10,325,myPaint);
                canvas.drawText("5. Esta tomando alguna medicación (detallar cuál o cuales)",10,340,myPaint);
                canvas.drawText("SI",395,265,myPaint);
                canvas.drawText("NO",495,265,myPaint);
                canvas.drawText("Todos los datos expresados en esta ficha constituyen declaración jurada de mi parte.",10,410,myPaint);
                canvas.drawText("He sido informado que de omitir o falsear información puedo perjudicar la salud de mis compañeros, y la mía propia,lo cual de constituir una falta grave a la salud",10,425,myPaint);
                canvas.drawText("En caso de que usted cuente con otro tipo de enfermedad de importancia, por favor amplie la infromación de dicha enfermedad:",10,440,myPaint);
                canvas.drawText("Firma:         ",510,538,myPaint);

                canvas.drawBitmap(firma,580,510,myPaint);

                myPdfDocument.finishPage(myPage1);
                 String filName = "_fec_" + ".pdf";
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

                //Send_Mail(file_path+filName);
        }catch (Exception vEx){
            System.out.println(vEx.getMessage());
            Toast.makeText(this, "Mensaje de error"+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //3.envio de correo usando javamail y credenciales locales
    public void Send_Mail(View view){
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
            Address formail = new InternetAddress("programador@gsagperu.com");
            Message m= new MimeMessage(s);
            m.setFrom(new InternetAddress(sEmail));

            m.setRecipient(Message.RecipientType.TO, formail);
            String email="";
            for(int i=0; i<mails.size();i++){
               email=email+","+mails.get(i);
               //Address CC = new InternetAddress(email);
               //m.addRecipient(Message.RecipientType.CC,CC);
                System.out.println(email);
            }
            InternetAddress[] cc = InternetAddress.parse(email);
            m.setRecipients(Message.RecipientType.CC, cc);

            m.setSubject("Subject");

            m.setText("Texto");

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
            progressDialog=ProgressDialog.show(prueba.this,"Enviando Informacion por correo","Enviando mensaje",true,false);

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
                    AlertDialog.Builder build=new AlertDialog.Builder(prueba.this);
                    build.setCancelable(false);
                    build.setTitle(Html.fromHtml("<font color='#509324'>Listo<font/>"));
                    build.setMessage("Mail enviado y completo.");
                    build.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                        }
                    });
                    build.show();
                }else{
                    System.out.println("Error"+s);
                    Toast.makeText(prueba.this, "Error"+s, Toast.LENGTH_SHORT).show();
                }
            }catch(Exception vEx){
                System.out.println(vEx.getMessage());
            }
        }
    }
    //2.envio de correo desde el celular con cuenta abierta en el
    /*
    public void sendmail(View view){
        String [] emails={"lfrw20@gmail.com"};
        String [] CC={"luferowo@gmail.com"};

        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL,emails);
        i.putExtra(Intent.EXTRA_CC,CC);

        i.putExtra(Intent.EXTRA_SUBJECT,"ASUNTO");
        i.putExtra(Intent.EXTRA_TEXT,"texto del cuerpo");

        startActivity(Intent.createChooser(i,"Seleccione metodo de envio"));
    }
*/
    public void imagen(View view){
        Bitmap a;
        a=Descarga_firma();
        dowloandimg.setImageBitmap(a);
    }
    //1.Traer imagen de firebase storage
    public Bitmap Descarga_firma(){
          FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference picRef=storage.getReference()
                    .child("Firmas")
                    .child("73110786.png");
        try{
            picRef.getBytes(100*300)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            b= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            //dowloandimg.setImageBitmap(b);
                        }
                    });
        }catch(Exception vEx){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        return b;
    }
}
