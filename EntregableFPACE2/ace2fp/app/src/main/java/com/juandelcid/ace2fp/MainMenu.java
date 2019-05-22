package com.juandelcid.ace2fp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;


public class MainMenu extends AppCompatActivity {

    ImageButton btnTakeS;
    ImageButton btnFriend;
    ImageButton btnSavedW;
    ImageButton btnRep;

    RadioButton rbtnMan;
    RadioButton rbtnAut;
    RadioButton rbtnSaver;

    int DUCH_TYPE = 0;

    String USUARIO_T = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        inicializar();
    }
    @SuppressLint("HandlerLeak")
    private void inicializar(){
        USUARIO_T = getIntent().getStringExtra("USUARIO_T");;

        rbtnMan = (RadioButton)findViewById(R.id.rbtnMan);
        rbtnMan.setChecked(true);
        rbtnMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DUCH_TYPE = 0;
            }
        });
        rbtnAut = (RadioButton)findViewById(R.id.rbtnAut);
        rbtnAut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DUCH_TYPE = 1;
            }
        });
        rbtnSaver = (RadioButton)findViewById(R.id.rbtnSaver);
        rbtnSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DUCH_TYPE = 2;
            }
        });


        btnTakeS = (ImageButton)findViewById(R.id.btnTakeS);
        btnTakeS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DUCH_TYPE == 0 ){
                    Intent newInt = new Intent( MainMenu.this, ManualShower.class);
                    startActivity( newInt );
                }
                else if (DUCH_TYPE == 1 ){
                    Intent newInt = new Intent( MainMenu.this, AutomaticShower.class);
                    startActivity( newInt );
                }
                else if (DUCH_TYPE == 2 ){
                    Intent newInt = new Intent( MainMenu.this, SaveShower.class);
                    startActivity( newInt );
                }

            }
        });
        btnFriend = (ImageButton)findViewById(R.id.btnFriend);
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newInt = new Intent( MainMenu.this, Friend.class);
                startActivity( newInt );
            }
        });
        btnSavedW = (ImageButton)findViewById(R.id.btnSavedW);
        btnSavedW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent newInt = new Intent(MainMenu.this, ReportSavedWater.class);
                //startActivity( newInt );
            }
        });
        btnRep = (ImageButton)findViewById(R.id.btnRep);
        btnRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* codigo para conectar a la pantalla led*/
                //Intent dispBT = new Intent(MsgMenuPrincipal.this, MsgDispositivosBT.class);
                //startActivity(dispBT);
                Intent newInt = new Intent(MainMenu.this, Report.class);
                startActivity( newInt );
            }
        });

    }
}
