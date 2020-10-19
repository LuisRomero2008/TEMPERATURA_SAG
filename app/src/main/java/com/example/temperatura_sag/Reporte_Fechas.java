package com.example.temperatura_sag;

import android.app.DatePickerDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.temperatura_sag.Common.DatePickerFragment;
import com.example.temperatura_sag.Common.Listado_grados;
import com.example.temperatura_sag.Extra.Grados_reporte;
import com.example.temperatura_sag.Extra.TemperaturaBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Reporte_Fechas extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
    {
    EditText etdesde, ethasta;
    String Doc,Grados,Nombres,date;
    int val=0;


        Canvas canvas;
        Paint myPaint = new Paint();

        ArrayList<String>lsd=new ArrayList<>();
        ArrayList<String> lsn=new ArrayList<>();
        ArrayList<String> lsg=new ArrayList<>();
        ArrayList lt=new ArrayList();

        TemperaturaBean vElement=new TemperaturaBean();
        Listado_grados objReporte=new Listado_grados();

        private DatabaseReference mdatabaseGrados;
    private DatabaseReference mdatabasePersonal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte__fechas);
        etdesde = findViewById(R.id.txtdesde);
        ethasta = findViewById(R.id.txthasta);
        mdatabasePersonal= FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_personal));
        mdatabaseGrados=FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_temperatura));

        etdesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val=1;
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });

        ethasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val=2;
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });
        datosP();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c= Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        date= DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());

        if(val==1){
            etdesde.setText(date);
        }
        if(val==2){
            ethasta.setText(date);
        }
        val=0;
    }

    public void datosP(){
        final Query qp=mdatabasePersonal;
        qp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                            Nombres=ds.child("Nombres").getValue().toString();
                            Doc=ds.child("DNI").getValue().toString();
                            lsd.add(Doc);
                            lsn.add(Nombres);
                    }
                }
                catch(Exception vEx){
                    Toast.makeText(Reporte_Fechas.this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public long fechas() throws ParseException {
        long MILLISECS_PER_DAY=24*60*60*1000;
        SimpleDateFormat formato=new SimpleDateFormat("dd/MM/yyyy");
        String fecini=etdesde.getText().toString();
        String fecfin=ethasta.getText().toString();
        Date Val1=null;
        Date Val2=null;
        Val1=(Date) formato.parse(fecini);
        Val2=(Date) formato.parse(fecfin);
        //fecha.setTime(Val1);
        long dias= (Val2.getTime() -Val1.getTime())/MILLISECS_PER_DAY;
        return dias;
    }

    public ArrayList prueba( int i){
        Query qg=mdatabaseGrados.orderByChild("Documento").equalTo(lsd.get(i));
        qg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lt.clear();
                /*for(DataSnapshot ds:dataSnapshot.getChildren()){
                        Grados=ds.child("Grados").getValue().toString();
                        //lsg.add(Grados);
                        lt.add(Grados);
                    }
*/
                   Grados_reporte data = objReporte.DataToList(dataSnapshot);
                    for (int a=0; a<data.getValores().size();a++){
                        //System.out.println("objeto "+a+" :"+data.getValores().get(a));
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return lt;
    }

    public void listado(View view){
        for (int a=0;a<lsd.size();a++){
            prueba(a);
             //ArrayList data =  prueba(a);
            //System.out.println("priemro "+data);
        }
    }


    public void pdf(View view) {
        try{
            String ifecha = etdesde.getText().toString();
            String ffecha = ethasta.getText().toString();

            PdfDocument myPdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
            PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
            canvas = myPage1.getCanvas();
            myPaint.setTextSize(7);

            int y = 100;
            int x = 110;
            canvas.drawText("Desde :................ hasta :................", 5, 50, myPaint);
            canvas.drawText(ifecha + "                " + ffecha, 35, 48, myPaint);
            canvas.drawText("Documento", 5, 80, myPaint);
            canvas.drawText("Nombres", 55, 80, myPaint);

            long contdias=fechas()+1;
            if(contdias<32){
                for (int i = 1; i < contdias+1; i++) {
                    //fecha.add(Calendar.DAY_OF_YEAR,i);
                    //System.out.println(fecha);
                    String dias = "Dia" + i;
                    canvas.drawText("" + dias, x, 80, myPaint);
                    if (i < 10) {
                        x = x + 20;
                    } else {
                        x = x + 25;
                    }
                }
                int n = 200, m = 200;
            }else{
                Toast.makeText(this, "No se puede generar el reporte por mas de 31 dias calendario", Toast.LENGTH_SHORT).show();
            }


            for (int i = 0; i < lsn.size(); i++) {
                String Documento = lsd.get(i).toString();
                String Nombre = lsn.get(i).toString();
                canvas.drawText("" + Documento, 5, y, myPaint);
                canvas.drawText("" + Nombre, 55, y, myPaint);
                y = y + 10;
            }

            myPdfDocument.finishPage(myPage1);

            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            Date myDate = new Date();
            final String fecha = new SimpleDateFormat("dd-MM-yy").format(myDate);

            String filName = "" + fecha + ".pdf";

            File file = new File(baseDir + File.separator + filName);

            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(Reporte_Fechas.this, "Reporte generado", Toast.LENGTH_SHORT).show();
            myPdfDocument.close();
        }catch (Exception vEx){
            Toast.makeText(this, ""+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    }
