package com.example.logingexampleback4app.Actividad;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static com.parse.Parse.getApplicationContext;
import static java.lang.Thread.sleep;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.logingexampleback4app.GPS.PeticionLocalizacion;
import com.example.logingexampleback4app.Telegram.TelegramExecutes;
import com.example.logingexampleback4app.command.command;

public class DetectaAlerta extends AsyncTask<Void, Void, Void> {

    String comandoRecibido = "/localizacion";
    boolean mandaMensaje = false;
    Context context;
    PeticionLocalizacion localizacion;
    LocationManager mLocationManager;
    TelegramExecutes telegramFunciones = new TelegramExecutes();
    public boolean ejecutando = false;

    @Override
    protected Void doInBackground(Void... voids) {
        if(!ejecutando)
        {
            Log.i(TAG, "RUN doInBackground");
            ejecutando = true;
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
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(ejecutando) {
            // Aquí puedes realizar las operaciones que necesites en el hilo principal
            Log.i(TAG, "RUN onPostExecute");
            super.onPostExecute(aVoid);
            if (mandaMensaje) {
                context = getApplicationContext();
                mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                localizacion = new PeticionLocalizacion(mLocationManager, context);
                localizacion.activaActualizacionLocalizacion(context);
                if (comandoRecibido.equals(command.location.toString())) {
                    localizacion.tomaBotTelegram(telegramFunciones);
                    iniciarCuentaAtras(20000);
                    localizacion.enviaMensajeTelegram();
                }
                comandoRecibido = "";
            }
            ejecutando = false;
        }
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
                localizacion.detenerActualizacionUbicacion();;
            }
        }.start();
    }

}
