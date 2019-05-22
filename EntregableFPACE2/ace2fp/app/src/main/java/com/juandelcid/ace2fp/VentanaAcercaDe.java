package com.juandelcid.ace2fp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VentanaAcercaDe extends AppCompatActivity {
    Button btnAgradece;
    TextView tvVersi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_acerca_de);
        incicializar();
    }
    private void incicializar(){
        tvVersi = (TextView)findViewById(R.id.tvSalmo);
        btnAgradece = (Button)findViewById(R.id.btnAgradece);
        btnAgradece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText( getApplicationContext(), R.string.msg_Versi, Toast.LENGTH_LONG).show();
                tvVersi.setText( "Thanks.");


            }
        });
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
