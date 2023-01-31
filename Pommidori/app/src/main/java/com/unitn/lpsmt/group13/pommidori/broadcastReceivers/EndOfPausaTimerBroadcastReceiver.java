package com.unitn.lpsmt.group13.pommidori.broadcastReceivers;

import static com.unitn.lpsmt.group13.pommidori.Utility.END_OF_PAUSA_TIMER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unitn.lpsmt.group13.pommidori.StatoTimer;

public class EndOfPausaTimerBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "EndOfPausaTimerBroadcastReceiver";

    private SwitchFragment switchFragmentListener;
    private int stato;

    public EndOfPausaTimerBroadcastReceiver( Context context) {
        this.switchFragmentListener = (SwitchFragment) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        stato = intent.getIntExtra(END_OF_PAUSA_TIMER, StatoTimer.DISATTIVO);
        switchFragmentListener.switchFragmentFromPausa( stato);
    }

    public interface SwitchFragment{
        public void switchFragmentFromPausa(int stato);
    }

}
