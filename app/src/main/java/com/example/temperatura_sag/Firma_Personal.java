package com.example.temperatura_sag;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temperatura_sag.Common.MyFirm;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

public class Firma_Personal extends AppCompatActivity {
    private StorageReference strmyfirm;
    private MyFirm myFirm;
    private View view;
    String Apellido,DNI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firma__personal);
        //ActivityCompat.requestPermissions(this,new String[]{
        //Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        view = findViewById(R.id.FirmView);

        DNI = getIntent().getStringExtra("DNI");
        Apellido = getIntent().getStringExtra("Apellido");

        strmyfirm= FirebaseStorage.getInstance().getReference();

        myFirm = new MyFirm(this, null);
        setContentView(myFirm);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

    }
    private static final int GALLERY_INTENT=1;



    public void Tomar(){


        view=getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);

        Bitmap b=Bitmap.createBitmap(view.getDrawingCache());
        //Bitmap b = Bitmap.createBitmap(255,255,Bitmap.Config.ARGB_8888);


        String file = Environment.getExternalStorageDirectory() + DNI+".jpg";
        System.out.println("ruta de aca "+file);
        File fileScreen= new File(file);

        FileOutputStream fileOutputStream = null;

        try{
            fileOutputStream= new FileOutputStream(fileScreen);
            b.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Toast.makeText(this, "Firma guardada", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        buscar();
    }

    public void buscar(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory()+"");
        i.setDataAndType(uri,"image/*");
        startActivityForResult(i,GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){

            final Uri uri= data.getData();
            StorageReference filepath=strmyfirm.child("Firmas").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("aca data "+data);
                    Toast.makeText(Firma_Personal.this, "Firma subida correctamente"+uri, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainv2,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()){
            case R.id.Guardar:
                Tomar();
                return true;
            case R.id.Limpiar:
                myFirm.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
