package com.juandelcid.ace2fp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class VentanaPrincipal extends AppCompatActivity {
    public static int id_user = 0;


    Button btnIngresar;
    Button btnAcerca;
    Button btnNewUser;

    EditText txtUser;
    EditText txtPass;


    String USUARIO_T;
    int privileg = 1;
    int itemSpin = 0;


    /* PARA REALIZAR LOGGIN*/
    private String userName;
    private String userPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_principal);

        inicializar();
        ingresar();
    }
    private void inicializar(){
        txtUser = (EditText)findViewById(R.id.txtUser);
        txtPass = (EditText)findViewById(R.id.txtPass);
        if( txtUser.requestFocus()) { getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE); }
    }
    private void ingresar() {
        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**  **/
                //URLConnect.metodo = "GET";
                URLConnect.postDataParams = new JSONObject();
                URLConnect.ruta = "login/" + txtUser.getText().toString() + "/" + txtPass.getText().toString();
                new URLConnect.SendPostRequest().execute();

                try {
                    JSONObject jsonObj = new JSONObject(URLConnect.data);
                    jsonObj = jsonObj.getJSONArray("items").getJSONObject(0);

                    if(jsonObj.length() > 0){
                        id_user = jsonObj.getInt("idusuario");
                    }

                    if(id_user != 0){
                        Intent mainW = new Intent(VentanaPrincipal.this, MainMenu.class);
                        startActivity(mainW);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "El correo que ingreso no se encuentra registrado en la plataforma.",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        btnNewUser = (Button) findViewById(R.id.btnNewUser);
        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** **/
                Intent newUser = new Intent(VentanaPrincipal.this, NewUser.class);
                startActivity(newUser);
            }
        });
        btnAcerca = (Button) findViewById(R.id.btnAgradece);
        btnAcerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* CODIGO */
                Intent about = new Intent(VentanaPrincipal.this, VentanaAcercaDe.class);
                startActivity( about );

            }
        });
    }

    private void limpiarCampos(){
        txtUser.setText("");
        txtPass.setText("");
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
