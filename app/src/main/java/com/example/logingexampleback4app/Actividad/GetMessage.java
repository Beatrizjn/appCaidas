package com.example.logingexampleback4app.Actividad;

import static android.content.ContentValues.TAG;

import static com.parse.Parse.getApplicationContext;

import static java.lang.Thread.sleep;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.logingexampleback4app.GPS.PeticionLocalizacion;
import com.example.logingexampleback4app.Telegram.TelegramExecutes;
import com.example.logingexampleback4app.command.command;

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
    boolean terminarHilo = false;
    Thread serviceThread;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        //myAsyncTask.ejecutando
        Log.i(TAG, "onStartCommand");
        try {
            comandoRecibido = telegramFunciones.dameTextoChatNuevo();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //gestionamos la respuesta
        //NEW
        Log.i(TAG, "Comando Recibido: " + comandoRecibido);
        if(comandoRecibido != "igual")
        {
            mandaMensaje = true;
        }
        else
        {
            mandaMensaje = false;
        }

        if(mandaMensaje)
        {
            if(!terminarHilo)
            {
                serviceThread = new Thread( new threadLisening());
                serviceThread.start();
            }

        }
        if(terminarHilo)
        {
            if(serviceThread != null){  //valida si existe.
                serviceThread.interrupt();  //Interrumpe su ejecución.
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Implementa este método para liberar recursos o detener la ejecución del servicio
        super.onDestroy();

    }

    final class threadLisening implements Runnable {

        Handler timerHandler;
        long cont = 0;

        public threadLisening()
        {
            Log.i(TAG, "HILO INICIADO");
            timerHandler = new Handler();
            mContext = getApplicationContext();
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            localizacion = new PeticionLocalizacion(mLocationManager, mContext);
        }

        private void iniciarCuentaAtras(long segundos)
        {
            // asi conseguimos que solo este activa la actualizacion de localizacion bajo peticion y durante un tiempo para que la localizacion sea capaz de actualizatse
            // se pretende optimizar al maximo la bateria.
            new CountDownTimer(segundos, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(TAG, "time:"+ millisUntilFinished +"ms -> Longitude " + localizacion.getLongitude() + ", Latitude " + localizacion.getLatitude() );
                }

                @Override
                public void onFinish() {
                    localizacion.enviaMensajeTelegram();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    localizacion.detenerActualizacionUbicacion();
                    terminarHilo = true;
                }
            }.start();
        }

        @Override
        public void run() {
            Looper.prepare();
            terminarHilo = false;
            mContext = getApplicationContext();
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            localizacion = new PeticionLocalizacion(mLocationManager, mContext);
            localizacion.activaActualizacionLocalizacion(mContext);
            if (comandoRecibido.equals(command.location.toString())) {
                localizacion.tomaBotTelegram(telegramFunciones);
                while (cont < 20)
                {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cont ++;
                    Log.i( TAG,  "TIMER " + cont );
                }
                localizacion.enviaMensajeTelegram();
                localizacion.detenerActualizacionUbicacion();
                terminarHilo = true;
                cont = 0;


                //iniciarCuentaAtras(20000);
            }
            comandoRecibido = "";
            Looper.loop();
        }
    }



}
