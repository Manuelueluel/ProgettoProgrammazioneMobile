package com.unitn.lpsmt.group13.pommidori.services;

import static com.unitn.lpsmt.group13.pommidori.Utility.END_OF_PAUSA_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.END_OF_PAUSA_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.ONGOING_COUNTDOWN_NOTIFICATION_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.ONGOING_COUNTUP_NOTIFICATION_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.ONGOING_PAUSA_NOTIFICATION_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.PAUSA;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER_PRECEDENTE;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_CHANNEL_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIME_MILLIS;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.createNotificationChannel;

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

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.fragments.CountUpTimerFragment;

public class PausaTimerService extends Service {

    private static final String TAG = "PausaTimerService";
    public static boolean isRunning = false;

    private LocalBroadcastManager localBroadcastManager;
    private SharedPreferences sharedPreferences;
    private CountDownTimer countdownTimer;
    private StatoTimer statoTimer;
    private StatoTimer statoTimerPrecedente;
    private long tempoRimasto;
    private final IBinder binder = new PausaTimerBinder();

    public class PausaTimerBinder extends Binder{
        public PausaTimerService getService( Context context){
            //Return this instance of PausaTimerService so clients can call public methods
            return PausaTimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        statoTimer = new StatoTimer( StatoTimer.PAUSA);
        statoTimerPrecedente = new StatoTimer();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground( ONGOING_PAUSA_NOTIFICATION_ID, createNotification());

        loadSharedPreferences();
        statoTimer.setValue( StatoTimer.PAUSA);

        //Intent update toolbar title
        Intent toolbarIntent = new Intent();
        toolbarIntent.setAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        toolbarIntent.putExtra(TOOLBAR_BUTTONS_STATO_TIMER, R.string.pausa_in_corso);
        localBroadcastManager.sendBroadcast(toolbarIntent);

        countdownTimer = new CountDownTimer(  tempoRimasto, 1000) {
            @Override
            public void onTick(long l) {
                tempoRimasto = l;
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
        statoTimer.setValue( StatoTimer.DISATTIVO);
        if( countdownTimer != null){
            countdownTimer.cancel();
        }
        saveSharedPreferences();

        Intent intentSwitch = new Intent();
        intentSwitch.setAction(END_OF_PAUSA_INTENT);
        intentSwitch.putExtra(END_OF_PAUSA_TIMER, statoTimerPrecedente.getValue());
        localBroadcastManager.sendBroadcast(intentSwitch);

        //Cancello la notifica
        NotificationManagerCompat nmc = NotificationManagerCompat.from( getBaseContext());
        nmc.cancel( ONGOING_PAUSA_NOTIFICATION_ID);

        isRunning = false;
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void loadSharedPreferences(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        statoTimerPrecedente.setValue( sharedPreferences.getInt( STATO_TIMER_PRECEDENTE, StatoTimer.DISATTIVO));
        tempoRimasto = 60000 * sharedPreferences.getInt(PAUSA, 5);
    }

    private void saveSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt( STATO_TIMER, statoTimer.getValue());

        editor.apply();
    }

    private Notification createNotification() {

        Intent intent = new Intent( this, CountUpTimerFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        createNotificationChannel( this, TIMER_CHANNEL_ID, getString(R.string.timer_channel_name), "");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.selector_circle_progress)
                .setContentTitle( getString(R.string.pausa_notification_title))
                .setContentText( getString(R.string.pausa_notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        //Mostra la notifica immediatamente
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify( ONGOING_PAUSA_NOTIFICATION_ID, builder.build());

        return builder.build();
    }

    public long getTempoRimasto(){
        return tempoRimasto;
    }
}
