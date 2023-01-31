package com.unitn.lpsmt.group13.pommidori.services;

import static com.unitn.lpsmt.group13.pommidori.Utility.COLORE_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.DURATA_MASSIMA_COUNTUP_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.NOME_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIME_MILLIS;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_STATO_TIMER;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unitn.lpsmt.group13.pommidori.CountUpTimer;
import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;

import java.util.Date;

public class CountUpTimerService extends Service {

    private static final String TAG = "CountUpService";
    public static boolean isRunning = false;

    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences sharedPreferences;
    private CountUpTimer countUpTimer;
    private Database database;
    private StatoTimer statoTimer;
    private long tempoIniziale;
    private long tempoTrascorso;
    private final IBinder binder = new CountUpTimerBinder();

    public class CountUpTimerBinder extends Binder{
        public CountUpTimerService getService( Context context){
            //Return this instance of CountUpTimerService so clients can call public methods
            return CountUpTimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        this.tempoIniziale = System.currentTimeMillis();
        this.tempoTrascorso = 0;
        this.statoTimer = new StatoTimer( StatoTimer.COUNTUP);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        statoTimer.setValue( StatoTimer.PAUSA);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        //Intent update toolbar title
        Intent toolbarIntent = new Intent();
        toolbarIntent.setAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        toolbarIntent.putExtra(TOOLBAR_BUTTONS_STATO_TIMER, R.string.pomodoro_in_corso);
        localBroadcastManager.sendBroadcast(toolbarIntent);

        countUpTimer = new CountUpTimer( DURATA_MASSIMA_COUNTUP_TIMER) {
            @Override
            public void onTick(int sec) {
                tempoTrascorso = tempoTrascorso + 1000;
                if(tempoTrascorso >= DURATA_MASSIMA_COUNTUP_TIMER){
                    //CountUpTimer arriva a durata massima, interrompi pomodoro
                    stopSelf();

                }else{
                    Intent intent = new Intent();
                    intent.setAction(TIMER_ACTION_INTENT);
                    intent.putExtra(TIME_MILLIS, tempoTrascorso);
                    localBroadcastManager.sendBroadcast( intent);
                }
            }
        };

        countUpTimer.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        addPomodoroCompletato();
        statoTimer.setValue( StatoTimer.DISATTIVO);
        if( countUpTimer != null){
            countUpTimer.cancel();
        }
        saveSharedPreferences();

        Intent intent = new Intent();
        intent.setAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        intent.putExtra(TOOLBAR_BUTTONS_STATO_TIMER, R.string.pomodoro_disattivo);
        localBroadcastManager.sendBroadcast(intent);

        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void saveSharedPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt( STATO_TIMER, statoTimer.getValue());

        editor.apply();
    }

    private boolean addPomodoroCompletato(){
        database = Database.getInstance( this);
        TablePomodoroModel pomodoro = new TablePomodoroModel();

        pomodoro.setName( sharedPreferences.getString(NOME_ACTIVITY_ASSOCIATA, "Nessuna attività"));
        pomodoro.setInizio( new Date( tempoIniziale));
        pomodoro.setDurata( tempoTrascorso);
        pomodoro.setColor( sharedPreferences.getInt(COLORE_ACTIVITY_ASSOCIATA, 0));

        return database.addCompletedPomodoro( pomodoro);
    }

    public long getTempoTrascorso(){
        return tempoTrascorso;
    }
}
