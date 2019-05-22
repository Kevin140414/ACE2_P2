package com.juandelcid.ace2fp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListContact extends AppCompatActivity {

    public ArrayList<String[]> items;
    ListView ListaItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contact);

        ListaItems = (ListView) findViewById(R.id.lstContacts);

        for(int i = 0; i < 2; i++){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            URLConnect.ruta = "contactos/" + VentanaPrincipal.id_user;
            new URLConnect.SendPostRequest().execute();

            items = new ArrayList<String[]>();
            try {
                JSONObject jsonObj = new JSONObject(URLConnect.data);
                JSONArray jsonArray = jsonObj.getJSONArray("items");

                for(int j = 0; j < jsonArray.length(); j++){
                    String data[] = new String[4];
                    data[0] = jsonArray.getJSONObject(j).getString("nombre");
                    data[1] = jsonArray.getJSONObject(j).getString("promediototal");
                    data[2] = jsonArray.getJSONObject(j).getString("promedioefectivo");

                    double num1 = Integer.parseInt(data[1].split(":")[0]) * 60 + Integer.parseInt(data[1].split(":")[1]);
                    double num2 = Integer.parseInt(data[2].split(":")[0]) * 60 + Integer.parseInt(data[2].split(":")[1]);
                    double porcentaje = ((num1 - num2)/num1) * 100;
                    data[3] = String.valueOf(porcentaje);
                    items.add(data);
                }

                String values[] = new String[items.size()];

                for(int k = 0; k < items.size(); k++)
                    values[k] = "Nombre: "+ items.get(k)[0] + '\n' + "Tiempo Total Ducha: " + items.get(k)[1] +
                            '\n' + "Tiempo Efectivo Ducha: " + items.get(k)[2] + '\n' +
                            "Porcentaje de ahorro: " + String.valueOf(Math.floor(Double.valueOf(items.get(k)[3]) * 100) / 100) + "%";

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
                ListaItems.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
