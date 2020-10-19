package com.example.temperatura_sag.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Network {
    public static boolean isDataConnectionAvailable(Context context) {
        try{
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }catch(Exception vEx){
            Toast.makeText(context, " "+vEx.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
