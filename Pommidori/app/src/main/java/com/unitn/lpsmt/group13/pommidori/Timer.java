package com.unitn.lpsmt.group13.pommidori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unitn.lpsmt.group13.pommidori.fragments.BottomDialogFragment;

import java.util.Locale;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class Timer extends AppCompatActivity {

    //Variabili
    private Toolbar toolbar;
    private FloatingActionButton btnPausa;
    private FloatingActionButton btnStop;
    private MaterialProgressBar progressBar;
    private TextView timer;
    private CountDownTimer countDownTimer;      //Timer a conto alla rovescia
    private CountUpTimer countUpTimer;          //Timer a contatore

    //Tutti i tempi long sono espressi in milli secondi
    private long tempoIniziale;
    private long tempoRimasto;
    private long tempoFinale;
    private long tempoTrascorso;
    private final long DURATA_MASSIMA_COUNTUP_TIMER = Utility.DURATA_MASSIMA_COUNTUP_TIMER;
    private StatoSessione statoSessione;
    private StatoSessione statoSessionePrecedentePausa;

    //I timer per essere persistenti anche con l'activity chiusa necessitano di salvare delle informazioni nelle shared preferences
    private final String SHARED_PREFS_SESSIONE = Utility.SHARED_PREFS_SESSIONE;
    private final String ORE_SESSIONE = Utility.ORE_SESSIONE;
    private final String MINUTI_SESSIONE = Utility.MINUTI_SESSIONE;
    private final String TEMPO_INIZIALE = Utility.TEMPO_INIZIALE;
    private final String TEMPO_RIMASTO = Utility.TEMPO_RIMASTO;
    private final String TEMPO_FINALE = Utility.TEMPO_FINALE;
    private final String TEMPO_TRASCORSO = Utility.TEMPO_TRASCORSO;
    private final String STATO_SESSIONE = Utility.STATO_SESSIONE;
    private final String STATO_SESSIONE_PRECEDENTE_PAUSA = Utility.STATO_SESSIONE_PRECEDENTE_PAUSA;
    private final String PAUSA = Utility.PAUSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //Inizializzazione variabili
        toolbar = findViewById(R.id.timerToolbar);
        btnPausa = findViewById(R.id.pausa_timer_fab);
        btnStop = findViewById(R.id.stop_timer_fab);
        progressBar = findViewById(R.id.progress_bar);
        timer = findViewById(R.id.timer);

        //Settaggio titolo toolbar
        toolbar.setTitle(R.string.sessione_in_corso);

        //Metodi
        setButtonListener();

    }

    //Salva i dati nelle shared preferences
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences( SHARED_PREFS_SESSIONE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong( TEMPO_INIZIALE, tempoIniziale);
        editor.putLong( TEMPO_RIMASTO, tempoRimasto);
        editor.putLong( TEMPO_FINALE, tempoFinale);
        editor.putLong( TEMPO_TRASCORSO, tempoTrascorso);
        editor.putInt( STATO_SESSIONE, statoSessione.getValue());
        editor.putInt( STATO_SESSIONE_PRECEDENTE_PAUSA, statoSessionePrecedentePausa.getValue());
        editor.apply();
    }

    //Carica i dati dalle shared preferences
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences( SHARED_PREFS_SESSIONE, MODE_PRIVATE);
        tempoIniziale = sharedPreferences.getLong( TEMPO_INIZIALE, 1800000);
        tempoRimasto = sharedPreferences.getLong( TEMPO_RIMASTO, tempoIniziale);
        tempoFinale = sharedPreferences.getLong( TEMPO_FINALE, 0);
        tempoTrascorso = sharedPreferences.getLong( TEMPO_TRASCORSO, 0);
        statoSessione = new StatoSessione( sharedPreferences.getInt( STATO_SESSIONE, StatoSessione.DISATTIVO));
        statoSessionePrecedentePausa = new StatoSessione( sharedPreferences.getInt( STATO_SESSIONE_PRECEDENTE_PAUSA, StatoSessione.DISATTIVO));
    }

    private void setButtonListener() {
        btnPausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPausa();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopSessione();
                reset();    //Metodo momentaneo
            }
        });

    }

    /*  Metodo invocato all'avvio o al rientro in activity, permette la selezione del timer corretto
        recuperando i dati salvati del tempo già trascorso per avviare poi un nuovo timer.
    * */
    @Override
    protected void onStart() {
        super.onStart();
        loadData();

        //Countdown e Pausa usano lo stesso metodo startCountDownTimer() per il conteggio del tempo
        if( statoSessione.getValue() == StatoSessione.COUNTDOWN || statoSessione.getValue() == StatoSessione.PAUSA){
            aggiornaCountDownTimer();
            tempoRimasto = tempoFinale - System.currentTimeMillis();    //Calcolo tempo rimanente
            if( tempoRimasto < 0){
                tempoRimasto = 0;
                statoSessione.setValue( StatoSessione.DISATTIVO);
                aggiornaCountDownTimer();
            }else{
                startCountDownTimer();
            }

        }else if( statoSessione.getValue() == StatoSessione.COUNTUP){   //Gestione CountUpTimer

            //Se tempoTrascorso == 0 significa che è un nuovo timer, non è necessario calcolare il tempo trascorso con l'activity chiusa
            if(tempoTrascorso != 0){
                //tempoFinale = System.currentTimeMillis() nel momento della onStop()
                //Calcolo che considera il tempo trascorso con l'activity chiusa
                tempoTrascorso = tempoTrascorso + (System.currentTimeMillis() - tempoFinale);
            }

            startCountUpTimer();
        }
    }

    //Cancella i timer in corso, salva le informazioni dei timer
    @Override
    protected void onStop() {
        super.onStop();

        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        if(countUpTimer != null){
            countUpTimer.cancel();
            tempoFinale = System.currentTimeMillis();   //Si salva il momento di chiusura dell'activity
        }

        saveData();
    }

    //Avvia un countdown timer
    private void startCountDownTimer(){
        tempoFinale = System.currentTimeMillis() + tempoRimasto;

        //Oggetto countdown timer, con tempo rimanente alla fine, metodo onTick richiamato ogni 1000 millisecondi
        countDownTimer = new CountDownTimer( tempoRimasto, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempoRimasto = millisUntilFinished;
                aggiornaCountDownTimer();
            }

            @Override
            public void onFinish() {
                //Fine countdowntimer, si controlla quale sia il suo stato attuale
                switch ( statoSessione.getValue()){

                    //In caso sia un countdowntimer, si salva tale stato e poi si fa partire una pausa
                    case StatoSessione.COUNTDOWN:
                        startPausa();
                        break;

                    //In caso sia finita una pausa, si controlla lo stato precedente e si ri esegue quel tipo di timer
                    //Si evita che una pausa possa essere seguita da un'altra pausa
                    case StatoSessione.PAUSA:
                        btnPausa.setEnabled(true);
                        toolbar.setTitle(R.string.sessione_in_corso);

                        if( statoSessionePrecedentePausa.getValue() == StatoSessione.COUNTDOWN){
                            //Riottieni i dati per il countdown e lo si avvia
                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SESSIONE, MODE_PRIVATE);
                            int ore = sharedPreferences.getInt(ORE_SESSIONE, 0);
                            int min = sharedPreferences.getInt(MINUTI_SESSIONE, 30);
                            long milliSecondi = ((ore*3600) +  (min*60)) * 1000;

                            tempoIniziale = milliSecondi;
                            tempoRimasto = milliSecondi;
                            tempoFinale = System.currentTimeMillis() + milliSecondi;
                            statoSessione.setValue( StatoSessione.COUNTDOWN);
                            startCountDownTimer();
                        }else{
                            startCountUpTimer();
                        }
                        break;

                    default:
                        break;
                }
            }
        }.start();
    }

    //Avvia un countup timer
    private void startCountUpTimer() {
        countUpTimer = new CountUpTimer( DURATA_MASSIMA_COUNTUP_TIMER) {
            @Override
            public void onTick(int sec) {
                tempoTrascorso = tempoTrascorso + 1000;
                if(tempoTrascorso >= DURATA_MASSIMA_COUNTUP_TIMER){
                    //CountUpTimer arriva a durata massima, interrompi sessione
                    stopSessione();
                }else{
                    aggiornaCountUpTimer();
                }
            }
        };

        countUpTimer.start();
    }

    //Avvia un countdown timer per una pausa, modificando dati salvati e alcune views
    private void startPausa(){
        //Disattivo bottone pausa
        btnPausa.setEnabled(false);

        toolbar.setTitle(R.string.pausa_in_corso);

        //Cancellare l'attuale timer
        if( countDownTimer != null){
            countDownTimer.cancel();
        }
        if( countUpTimer != null){
            countUpTimer.cancel();
        }

        //Recuperare durata pausa
        SharedPreferences sharedPreferences = getSharedPreferences( SHARED_PREFS_SESSIONE, MODE_PRIVATE);
        long pausaMilliSecondi = 60000 * sharedPreferences.getInt(PAUSA, 5);

        //Reimpostare i tempi con quelli della pausa e salvarli
        tempoIniziale = pausaMilliSecondi;
        tempoRimasto = pausaMilliSecondi;
        tempoFinale = System.currentTimeMillis() + tempoRimasto;
        tempoTrascorso = 0;     //Il countUpTimer dovrà ripartire da 0 dopo la pausa
        //Salvare lo stato precedente e impostare quello attuale a pausa
        statoSessionePrecedentePausa.setValue( statoSessione.getValue());
        statoSessione.setValue( StatoSessione.PAUSA);
        saveData();

        //Riavviare un countdowntimer per la pausa
        startCountDownTimer();
    }

    //Aggiorna il countdown timer, formattando una stringa in modo opportuno
    private void aggiornaCountDownTimer() {
        String tempoFormattato;
        int ore = (int) (tempoRimasto / 1000) / 3600;
        int minuti = (int) ((tempoRimasto / 1000) % 3600) / 60;
        int secondi = (int) (tempoRimasto / 1000) % 60;

        if( ore > 0){
            tempoFormattato = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", ore, minuti, secondi);
        }else{
            tempoFormattato = String.format(Locale.getDefault(),
                    "%02d:%02d", minuti, secondi);
        }

        timer.setText( tempoFormattato);
    }

    //Aggiorna il countup timer, formattando una stringa in modo opportuno
    private void aggiornaCountUpTimer() {
        String tempoFormattato;
        int ore = (int) (tempoTrascorso / 1000) / 3600;
        int minuti = (int) ((tempoTrascorso / 1000) % 3600) / 60;
        int secondi = (int) (tempoTrascorso / 1000) % 60;

        if( ore > 0){
            tempoFormattato = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", ore, minuti, secondi);
        }else{
            tempoFormattato = String.format(Locale.getDefault(),
                    "%02d:%02d", minuti, secondi);
        }

        timer.setText( tempoFormattato);
    }

    //Ferma qualsiasi timer e apre il dialog di valutazione
    private void stopSessione() {
        //TODO stop sessione
    }

    //Funzione puramente di debug, chiude i timer in corso resettando lo statoSessione e riportando alla homepage
    private void reset(){
        statoSessione.setValue( StatoSessione.DISATTIVO);
        if( countDownTimer != null){
            countDownTimer.cancel();
        }
        if( countUpTimer != null){
            countUpTimer.cancel();
        }

        saveData();
        Intent i = new Intent( Timer.this, Homepage.class);
        startActivity( i);

    }
}