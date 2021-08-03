package com.unitn.lpsmt.group13.pommidori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Timer extends AppCompatActivity {

    //Variabili
    Toolbar toolbar;

    TextView ore, minuti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Intent intent = getIntent();

        //Inizializzazione variabili
        toolbar = findViewById(R.id.timerToolbar);

        ore = findViewById(R.id.ore);
        minuti = findViewById(R.id.minuti);

        //settare titolo e icona del toolbar
        toolbar.setTitle("Sessione in corso");

        //Ricevere dati da intent
        String sessionOre = intent.getStringExtra("ore");
        String sessionMin = intent.getStringExtra("minuti");

        Log.d("tempo",sessionOre + "," + sessionMin);

        ore.setText(sessionOre);
        minuti.setText(sessionMin);
    }
}