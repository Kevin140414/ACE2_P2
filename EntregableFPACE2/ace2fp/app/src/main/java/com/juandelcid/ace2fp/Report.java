package com.juandelcid.ace2fp;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Report extends AppCompatActivity {

    Button btnHistorial;
    Button btnPromPro;
    Button btnPromAgen;
    ListView listReport;

    public ArrayList<String[]> items;
    ListView ListaItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        btnHistorial = (Button) findViewById(R.id.btnHistorial);
        btnPromPro = (Button) findViewById(R.id.btnPromPro);
        btnPromAgen = (Button) findViewById(R.id.btnProAgen);
        ListaItems = (ListView) findViewById(R.id.lstReport);

        btnPromAgen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URLConnect.ruta = "promedioagenda/" + VentanaPrincipal.id_user;
                new URLConnect.SendPostRequest().execute();

                items = new ArrayList<String[]>();
                try {
                    JSONObject jsonObj = new JSONObject(URLConnect.data);
                    JSONArray jsonArray = jsonObj.getJSONArray("items");

                    for(int j = 0; j < jsonArray.length(); j++){
                        String data[] = new String[4];
                        data[0] = jsonArray.getJSONObject(j).getString("promediototal");
                        data[1] = jsonArray.getJSONObject(j).getString("promedioefectivo");
                        data[2] = jsonArray.getJSONObject(j).getString("promediolitros");
                        data[3] = jsonArray.getJSONObject(j).getString("nombre");
                    }

                    String values[] = new String[items.size()];

                    for(int k = 0; k < items.size(); k++)
                        values[k] = "Nombre: " + items.get(k)[3] + '\n' +
                                "Promedio de tiempo total: " + items.get(k)[0] + '\n' +
                                "Promedio de tiempo efectivo: " + items.get(k)[1] + '\n' +
                                "`Promedio de litros: " + items.get(k)[2];

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, values);
                    ListaItems.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnPromPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URLConnect.ruta = "promedio/" + VentanaPrincipal.id_user;
                new URLConnect.SendPostRequest().execute();

                items = new ArrayList<String[]>();
                try {
                    JSONObject jsonObj = new JSONObject(URLConnect.data);
                    JSONArray jsonArray = jsonObj.getJSONArray("items");

                    for(int j = 0; j < jsonArray.length(); j++){
                        String data[] = new String[3];
                        data[0] = jsonArray.getJSONObject(j).getString("promediototal");
                        data[1] = jsonArray.getJSONObject(j).getString("promedioefectivo");
                        data[2] = jsonArray.getJSONObject(j).getString("promediolitros");
                    }

                    String values[] = new String[items.size()];

                    for(int k = 0; k < items.size(); k++)
                        values[k] = "Promedio de tiempo total: " + items.get(k)[0] + '\n' +
                                "Promedio de tiempo efectivo: " + items.get(k)[1] + '\n' +
                                "`Promedio de litros: " + items.get(k)[2];

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, values);
                    ListaItems.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URLConnect.ruta = "registros/" + VentanaPrincipal.id_user;
                new URLConnect.SendPostRequest().execute();

                items = new ArrayList<String[]>();
                try {
                    JSONObject jsonObj = new JSONObject(URLConnect.data);
                    JSONArray jsonArray = jsonObj.getJSONArray("items");

                    for(int j = 0; j < jsonArray.length(); j++){
                        String data[] = new String[6];
                        data[0] = jsonArray.getJSONObject(j).getString("fecha");
                        data[1] = jsonArray.getJSONObject(j).getString("hora");
                        data[2] = jsonArray.getJSONObject(j).getString("tiempototal");
                        data[3] = jsonArray.getJSONObject(j).getString("tiempoefectivo");
                        data[4] = jsonArray.getJSONObject(j).getString("litros");

                        double num1 = Integer.parseInt(data[2]);
                        double num2 = Integer.parseInt(data[3]);
                        double porcentaje = ((num1 - num2)/num1) * 100;
                        data[5] = String.valueOf(porcentaje);
                        items.add(data);
                    }

                    String values[] = new String[items.size()];

                    for(int k = 0; k < items.size(); k++)
                        values[k] = "Fecha: "+ items.get(k)[0] + '\n' + "Hora: " + items.get(k)[1] + '\n' +
                                    "Tiempo Total Ducha: " + items.get(k)[2] + '\n' +
                                    "Tiempo Efectivo Ducha: " + items.get(k)[3] + '\n' +
                                    "Litros de agua: " + items.get(k)[4] + '\n' +
                                    "Porcentaje de ahorro: " + String.valueOf(Math.floor(Double.valueOf(items.get(k)[5]) * 100) / 100) + "%";

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, values);
                    ListaItems.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
