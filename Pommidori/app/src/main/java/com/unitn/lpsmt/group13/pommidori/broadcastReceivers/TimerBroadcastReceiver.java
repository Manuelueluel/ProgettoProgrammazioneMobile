package com.unitn.lpsmt.group13.pommidori.broadcastReceivers;

import static com.unitn.lpsmt.group13.pommidori.Utility.TIME_MILLIS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "TimerBroadcastReceiver";

    private UpdateTimer updateTimerListener;
    private long timeMillis;

    public TimerBroadcastReceiver( Context context) {
        this.updateTimerListener = (UpdateTimer) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        timeMillis = intent.getLongExtra( TIME_MILLIS, 0);
        updateTimerListener.updateTimer( timeMillis);
    }

    public interface UpdateTimer{
        public void updateTimer(long timeMillis);
    }
}
