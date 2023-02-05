package com.unitn.lpsmt.group13.pommidori.activities;

import static com.unitn.lpsmt.group13.pommidori.Utility.POST_NOTIFICATIONS_PERMISSION_CODE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Utility;

public class Impostazioni extends AppCompatActivity {

    private static final String TAG = "Impostazioni";

    //Variabili
    Toolbar toolbar;
    Button pausaMeno, pausaPiu;
    TextView durataPausa;
    SharedPreferences sharedPreferences;

    //valore pausa
    int pausa;

    //Shared Preferances key
    private final String SHARED_PREFS_SESSIONE = Utility.SHARED_PREFS_TIMER;
    private final String PAUSA = Utility.PAUSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impostazioni);

        //Inizializzazione variabili
        toolbar = findViewById(R.id.settingToolbar);
        pausaMeno = findViewById(R.id.pausa_meno);
        pausaPiu = findViewById(R.id.pausa_piu);
        durataPausa = findViewById(R.id.durata_pausa);
        //Aprire/creare il file xml "SHARED_PREF" in modalità privata (solo questa applicazione può accedervi)
        sharedPreferences = getSharedPreferences(SHARED_PREFS_SESSIONE, MODE_PRIVATE);


        //Metodi
        loadData();
        setToolbar();
        setButtonListener();

        //Utility.createNotificationChannel( getBaseContext(), Utility.TIMER_CHANNEL_ID, getString(R.string.timer_channel_name), "");
        //requestPermission( Manifest.permission.POST_NOTIFICATIONS);

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
                    saveData();
                }
            }
        });

        pausaMeno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pausa>1) { //la pausa deve essere di almeno 1 minuto
                    pausa--;
                    saveData();
                }
            }
        });
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PAUSA, pausa);
        editor.apply();

        durataPausa.setText(Integer.toString(pausa));
    }

     private void loadData(){
         pausa = sharedPreferences.getInt(PAUSA, 5);

         durataPausa.setText(Integer.toString(pausa));
     }
}