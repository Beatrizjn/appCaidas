package com.example.logingexampleback4app.Aplicacion;

import android.app.Application;
import android.view.View;

import com.example.logingexampleback4app.R;
import com.parse.Parse;
import com.parse.ParseInstallation;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}
