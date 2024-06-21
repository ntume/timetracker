package com.codeopticals.activitiestracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;

import java.security.Provider;

public class PingService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        String Active;
        Active = intent.getStringExtra("Activity");
        Intent intent1 = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this,"ChannelId1")
                .setContentTitle("Time Tracker Ping")
                .setContentText("Are you still " + Active)
                .setSmallIcon(R.drawable.question)
                .setContentIntent(pendingIntent).build();

        startForeground(1,notification);
        Uri alarmSound =
                RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );
        MediaPlayer mp = MediaPlayer. create (getApplicationContext(), R.raw.notify);
        mp.start();

        return  START_STICKY;


    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "ChannelId1","Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
