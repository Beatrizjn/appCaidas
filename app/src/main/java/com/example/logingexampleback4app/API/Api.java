package com.example.logingexampleback4app.API;

import androidx.collection.ArraySet;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class Api {

    private static ArraySet<ParseObject> lista;
    //private final List<DatosUsuario> lista = new LinkedList<DatosUsuario>();

    public ArraySet<ParseObject> getLista() {
        return lista;
    }

    public static void getAllData()
    {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DatosUsuario");

        query.findInBackground( (object, e) ->{
                if(e == null)
                {
                    guardaLista(object);
                }
                else
                {
                }
        });

        return;
    }

    private static void guardaLista(List<ParseObject> list)
    {
        if(list == null || list.isEmpty())
        {
            return;
        }
        else
        {
            for(int i = 0; i < list.size(); i++)
            {
                lista.add(list.get(i));
            }
        }
    }

}
