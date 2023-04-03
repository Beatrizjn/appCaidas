package com.example.logingexampleback4app.Actividad;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.logingexampleback4app.GPS.PeticionLocalizacion;
import com.example.logingexampleback4app.Telegram.TelegramExecutes;

public class GetMessage extends Service {

    String comandoRecibido = "/localizacion";
    boolean mandaMensaje = true;
    PeticionLocalizacion localizacion = null;
    Handler mHandler;
    TelegramExecutes telegramFunciones = new TelegramExecutes();
    LocationManager mLocationManager;
    Context mContext;
    private Thread thread;
    DetectaAlerta myAsyncTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mHandler = new Handler();
        myAsyncTask = new DetectaAlerta();
        /*mContext = getApplicationContext();
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        localizacion = new PeticionLocalizacion(mLocationManager, mContext);*/

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Crear la notificación
        /*Notification notification = new Notification.Builder(this)
                .setContentTitle("Mi aplicación está en segundo plano")
                .setContentText("La aplicación está ejecutando un servicio en segundo plano")
                .build();

        // Mostrar la notificación y poner el servicio en primer plano
        startForeground(1, notification);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        //myAsyncTask.ejecutando

        if(!myAsyncTask.ejecutando)
        {
            myAsyncTask.cancel(true);
            myAsyncTask = new DetectaAlerta();

            Log.i(TAG, "RUN GETMESSAGE");
            myAsyncTask.execute();
        }



        /*thread = new Thread(new Runnable() {
            public void run() {
                try {
                    detectaAlerta();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();*/


        // para que use un hilo diferente al principal hay que crear una clase runnable
        /*Thread serviceThread = new Thread( new threadLisening(startId));
        serviceThread.start();*/

        /*new Handler(Looper.myLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    //Sync data to and fro every 300 seconds
                    detectaAlerta();
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Implementa este método para liberar recursos o detener la ejecución del servicio
        super.onDestroy();

    }

/*    final class threadLisening implements Runnable {

        int idServiceThread;
        Handler timerHandler;*/

        /*public threadLisening(int idThread)
        {
            idServiceThread = idThread;
            Log.i(TAG, "HILO INICIADO: " + idThread);
            timerHandler = new Handler();
            mContext = getApplicationContext();
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            localizacion = new PeticionLocalizacion(mLocationManager, mContext);
        }*/

/*        @Override
        public void run() {
            try {
                //Sync data to and fro every 300 seconds
                detectaAlerta();
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }        }
    }*/



}
