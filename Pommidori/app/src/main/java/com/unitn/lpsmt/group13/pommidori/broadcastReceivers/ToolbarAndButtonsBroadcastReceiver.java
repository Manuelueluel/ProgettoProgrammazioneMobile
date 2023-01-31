package com.unitn.lpsmt.group13.pommidori.broadcastReceivers;

import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_STATO_TIMER;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unitn.lpsmt.group13.pommidori.R;

public class ToolbarAndButtonsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ToolbarAndButtonsBroadcastReceiver";

    private UpdateToolbarAndButtons updateToolbarListener;
    private int stato;

    public ToolbarAndButtonsBroadcastReceiver(Context context){
        this.updateToolbarListener = (UpdateToolbarAndButtons) context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        stato = intent.getIntExtra(TOOLBAR_BUTTONS_STATO_TIMER, R.string.pomodoro_disattivo);
        updateToolbarListener.updateToolbarAndButtons( stato);
    }

    public interface UpdateToolbarAndButtons {
        public void updateToolbarAndButtons(int stato);
    }
}
