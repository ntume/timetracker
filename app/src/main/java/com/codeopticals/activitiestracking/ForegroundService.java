package com.codeopticals.activitiestracking;

import static io.paperdb.Paper.book;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import io.paperdb.Paper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {

    Timer timer;
    TimerTask timerTask;
    Context context;
    private long SECS = 9;

    private final String TEMP = "temp";
    private final int SECOND = 1000;
    private final int MINUTE = SECOND * 60;
    private final int HOUR = MINUTE * 60;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // create the custom or default notification
        // based on the android version
        if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) startMyOwnForeground();
        else startForeground(1, new Notification());

        // create an instance of Window class
        // and display the content on screen
        context = this;
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // for android version >=O we need to create
    // custom notification stating
    // foreground service is running
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent1,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_MIN
        );

        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_ID
        );
        Notification notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle("Service running")
                .setContentText("Displaying over other apps")
                // this is important, otherwise the notification will show the way
                // you want i.e. it will show some default notification
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(2, notification);
    }

    final Handler handler = new Handler();

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, 10 * MINUTE);
    }

    public void initializeTimerTask() {
        timerTask =
                new TimerTask() {
                    public void run() {
                        handler.post(
                                new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    public void run() {
                                        boolean is20thHour = LocalTime.now().getHour() == 20 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20;
                                        MoodsManager mm = new MoodsManager(context);
                                        boolean response = false;

                                        for (MoodModel m : mm.readAllUserMoods()) {
                                            String currentDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                                            response = m.getDateString().equals(currentDate);
                                            if (response) break;
                                        }

                                        if (is20thHour && !response) {
                                            Window window = new Window(context);
                                            window.open();
                                            System.out.println("It is the 20th hour of the day (8:00 PM).");
                                        }
                                    }
                                }
                        );
                    }
                };
    }

}
