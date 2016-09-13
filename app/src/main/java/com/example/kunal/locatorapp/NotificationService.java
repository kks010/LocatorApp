package com.example.kunal.locatorapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kunal on 13-09-2016.
 */
public class NotificationService extends Service {



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(),"App running in background",Toast.LENGTH_SHORT).show();

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Locator App");
        builder.setContentText("Tap to close the service");


        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

}
