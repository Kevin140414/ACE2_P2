package com.juandelcid.ace2fp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Friend extends AppCompatActivity {
    ImageButton btnLstContact;
    ImageButton btnNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        inicializar();
    }
    void inicializar(){
        btnLstContact = (ImageButton)findViewById(R.id.btnLstContact);
        btnLstContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newInt = new Intent(Friend.this, ListContact.class);
                startActivity( newInt );
            }
        });
        btnNewContact = (ImageButton)findViewById(R.id.btnNewContact);
        btnNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newInt = new Intent(Friend.this, NewContac.class);
                startActivity( newInt );
            }
        });
    }
}
