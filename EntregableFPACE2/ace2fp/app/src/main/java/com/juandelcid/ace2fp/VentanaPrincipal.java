package com.juandelcid.ace2fp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class VentanaPrincipal extends AppCompatActivity {
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
                Intent mainW = new Intent(VentanaPrincipal.this, MainMenu.class);
                startActivity(mainW);
                limpiarCampos();
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
