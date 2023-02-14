package com.unitn.lpsmt.group13.pommidori.services;

import static com.unitn.lpsmt.group13.pommidori.Utility.ACCELEROMETER;
import static com.unitn.lpsmt.group13.pommidori.Utility.COLORE_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.MINUTI_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.N_STARS;
import static com.unitn.lpsmt.group13.pommidori.Utility.ONGOING_COUNTDOWN_NOTIFICATION_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.ORE_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.NOME_ACTIVITY_ASSOCIATA;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_CHANNEL_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIME_MILLIS;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TRIGGERS_WEIGHT;
import static com.unitn.lpsmt.group13.pommidori.Utility.createNotificationChannel;
import static com.unitn.lpsmt.group13.pommidori.Utility.ensureRange;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unitn.lpsmt.group13.pommidori.AccelerometerSensor;
import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.fragments.CountDownTimerFragment;

import java.util.Date;

public class CountDownTimerService extends Service {

    private static final String TAG = "CountDownService";
    public static boolean isRunning = false;

    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countDownTimer;
    private Database database;
    private StatoTimer statoTimer;
    private int oreTimer, minutiTimer;
    private long tempoRimasto;
    private long tempoIniziale;
    private long tempoTrascorso;
    private AccelerometerSensor accelerometerSensor;
    private boolean accelerometerIsActive;
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
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
        this.sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        this.isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground( ONGOING_COUNTDOWN_NOTIFICATION_ID, createNotification());

        loadSharedPreferences();
        statoTimer.setValue( StatoTimer.COUNTDOWN);

        if( accelerometerIsActive){
            accelerometerSensor = new AccelerometerSensor( getBaseContext());
        }

        //Intent update toolbar title
        Intent toolbarIntent = new Intent();
        toolbarIntent.setAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        toolbarIntent.putExtra(TOOLBAR_BUTTONS_STATO_TIMER, R.string.pomodoro_in_corso);
        localBroadcastManager.sendBroadcast(toolbarIntent);

        startForeground(ONGOING_COUNTDOWN_NOTIFICATION_ID, createNotification());

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
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
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

        //Cancello la notifica
        NotificationManagerCompat nmc = NotificationManagerCompat.from( getBaseContext());
        nmc.cancel( ONGOING_COUNTDOWN_NOTIFICATION_ID);

        accelerometerSensor = null;
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

    private Notification createNotification() {

        Intent intent = new Intent( this, CountDownTimerFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        createNotificationChannel( this, TIMER_CHANNEL_ID, getString(R.string.timer_channel_name), "");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.selector_circle_progress)
                .setContentTitle( getString(R.string.timer_notification_title))
                .setContentText( getString(R.string.timer_notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        //Mostra la notifica immediatamente
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify( ONGOING_COUNTDOWN_NOTIFICATION_ID, builder.build());

        return builder.build();
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt( STATO_TIMER, statoTimer.getValue());

        editor.apply();
    }

    private void loadSharedPreferences(){
        oreTimer = sharedPreferences.getInt( ORE_TIMER, 0);
        minutiTimer = sharedPreferences.getInt( MINUTI_TIMER, 30);
        accelerometerIsActive = sharedPreferences.getBoolean(ACCELEROMETER, false);
        tempoRimasto = ((oreTimer*3600) + (minutiTimer*60)) * 1000;
    }

    private boolean addPomodoroCompletato(){
        database = Database.getInstance( this);
        TablePomodoroModel pomodoro = new TablePomodoroModel();

        pomodoro.setName( sharedPreferences.getString(NOME_ACTIVITY_ASSOCIATA, "Nessuna attività"));
        pomodoro.setInizio( new Date( tempoIniziale));
        pomodoro.setDurata( tempoTrascorso);
        pomodoro.setColor( sharedPreferences.getInt(COLORE_ACTIVITY_ASSOCIATA, 0));
        pomodoro.setRating( calculateRating());

        return database.addCompletedPomodoro( pomodoro);
    }

    public long getTempoRimasto(){
        return tempoRimasto;
    }

    private float calculateRating(){
        long durataPrevista = ((oreTimer*3600) + (minutiTimer*60)) * 1000;
        float rapporto = ((float) (tempoTrascorso) / (float) (durataPrevista));
        float rating;

        if( accelerometerIsActive) {
            rating = N_STARS * rapporto - accelerometerSensor.getTriggers() * TRIGGERS_WEIGHT;
        }else{
            rating = N_STARS * rapporto;
        }

        return ensureRange( rating, N_STARS, 0);
    }
}
