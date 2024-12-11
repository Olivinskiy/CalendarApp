package com.olivinskij.authandreg.UI;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.olivinskij.authandreg.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String note = intent.getStringExtra("note");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NotesChannel")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Напоминание")
                .setContentText(note)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
