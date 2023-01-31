package com.unitn.lpsmt.group13.pommidori.services;

import static com.unitn.lpsmt.group13.pommidori.Utility.COLORE_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.MINUTI_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.ORE_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.NOME_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIME_MILLIS;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_STATO_TIMER;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;

import java.util.Date;

public class CountDownTimerService extends Service {

    private static final String TAG = "CountDownService";
    public static boolean isRunning = false;

    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countDownTimer;
    private Database database;
    private StatoTimer statoTimer;
    private long tempoRimasto;
    private long tempoIniziale;
    private long tempoTrascorso;
    private final IBinder binder = new CountDownTimerBinder();

    public class CountDownTimerBinder extends Binder{
        public CountDownTimerService getService( Context context){
            //Return this instance of CountDownTimerService so clients can call public methods
            return CountDownTimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        this.tempoIniziale = System.currentTimeMillis();
        this.tempoTrascorso = 0;
        this.statoTimer = new StatoTimer( StatoTimer.COUNTDOWN);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadSharedPreferences();
        statoTimer.setValue( StatoTimer.COUNTDOWN);

        //Intent update toolbar title
        Intent toolbarIntent = new Intent();
        toolbarIntent.setAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        toolbarIntent.putExtra(TOOLBAR_BUTTONS_STATO_TIMER, R.string.pomodoro_in_corso);
        localBroadcastManager.sendBroadcast(toolbarIntent);

        countDownTimer = new CountDownTimer(  tempoRimasto, 1000) {
            @Override
            public void onTick(long l) {
                tempoRimasto = l;
                tempoTrascorso = tempoTrascorso + 1000;
                Intent intent = new Intent();
                intent.setAction(TIMER_ACTION_INTENT);
                intent.putExtra(TIME_MILLIS, tempoRimasto);
                localBroadcastManager.sendBroadcast( intent);
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        addPomodoroCompletato();

        statoTimer.setValue( StatoTimer.DISATTIVO);
        if( countDownTimer != null){
            countDownTimer.cancel();
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

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt( STATO_TIMER, statoTimer.getValue());

        editor.apply();
    }

    private void loadSharedPreferences(){
        int oreTimer = sharedPreferences.getInt( ORE_TIMER, 0);
        int minutiTimer = sharedPreferences.getInt( MINUTI_TIMER, 30);
        tempoRimasto = ((oreTimer*3600) + (minutiTimer*60)) * 1000;
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

    public long getTempoRimasto(){
        return tempoRimasto;
    }
}
