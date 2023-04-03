package com.example.logingexampleback4app.Telegram;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendLocation;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.io.IOException;

import okhttp3.OkHttpClient;

public class TelegramExecutes {

    OkHttpClient client = null;
    TelegramBot bot = null;
    Long chatId;
    int mensajeDiferente;
    private String myId;
    String respuesta = "igual";

    public TelegramExecutes()
    {
        if(client == null)
            client = new OkHttpClient();
        if(bot == null)
            bot = new TelegramBot.Builder("5908330497:AAGUu9mSRLyT9pGXypIbEJIQRkIxOL3c2PU").okHttpClient(client).build();
        chatId = Long.parseLong("-1001747594999");
    }

    public String dameTextoChatNuevo() throws InterruptedException {

        bot.execute(new GetUpdates(), new Callback<GetUpdates, GetUpdatesResponse>() {
            @Override
            public void onResponse(GetUpdates request, GetUpdatesResponse response) {
                Update update = null;
                if(response != null)
                {
                    if( response.updates().size() > 0) {
                        update = (Update) response.updates().get(response.updates().size() - 1);

                        if (mensajeDiferente != update.message().messageId()) {
                            mensajeDiferente = update.message().messageId();
                            respuesta = update.message().text();
                        } else {
                            respuesta = "igual";
                        }
                    }
                }

            }

            @Override
            public void onFailure(GetUpdates request, IOException e) {

            }
        });

        Thread.sleep(2000);

        return respuesta;
    }

    public void enviaLocalizacion(float longitud, float latitud)
    {
        bot.execute(new SendLocation(chatId,latitud,longitud ), new Callback() {
            @Override
            public void onResponse(BaseRequest request, BaseResponse response) {

            }
            @Override
            public void onFailure(BaseRequest request, IOException e) {

            }
        });

    }

    public void enviaTexto(String texto, float longitud, float latitud)
    {

        bot.execute(new SendMessage(chatId, texto + longitud + "," + latitud ), new Callback() {
            @Override
            public void onResponse(BaseRequest request, BaseResponse response) {

            }
            @Override
            public void onFailure(BaseRequest request, IOException e) {

            }
        });

    }
}
