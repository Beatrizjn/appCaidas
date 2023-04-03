package com.example.logingexampleback4app.GPS;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.logingexampleback4app.Telegram.TelegramExecutes;
import com.pengrad.telegrambot.TelegramBot;

import java.util.List;

public class PeticionLocalizacion extends BroadcastReceiver {

    TelegramBot bot;
    float longitude = 0;
    float latitude = 0;
    long chatId = 0;
    LocationManager mLocationManager;
    LocationListener mlocationListener;
    String proveedor;
    Criteria criteria;
    TelegramExecutes telegramFunciones;
    //LocationServiceBinder binder;
    Context context;

    public PeticionLocalizacion( LocationManager locationManager, Context context) {

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = locationManager;
        mlocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {
                obtenerLocalizacion(location);
            }

            @Override
            public void onLocationChanged(@NonNull List<Location> locations) {
                LocationListener.super.onLocationChanged(locations);
            }

            @Override
            public void onFlushComplete(int requestCode) {
                LocationListener.super.onFlushComplete(requestCode);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
            }
        };

        activaActualizacionLocalizacion(context);
    }

    @Override
    public void onReceive(Context Mcontext, Intent intent) {
        context = Mcontext;
        activaActualizacionLocalizacion(context);
    }



    public void enviaMensajeTelegram()
    {
        telegramFunciones.enviaTexto("Me cai ayuda! Estoy en ",longitude , latitude);
        telegramFunciones.enviaLocalizacion(longitude, latitude );
    }

    public void obtenerLocalizacion(Location mlocation)
    {
        longitude = (float) mlocation.getLongitude();
        latitude = (float) mlocation.getLatitude();
    }

    public float getLongitude()
    {
        return longitude;
    }
    public float getLatitude()
    {
        return latitude;
    }

    public void activaActualizacionLocalizacion(Context context)
    {   // para el principal activite cambiar ContextCompat por ActivityCompat

        if ((ContextCompat.checkSelfPermission(context, /*Manifest.permission.*/ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, /*Manifest.permission.*/ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) == false)
        {
            proveedor = LocationManager.NETWORK_PROVIDER;
        } else {
            proveedor = LocationManager.GPS_PROVIDER;
        }
        //initializeLocationManager();
        mLocationManager.requestLocationUpdates(proveedor, 0, 0, mlocationListener, Looper.myLooper());
    }

    public void detenerActualizacionUbicacion()
    {
        mLocationManager.removeUpdates(mlocationListener);
    }

    public void tomaBotTelegram(TelegramExecutes telegram)
    {
        telegramFunciones = telegram;
    }


/*    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
    }*/


}

