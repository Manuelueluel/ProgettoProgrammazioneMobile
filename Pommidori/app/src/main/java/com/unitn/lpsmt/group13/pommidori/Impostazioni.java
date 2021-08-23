package com.unitn.lpsmt.group13.pommidori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Impostazioni extends AppCompatActivity {

    //Variabili
    Toolbar toolbar;

    Button pausaMeno, pausaPiu;
    TextView durataPausa;

    //valore provvisorio pausa
    int pausa;

    //Shared Preferances key
    public static final String SHARED_PREFS_SESSIONE = Homepage.SHARED_PREFS_SESSIONE;
    public static final String PAUSA = "pausa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        //Inizializzazione variabili
        toolbar = findViewById(R.id.settingToolbar);

        pausaMeno = findViewById(R.id.pausa_meno);
        pausaPiu = findViewById(R.id.pausa_piu);
        durataPausa = findViewById(R.id.durata_pausa);


        //Metodi
        loadData();
        setToolbar();
        setButtonListener();

    }

    protected void setToolbar(){
        //settare titolo e icona del toolbar
        toolbar.setTitle("Impostazioni");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Impostazioni.this, Homepage.class));
            }
        });
    }

    protected void setButtonListener(){
        pausaPiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pausa<30) { //durata massima pausa 30 minuti
                    pausa++;
                    saveData();
                }
            }
        });
        pausaMeno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pausa>0) { //la pausa deve essere maggiore di 0
                    pausa--;
                    saveData();
                }
            }
        });
    }
    private void saveData() {
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SESSIONE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PAUSA, pausa);
        editor.apply();

        durataPausa.setText(Integer.toString(pausa));
    }
     private void loadData(){
         //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SESSIONE, MODE_PRIVATE);

         pausa = sharedPreferences.getInt(PAUSA, 5);

         durataPausa.setText(Integer.toString(pausa));
     }
}