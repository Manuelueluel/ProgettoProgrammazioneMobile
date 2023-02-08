package com.unitn.lpsmt.group13.pommidori.broadcastReceivers;

import static android.content.Context.NOTIFICATION_SERVICE;

import static com.unitn.lpsmt.group13.pommidori.Utility.REMINDER_ACTIVITY_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.REMINDER_CHANNEL_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.REMINDER_NOTIFICATION_ID;
import static com.unitn.lpsmt.group13.pommidori.Utility.REMINDER_START_HOUR_INTENT;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.activities.Homepage;
import com.unitn.lpsmt.group13.pommidori.fragments.NewSessionFragment;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String activityName = intent.getStringExtra(REMINDER_ACTIVITY_INTENT);
        long millis = intent.getLongExtra(REMINDER_START_HOUR_INTENT,0);

        LocalDateTime time = LocalDateTime.ofInstant( Instant.ofEpochMilli( millis), ZoneId.systemDefault());
        String str = time.getHour() + ":" + (time.getMinute()<10?"0"+time.getMinute():time.getMinute());

        Intent destinationIntent = new Intent( context, Homepage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity( context, 0, destinationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.selector_circle_progress)
                .setContentTitle( context.getString(R.string.reminder_text) + " " + activityName)
                .setContentText( activityName + " " + str)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(REMINDER_NOTIFICATION_ID, builder.build());
    }
}