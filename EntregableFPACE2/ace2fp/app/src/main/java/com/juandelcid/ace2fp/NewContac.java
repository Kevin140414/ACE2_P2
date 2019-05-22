package com.juandelcid.ace2fp;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewContac extends AppCompatActivity {
    EditText txtCorreo;
    EditText txtIDUser;

    Button btnAgree;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contac);
        inicializar();
    }

    private void inicializar() {
        txtCorreo = (EditText)findViewById(R.id.txtCorreo);
        txtIDUser = (EditText)findViewById(R.id.txtIDUser);

        btnAgree = (Button)findViewById(R.id.btnAgree);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarUsuario();
                //consumirServicio();
            }
        });
    }
    public void consumirServicio(){
        // ahora ejecutaremos el hilo creado
        String correo = txtCorreo.getText().toString();
        String idusuario = txtIDUser.getText().toString();

        ServicioNewContact servicioTask= new ServicioNewContact(this,"https://apex.oracle.com/pls/apex/ace2g3/open-api-catalog/duchapp/contacto",correo,idusuario);
        servicioTask.execute();



    }
    boolean isEmpty(){
        if (txtCorreo.getText().length() == 0 ){
            return false;
        }
        else if (txtIDUser.getText().length() == 0 ){
            return false;
        }
        return true;
    }
    private void guardarUsuario() {
        if ( isEmpty() ){
            //return;
            String correo = txtCorreo.getText().toString();
            String idusuario = txtIDUser.getText().toString();

            //ServicioTask servicioTask= new ServicioTask(this,"http://192.168.0.10:15009/WEBAPIREST/api/persona",apellido,clave, nombre, correo);
            ServicioNewContact servicioTask= new ServicioNewContact(this,"https://apex.oracle.com/pls/apex/ace2g3/open-api-catalog/duchapp/contacto/",correo,idusuario);
            servicioTask.execute();

            Toast.makeText(getBaseContext(), "Successful process.", Toast.LENGTH_SHORT).show();
            onBackPressed();
            limpiarCampos();
        }
    }

    private void limpiarCampos(){
        txtCorreo.setText("");
        txtIDUser.setText("");
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
