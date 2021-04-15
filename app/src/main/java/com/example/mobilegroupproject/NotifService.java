package com.example.mobilegroupproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class NotifService extends Service {
    static Chronometer timer;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotifyBuilder;
    Timer notifTimer;
    String selection;
    long base;
    private static final String CHANNEL_ID = "timer";
    public NotifService() {
    }

    @Override
    public int onStartCommand(Intent intent1, int flags, int startID)
    {
        timer = MainActivity.timer;
        base = timer.getBase();
        selection = intent1.getStringExtra("selection");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mNotifyBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("method", "stop");
                PendingIntent stopTimer = PendingIntent.getActivity(getApplicationContext(), 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
                int numMessages = 0;
                long realTime = (SystemClock.elapsedRealtime() - base) / 1000;
                int hours = (int) realTime / 3600;
                int otherMin = (int) realTime - hours * 3600;
                int mins = otherMin / 60;
                otherMin = otherMin - mins * 60;
                int secs = otherMin;
                String together = "Current Time Elapsed in " + selection + ": " + hours + ":" + mins + ":" + secs;
                mNotifyBuilder.setContentText(together) // <-- your timer value
                        .setNumber(++numMessages)
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setContentIntent(pendingIntent)
                        .addAction(R.drawable.ic_action_name, "Stop", stopTimer);
                mNotificationManager.notify(
                        1,
                        mNotifyBuilder.build());
            }
        };
        notifTimer = new Timer();
        notifTimer.scheduleAtFixedRate(task, 0, 1000);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mNotificationManager.cancelAll();
        notifTimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}