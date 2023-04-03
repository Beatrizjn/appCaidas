package com.example.logingexampleback4app.Actividad;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logingexampleback4app.BuildConfig;
import com.example.logingexampleback4app.GPS.PeticionLocalizacion;
import com.example.logingexampleback4app.Modelos.DatosUsuario;
import com.example.logingexampleback4app.R;
import com.example.logingexampleback4app.Telegram.TelegramExecutes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 200;
    private ProgressDialog progressDialog;
    private TextView username;
    private TextView CaidasDia;
    private TextView CaidasSemana;
    private Button alertButton;
    private DatosUsuario datos;

    protected float latitude, longitude;
    LocationManager mLocationManager;
    Handler handler;
    private BroadcastReceiver broadcastReceiver;

    private FloatingActionButton editarUsuariosAlertados;
    private RecyclerView recyclerView;
    private TextView empty_text;
    PeticionLocalizacion localizacion = null;
    private String myId;
    private String comando;
    private Context context;

    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myId = "BtIoRy64hG";
        /*client = new OkHttpClient();
        bot = new TelegramBot.Builder("5908330497:AAGUu9mSRLyT9pGXypIbEJIQRkIxOL3c2PU").okHttpClient(client).build();
        chatId = Long.parseLong("-1001747594999");*/



        progressDialog = new ProgressDialog(MainActivity.this);
        initMainActivityControls();
        getTodoList();

        editarUsuariosAlertados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listAlertUser = new Intent(MainActivity.this, ListaAlertUser.class);
                startActivity(listAlertUser);
            }
        });

        //Obtener locacizcion
        /*mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        localizacion = new PeticionLocalizacion(mLocationManager, getApplicationContext());
        iniciarCuentaAtras(30000);*/

        /*handler = new Handler()
        {
            @Override
            public void handleMessage(Message mensaje)
            {
                Bundle bundle = mensaje.getData();
                comando = bundle.getString("comando");
                if(comando.equals(command.location.toString()))
                {
                    sendLocation();
                }
            }
        };*/ // En esta zona creamos el objeto Handler
        /*Thread thread = new Thread((Runnable) new GetMessage(bot, handler));
        thread.start();*/

        /*// Create the Data object:
        @SuppressLint("RestrictedApi") Data myData = new Data.Builder()
                .put("BOT", bot)
                .put("HANDLER", handler)
                .build();*/

        //WorkRequest getMessageRequest = new PeriodicWorkRequest.Builder(GetMessage.class,15000, TimeUnit.MILLISECONDS).build();
        /*context = getApplicationContext();
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        localizacion = new PeticionLocalizacion(mLocationManager, context);
        localizacion.activaActualizacionLocalizacion(context);
        TelegramExecutes telegramFunciones = new TelegramExecutes();
        localizacion.tomaBotTelegram(telegramFunciones);
        iniciarCuentaAtras(10000);
        localizacion.enviaMensajeTelegram();*/

        //WorkManager.getInstance(this).enqueue(getMessageRequest);
        Intent serviceIntent = new Intent(this, GetMessage.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
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
                detenerActualizacionUbicacion();
            }
        }.start();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // para que vuelva a la vida con la aplicacion cuando no quieres que se actualice en segundo plano, asi ahorras bateria
        //activaActualizacionLocalizacion();
    }

    private void activaActualizacionLocalizacion()
    {
        localizacion.activaActualizacionLocalizacion(getApplicationContext());
    }

    private void initMainActivityControls() {
        alertButton = (Button)findViewById(R.id.buttonAlerta);
        username = (TextView)findViewById(R.id.username);
        CaidasDia = (TextView)findViewById(R.id.datoCaidaDia);
        CaidasSemana = (TextView)findViewById(R.id.datoCaidaSemana);
        editarUsuariosAlertados = (FloatingActionButton) findViewById(R.id.floatingActionButton_edit);
    }

    private void getTodoList() {
        progressDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DatosUsuario");
        //We use this code to fetch data from newest to oldest.
        //query.orderByDescending("createdAt");
        query.getInBackground(myId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //We are initializing Todo object list to our adapter
                    userDataFromBack4app(object);
                } else {
                    //showAlert("Error", e.getMessage());
                }
            }
        });
        progressDialog.dismiss();
        /*query.findInBackground((objects, e) -> {
            progressDialog.dismiss();
            if (e == null) {
                //We are initializing Todo object list to our adapter
                initTodoList(objects);
            } else {
                showAlert("Error", e.getMessage());
            }
        });*/
    }
    @Override
    protected void onPause() {
        super.onPause();
        // detener las acciones que no quieres que corran en segundo plano
        //detenerActualizacionUbicacion();
    }

    public void detenerActualizacionUbicacion()
    {
        localizacion.detenerActualizacionUbicacion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }


    private void userDataFromBack4app(ParseObject dato)
    {
        int caidas_dia, caida_semana;
        DatosUsuario user;
        if (dato == null) {
            empty_text.setVisibility(View.VISIBLE);
            return;
        }
        //empty_text.setVisibility(View.GONE);

        caidas_dia = dato.getInt("Caidas_Dia");
        caida_semana = dato.getInt("Caidas_Semana");

        username.setText(dato.getString("nombreUsuario"));
        CaidasDia.setText(String.valueOf(caidas_dia));
        CaidasSemana.setText(String.valueOf(caida_semana));

        //UserAdapterDatos adapter = new UserAdapterDatos(list, this);

    }

    private void initTodoList(List<DatosUsuario> list) {
        if (list == null || list.isEmpty()) {
            empty_text.setVisibility(View.VISIBLE);
            return;
        }
        empty_text.setVisibility(View.GONE);
    }

    // actualiza los datos de la pantalla
    private void initPopupViewControls(String name, String dias, String semana) {
        /*LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        popupInputDialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null);
        titleInput = popupInputDialogView.findViewById(R.id.titleInput);
        descriptionInput = popupInputDialogView.findViewById(R.id.descriptionInput);
        saveTodoButton = popupInputDialogView.findViewById(R.id.button_save_todo);
        cancelUserDataButton = popupInputDialogView.findViewById(R.id.button_cancel_user_data);

        titleInput.setText(title);
        descriptionInput.setText(description);*/
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public void listaAlertUser(View view)
    {
        Intent listAlertUser = new Intent(this, ListaAlertUser.class);
        startActivity(listAlertUser);
    }

    public void accionAlertarUsuarios(View view)
    {
        sendLocation();
    }


    public void sendLocation()
    {
        activaActualizacionLocalizacion();
        //obtenerUltimaLocalizacion();
        iniciarCuentaAtras(7000);
    }
}