package com.juandelcid.ace2fp;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NewUser extends AppCompatActivity {
    EditText txtNombre;
    EditText txtApellido;
    EditText txtClave;
    EditText txtCorreo;

    Button btnAgree;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        inicializar();
    }

    private void inicializar() {
        txtNombre = (EditText)findViewById(R.id.txtNombre);
        txtApellido = (EditText)findViewById(R.id.txtApellido);
        txtClave = (EditText)findViewById(R.id.txtClave);
        txtCorreo = (EditText)findViewById(R.id.txtCorreo);

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
        String nombre = txtNombre.getText().toString();
        String apellido = txtApellido.getText().toString();
        String clave = txtApellido.getText().toString();
        String correo = txtApellido.getText().toString();

        //ServicioTask servicioTask= new ServicioTask(this,"http://192.168.0.10:15009/WEBAPIREST/api/persona",apellido,clave, nombre, correo);
        ServicioNewUser servicioTask= new ServicioNewUser(this,"https://apex.oracle.com/pls/apex/ace2g3/duchap/signin",apellido,clave, nombre, correo);
        servicioTask.execute();
    }

    boolean isEmpty(){
        if (txtNombre.getText().length() == 0 ){
            return false;
        }
        else if (txtApellido.getText().length() == 0 ){
            return false;
        }
        else if (txtClave.getText().length() == 0 ){
            return false;
        }
        else if (txtCorreo.getText().length() == 0 ){
            return false;
        }
        return true;
    }
    private void guardarUsuario() {
        if ( isEmpty() ){
            //return;
            String nombre = txtNombre.getText().toString();
            String apellido = txtApellido.getText().toString();
            String clave = txtClave.getText().toString();
            String correo = txtCorreo.getText().toString();

            //ServicioTask servicioTask= new ServicioTask(this,"http://192.168.0.10:15009/WEBAPIREST/api/persona",apellido,clave, nombre, correo);
            ServicioNewUser servicioTask= new ServicioNewUser(this,"https://apex.oracle.com/pls/apex/ace2g3/duchapp/signin",apellido,clave, nombre, correo);
            servicioTask.execute();

            Toast.makeText(getBaseContext(), "Successful process.", Toast.LENGTH_SHORT).show();
            onBackPressed();
            limpiarCampos();
        }
    }

    private void limpiarCampos(){
        txtNombre.setText("");
        txtApellido.setText("");
        txtClave.setText("");
        txtCorreo.setText("");
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
