package com.unitn.lpsmt.group13.pommidori.activities;

import static com.unitn.lpsmt.group13.pommidori.Utility.ACCELEROMETER;
import static com.unitn.lpsmt.group13.pommidori.Utility.PAUSA;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.R;

public class Impostazioni extends AppCompatActivity {

    private static final String TAG = "Impostazioni";

    //Variabili
    Toolbar toolbar;
    Button pausaMeno, pausaPiu;
    TextView durataPausa;
    SwitchCompat switchCompat;
    SharedPreferences sharedPreferences;

    //valore pausa
    int pausa;
    boolean accelerometerIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        //Inizializzazione variabili
        toolbar = findViewById(R.id.settingToolbar);
        pausaMeno = findViewById(R.id.pausa_meno);
        pausaPiu = findViewById(R.id.pausa_piu);
        durataPausa = findViewById(R.id.durata_pausa);
        switchCompat = findViewById(R.id.acelerometer_sensor_switch);
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);

        //Metodi
        loadSharedPreferences();

        if( accelerometerIsActive){
            switchCompat.setChecked( true);
        }else{
            switchCompat.setChecked( false);
        }

        setToolbar();
        setButtonListener();

    }

    protected void setToolbar(){
        //settare titolo e icona del toolbar
        toolbar.setTitle(R.string.impostazioni);
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
                    saveSharedPreferences();
                }
            }
        });

        pausaMeno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pausa>1) { //la pausa deve essere di almeno 1 minuto
                    pausa--;
                    saveSharedPreferences();
                }
            }
        });

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( switchCompat.isChecked()){
                    accelerometerIsActive = true;
                }else{
                    accelerometerIsActive = false;
                }
                saveSharedPreferences();
            }
        });
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PAUSA, pausa);
        editor.putBoolean(ACCELEROMETER, accelerometerIsActive);
        editor.apply();
        durataPausa.setText(Integer.toString(pausa));
    }

     private void loadSharedPreferences(){
        pausa = sharedPreferences.getInt(PAUSA, 5);
        accelerometerIsActive = sharedPreferences.getBoolean(ACCELEROMETER, false);
        durataPausa.setText(Integer.toString(pausa));
     }
}