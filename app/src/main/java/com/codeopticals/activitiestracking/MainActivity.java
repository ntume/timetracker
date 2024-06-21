package com.codeopticals.activitiestracking;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.codeopticals.activitiestracking.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

  ActivityMainBinding binding;

  List<ActivityModel> runningActivities;
  Chronometer chronometer;
  Chronometer chronometer2;
  SessionManager sessionManager;
  SessionManager2 sessionManager2;
  SimpleDateFormat format;
  String currentTime;
  Timer timer;
  TimerTask timerTask;
  ActivityManager manager;
  String ActivityRunning = "";
  private long Your_X_SECS = 5;
  private final int SECOND = 1000;
  private final int MINUTE = SECOND * 60;
  private final int HOUR = MINUTE * 60;
  AlertDialog.Builder builder;
  // constant code for runtime permission
  private static final int PERMISSION_REQUEST_CODE = 200;
  TextView activityTime;

  private String[] activityName = {
    "Sleeping and Resting",
    "Personal Care",
    "Cooking",
    "Eating and drinking",
    "Domestic work",
    "Traveling and commuting",
    "School (also homework)",
    "Work as employed",
    "Own business work/shop/vending",
    "Farming/Livestock",
    "Fishing",
    "Shopping/getting services (including health services)",
    "Fetching wood",
    "Fetching water",
    "Taking care of children/sick/elderly",
    "Social Activities/Hobbies",
    "Religious Activities",
    "Exercising",
    "Carrying Goods",
  };

  private int[] activityImage = {
    R.drawable.ic_76,
    R.drawable.ic_80,
    R.drawable.ic_70,
    R.drawable.ic_79,
    R.drawable.ic_71,
    R.drawable.ic_60,
    R.drawable.ic_64,
    R.drawable.ic_89,
    R.drawable.ic_40,
    R.drawable.ic_20,
    R.drawable.ic_43,
    R.drawable.ic_48,
    R.drawable.ic_69,
    R.drawable.ic_27,
    R.drawable.ic_66,
    R.drawable.ic_85,
    R.drawable.ic_82,
    R.drawable.ic_84,
    R.drawable.ic_52,
  };

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    activityTime = findViewById(R.id.activityTime);
    if (!checkPermission()) {
      requestPermission();
    }
    builder = new AlertDialog.Builder(this).setTitle("Activity");
    ActivityCompat.requestPermissions(
      this,
      new String[] { WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE },
      PackageManager.PERMISSION_GRANTED
    );
    checkOverlayPermission();





    findViewById(R.id.history)
      .setOnClickListener(v -> {
        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
      });

    findViewById(R.id.moods)
      .setOnClickListener(v -> {
        startActivity(new Intent(MainActivity.this, MoodsListActivity.class));
      });

    chronometer = (Chronometer) findViewById(R.id.chronometer1);
    sessionManager = new SessionManager(this);
    chronometer2 = binding.chronometer2;
    sessionManager2 = new SessionManager2(this);
    startService();
    startTimer();
    // Getting current time
    format = new SimpleDateFormat("hh:mm:ss:aa");
    currentTime = format.format(new Date());

    manager = new ActivityManager(getApplicationContext());
    manager.resumeActivity();

    if (SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
        "Notification",
        "Notification",
        NotificationManager.IMPORTANCE_DEFAULT
      );
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(channel);
    }


    if (!sessionManager.getFlag()) {
      sessionManager.setCurrentTime(currentTime);
      sessionManager.setFlag(true);
    } else
    {
      String sessionManagerCurrentTime = sessionManager.getCurrentTime();
      try {
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg")); // Set South Africa time zone

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        ActivityModel aa = null; // Initialize aa to null

        if (!manager.getRunningActivities().isEmpty()) {
          aa = manager.getRunningActivities().get(0); // Check if the list is not empty before accessing the first element
        }


        String aDate = dateFormat.format(new Date());
        if (aa != null) {
          aDate = dateFormat.format(aa.getDate());
        }

        // Parse date1
        //Date date1 = format.parse("29 September " + Calendar.getInstance().get(Calendar.YEAR) + " " + sessionManagerCurrentTime);
        Date date1 = format.parse(aDate + " " + sessionManagerCurrentTime);

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format the time component of currentDate
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg")); // Set South Africa time zone
        String currentTime = timeFormat.format(currentDate);

        // Combine the current date with the formatted time
        String formattedCurrentTime = String.format("%02d %s %d %s", calendar.get(Calendar.DAY_OF_MONTH), calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), calendar.get(Calendar.YEAR), currentTime);
        Date date2 = format.parse(formattedCurrentTime);

        // Calculate the time difference in milliseconds
        long timeDifferenceMillis = date2.getTime() - date1.getTime();

        // If date2 is ahead of date1 by more than 12 hours, subtract 12 hours
        if (timeDifferenceMillis > TimeUnit.HOURS.toMillis(12)) {
          timeDifferenceMillis -= TimeUnit.HOURS.toMillis(12);
        }

        // Calculate years, months, days, hours, minutes, and seconds
        long years = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis) / 365;
        long months = (TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis) % 365) / 30;
        long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis) % 30;
        long hours = (timeDifferenceMillis / (60 * 60 * 1000)) % 24;
        long minutes = (timeDifferenceMillis / (60 * 1000)) % 60;
        long seconds = (timeDifferenceMillis / 1000) % 60;

        // Set the chronometer's base time to the calculated time difference
        chronometer.setBase(SystemClock.elapsedRealtime() - timeDifferenceMillis);

        chronometer.start();
      } catch (ParseException e) {
        e.printStackTrace();
      }






    }

    if (!sessionManager2.getFlag()) {
      sessionManager2.setCurrentTime(currentTime);
      sessionManager2.setFlag(true);
    } else {
      String sessionManagerCurrentTime = sessionManager2.getCurrentTime();
      try {
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault());
        //format.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg")); // Set South Africa time zone

        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        ActivityModel aa = null; // Initialize aa to null

        if (!manager.getRunningActivities().isEmpty()) {
          aa = manager.getRunningActivities().get(0); // Check if the list is not empty before accessing the first element
        }

        String aDate = dateFormat.format(new Date());
        if (aa != null) {
          aDate = dateFormat.format(aa.getDate());
        }


        // Parse date1
        //Date date1 = format.parse("29 September " + Calendar.getInstance().get(Calendar.YEAR) + " " + sessionManagerCurrentTime);
        Date date1 = format.parse(aDate + " " + sessionManagerCurrentTime);

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Format the time component of currentDate
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        //timeFormat.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg")); // Set South Africa time zone
        String currentTime = timeFormat.format(currentDate);

        // Combine the current date with the formatted time
        String formattedCurrentTime = String.format("%02d %s %d %s", calendar.get(Calendar.DAY_OF_MONTH), calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), calendar.get(Calendar.YEAR), currentTime);
        Date date2 = format.parse(formattedCurrentTime);

        // Calculate the time difference in milliseconds
        long timeDifferenceMillis = date2.getTime() - date1.getTime();

        // If date2 is ahead of date1 by more than 12 hours, subtract 12 hours
        if (timeDifferenceMillis > TimeUnit.HOURS.toMillis(12)) {
          timeDifferenceMillis -= TimeUnit.HOURS.toMillis(12);
        }

        // Calculate years, months, days, hours, minutes, and seconds
        long years = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis) / 365;
        long months = (TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis) % 365) / 30;
        long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis) % 30;
        long hours = (timeDifferenceMillis / (60 * 60 * 1000)) % 24;
        long minutes = (timeDifferenceMillis / (60 * 1000)) % 60;
        long seconds = (timeDifferenceMillis / 1000) % 60;

        // Set the chronometer's base time to the calculated time difference
        chronometer2.setBase(SystemClock.elapsedRealtime() - timeDifferenceMillis);

        chronometer2.start();
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    // The value will be default as empty string because for
    // the very first time when the app is opened, there is nothing to show
    //        String s1 = manager.getActivity() != null ? manager.getActivity().getName() : "NoActivity";

    runningActivities = manager.getRunningActivities();

    // Checks if there's a second activity running
    if (
      manager.getRunningActivities() != null &&
      manager.getRunningActivities().size() > 1
    ) {
      ActivityModel a = runningActivities.get(1);
      if (a.getName().equals("NoActivity")) {
        binding.activity2Wrapper.setVisibility(View.GONE);
        manager.endActivity(a.getId());
      } else {
        binding.activity2Wrapper.setVisibility(View.VISIBLE);
        binding.activityTime2.setText(
          "Activity started at " + format.format(a.getDate())
        );
        for (int i = 0; i < activityName.length; i++) {
          if (a.getName().equals(activityName[i])) {
            binding.activity2Image.setImageResource(activityImage[i]);
            System.out.println(activityImage[i]);
          }
        }
      }
    }

    // Checks if there's at least one activity running
    if (
      manager.getRunningActivities() != null &&
      manager.getRunningActivities().size() > 0
    ) {
      ActivityModel a = runningActivities.get(0);
      if (a.getName().equals("NoActivity")) {
        binding.selectedImage.setImageResource(R.drawable.download);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        if (a != null) activityTime.setText(
          "No Activity since " + format.format(a.getDate())
        ); else activityTime.setText("");
      } else {



        activityTime.setText(
          "Activity started at " + format.format(a.getDate())
        );
        for (int i = 0; i < activityName.length; i++) {
          if (a.getName().equals(activityName[i])) {
            binding.selectedImage.setImageResource(activityImage[i]);
          }
        }
      }
    }
    // Checks if there is no activity running an resets variables
    else {
      binding.selectedImage.setImageResource(R.drawable.download);
      chronometer.setBase(SystemClock.elapsedRealtime());
      chronometer.stop();
      binding.activity2Wrapper.setVisibility(View.GONE);
      if (binding.activity1wrapper.getVisibility() == View.GONE) {
        binding.activity1wrapper.setVisibility(View.VISIBLE);
      }
    }

    MyAdapter myAdapter = new MyAdapter(this, activityName, activityImage);
    binding.gridView.setAdapter(myAdapter);
    binding.gridView.setOnItemClickListener((adapterView, view, i, l) -> {
      if (isRunning(activityName[i])) {
        Toast
          .makeText(
            MainActivity.this,
            "You are already doing this activity",
            Toast.LENGTH_SHORT
          )
          .show();
        return;
      }

      builder = new AlertDialog.Builder(MainActivity.this).setTitle("Activity");
      builder
        .setMessage("Are you " + activityName[i] + " now?")
        .setPositiveButton(
          "Yes",
          (dialogInterface, x) -> {
            // check if there's any running activity
            if (
              manager.getRunningActivities() != null &&
              manager.getRunningActivities().size() != 0
            ) {
              if (manager.getRunningActivities().size() == 2) {
                for (ActivityModel as : manager.getRunningActivities()) {
                  if (as.getName().equals("NoActivity")) {
                    manager.endActivity(as.getId());
                    rearrangeActivities();
                    return;
                  }
                }
                new AlertDialog.Builder(MainActivity.this)
                  .setTitle("Activity")
                  .setMessage(
                    "Please Stop one of the running activities to add another one."
                  )
                  .setNegativeButton("Dismiss", (dialogInterface1, i1) -> {})
                  .create()
                  .show();
                rearrangeActivities();
                return;
              }

              if (manager.getRunningActivities().size() == 1) {
                String a = manager.getRunningActivities().get(0).getName();
                if (a.equals("NoActivity")) {
                  currentTime = format.format(new Date());
                  stopTimerTask();

                  // are doing this activity
                  manager =
                    new ActivityManager(
                      getApplicationContext(),
                      activityName[i]
                    );
                  activityTime.setText(
                    "Activity started at " +
                    format.format(
                      manager.getRunningActivities().get(0).getDate()
                    )
                  );

                  chronometer.setBase(SystemClock.elapsedRealtime());
                  sessionManager.setCurrentTime(currentTime);
                  chronometer.start();
                  binding.activity1wrapper.setVisibility(View.VISIBLE);
                  binding.selectedImage.setImageResource(activityImage[i]);
                  ActivityRunning = activityName[i];

                  // Timeout for ping if the user is still doing the running activity or start a new activity
                  startTimer();

                  NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    MainActivity.this,
                    "Notification"
                  );
                  builder.setContentTitle("Activity Running");
                  builder.setContentText("You are " + activityName[i]);
                  builder.setSmallIcon(activityImage[i]);
                  builder.setAutoCancel(false);
                  builder.clearActions();

                  NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                    MainActivity.this
                  );
                  managerCompat.notify(1, builder.build());
                  rearrangeActivities();
                } else {
                  builder
                    .setMessage(
                      "Do you want to replace " +
                      a +
                      " or do them simultaneously?"
                    )
                    .setPositiveButton(
                      "Replace",
                      (dialogInterface12, i12) -> {
                        currentTime = format.format(new Date());
                        stopTimerTask();

                        // Stop the running activity
                        String aa = manager
                          .getRunningActivities()
                          .get(0)
                          .getId();
                        manager.endActivity(aa);

                        // are doing this activity
                        manager =
                          new ActivityManager(
                            getApplicationContext(),
                            activityName[i]
                          );
                        activityTime.setText(
                          "Activity started at " +
                          format.format(
                            manager.getRunningActivities().get(0).getDate()
                          )
                        );

                        chronometer.setBase(SystemClock.elapsedRealtime());
                        sessionManager.setCurrentTime(currentTime);
                        chronometer.start();
                        binding.activity1wrapper.setVisibility(View.VISIBLE);
                        binding.selectedImage.setImageResource(
                          activityImage[i]
                        );
                        ActivityRunning = activityName[i];

                        // Timeout for ping if the user is still doing the running activity or start a new activity
                        startTimer();

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                          MainActivity.this,
                          "Notification"
                        );
                        builder.setContentTitle("Activity Running");
                        builder.setContentText("You are " + activityName[i]);
                        builder.setSmallIcon(activityImage[i]);
                        builder.setAutoCancel(false);
                        builder.clearActions();
                        System.out.println(
                          "1 replace activities " +
                          manager.getRunningActivities()
                        );
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                          MainActivity.this
                        );
                        managerCompat.notify(1, builder.build());
                        rearrangeActivities();
                      }
                    )
                    .setNegativeButton(
                      "Run simultaneously",
                      (dialogInterface13, i13) -> {
                        currentTime = format.format(new Date());
                        stopTimerTask();
                        // are doing this activity
                        /**!ToDo modify the manager to handle 2 activity */

                        for (ActivityModel activity : manager.getRunningActivities()) {
                          if (activity.getName().equals(activityName[i])) {
                            return;
                          }
                        }

                        manager =
                          new ActivityManager(
                            getApplicationContext(),
                            activityName[i]
                          );
                        binding.activityTime2.setText(
                          "Activity started at " +
                          format.format(
                            manager.getRunningActivities().get(1).getDate()
                          )
                        );

                        binding.chronometer2.setBase(
                          SystemClock.elapsedRealtime()
                        );
                        sessionManager2.setCurrentTime(currentTime);
                        binding.chronometer2.start();
                        binding.activity2Image.setImageResource(
                          activityImage[i]
                        );
                        // ActivityRunning = activityName[i];

                        // Timeout for ping if the user is still doing the running activity or start a new activity
                        startTimer();

                        binding.activity2Wrapper.setVisibility(View.VISIBLE);

                        rearrangeActivities();
                      }
                    )
                    .setNeutralButton("Dismiss", (dialogInterface14, i14) -> {})
                    .create()
                    .show();
                }
              } else {
                currentTime = format.format(new Date());
                stopTimerTask();

                System.out.println(
                  "no Running activities " + manager.getRunningActivities()
                );
                // are doing this activity
                manager =
                  new ActivityManager(getApplicationContext(), activityName[i]);
                activityTime.setText(
                  "Activity started at " +
                  format.format(manager.getRunningActivities().get(0).getDate())
                );

                chronometer.setBase(SystemClock.elapsedRealtime());
                sessionManager.setCurrentTime(currentTime);
                chronometer.start();

                binding.selectedImage.setImageResource(activityImage[i]);
                binding.activity1wrapper.setVisibility(View.VISIBLE);
                ActivityRunning = activityName[i];

                // Timeout for ping if the user is still doing the running activity or start a new activity
                startTimer();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                  MainActivity.this,
                  "Notification"
                );
                builder.setContentTitle("Activity Running");
                builder.setContentText("You are " + activityName[i]);
                builder.setSmallIcon(activityImage[i]);
                builder.setAutoCancel(false);
                builder.clearActions();

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                  MainActivity.this
                );
                managerCompat.notify(1, builder.build());
                rearrangeActivities();
              }
            } else {
              currentTime = format.format(new Date());
              stopTimerTask();

              // are doing this activity
              manager =
                new ActivityManager(getApplicationContext(), activityName[i]);
              activityTime.setText(
                "Activity started at " +
                format.format(manager.getRunningActivities().get(0).getDate())
              );

              chronometer.setBase(SystemClock.elapsedRealtime());
              sessionManager.setCurrentTime(currentTime);
              chronometer.start();

              binding.selectedImage.setImageResource(activityImage[i]);
              binding.activity1wrapper.setVisibility(View.VISIBLE);
              ActivityRunning = activityName[i];

              System.out.println(
                "no Running activities " + manager.getRunningActivities()
              );
              // Timeout for ping if the user is still doing the running activity or start a new activity
              startTimer();

              NotificationCompat.Builder builder = new NotificationCompat.Builder(
                MainActivity.this,
                "Notification"
              );
              builder.setContentTitle("Activity Running");
              builder.setContentText("You are " + activityName[i]);
              builder.setSmallIcon(activityImage[i]);
              builder.setAutoCancel(false);
              builder.clearActions();

              NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                MainActivity.this
              );
              managerCompat.notify(1, builder.build());
              rearrangeActivities();
            }
          }
        )
        .setNeutralButton(
          "Set Start Time",
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int u) {
              Calendar c = Calendar.getInstance();
              int hour = c.get(Calendar.HOUR_OF_DAY);
              int minute = c.get(Calendar.MINUTE);

              TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                  @Override
                  public void onTimeSet(
                    TimePicker view,
                    int hourOfDay,
                    int minuteOfDay
                  ) {
                    // check if there's any running activity
                    if (
                      manager.getRunningActivities() != null &&
                      manager.getRunningActivities().size() != 0
                    ) {
                      // check if there are 2 running activities
                      // if there are 2 running activities, check if one of them is NoActivity
                      // if one of them is NoActivity, end it and start the new activity
                      // if none of them is NoActivity, ask user to stop at least one activity
                      if (manager.getRunningActivities().size() == 2) {
                        for (ActivityModel as : manager.getRunningActivities()) {
                          if (as.getName().equals("NoActivity")) {
                            manager.endActivity(as.getId());
                            rearrangeActivities();
                            return;
                          }
                        }
                        new AlertDialog.Builder(MainActivity.this)
                          .setTitle("Activity")
                          .setMessage(
                            "Please Stop one of the running activities to add another one."
                          )
                          .setNegativeButton(
                            "Dismiss",
                            (dialogInterface1, i1) -> {}
                          )
                          .create()
                          .show();
                        return;
                      }

                      if (manager.getRunningActivities().size() == 1) {
                        String a = manager
                          .getRunningActivities()
                          .get(0)
                          .getName();
                        if (a.equals("NoActivity")) {
                          currentTime = format.format(new Date());
                          stopTimerTask();

                          // are doing this activity
                          manager =
                            new ActivityManager(
                              getApplicationContext(),
                              activityName[i]
                            );
                          activityTime.setText(
                            "Activity started at " +
                            format.format(
                              manager.getRunningActivities().get(0).getDate()
                            )
                          );
                          rearrangeActivities();
                          chronometer.setBase(SystemClock.elapsedRealtime());
                          sessionManager.setCurrentTime(currentTime);
                          chronometer.start();
                          binding.activity1wrapper.setVisibility(View.VISIBLE);
                          binding.selectedImage.setImageResource(
                            activityImage[i]
                          );
                          ActivityRunning = activityName[i];

                          // Timeout for ping if the user is still doing the running activity or start a new activity
                          startTimer();

                          NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            MainActivity.this,
                            "Notification"
                          );
                          builder.setContentTitle("Activity Running");
                          builder.setContentText("You are " + activityName[i]);
                          builder.setSmallIcon(activityImage[i]);
                          builder.setAutoCancel(false);
                          builder.clearActions();

                          NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                            MainActivity.this
                          );
                          managerCompat.notify(1, builder.build());
                          rearrangeActivities();
                        } else {
                          builder
                            .setMessage(
                              "Do you want to replace " +
                              a +
                              " or do them simultaneously?"
                            )
                            .setPositiveButton(
                              "Replace",
                              (dialogInterface12, i12) -> {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minuteOfDay);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                DateFormat format = new SimpleDateFormat(
                                  "HH:mm:ss"
                                );
                                currentTime = format.format(calendar.getTime());
                                stopTimerTask();
                                // are doing this activity
                                try {
                                  manager =
                                    new ActivityManager(
                                      getApplicationContext(),
                                      activityName[i],
                                      LocalTime.parse(currentTime)
                                    );
                                  rearrangeActivities();
                                } catch (Exception e) {
                                  new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Activity")
                                    .setMessage(
                                      "Please Stop one of the running activities to add another one."
                                    )
                                    .setNegativeButton(
                                      "Dismiss",
                                      (dialogInterface1, i1) -> {}
                                    )
                                    .create()
                                    .show();
                                  throw new RuntimeException(e);
                                }

                                activityTime.setText(
                                  "Activity started at " +
                                  manager
                                    .getRunningActivities()
                                    .get(0)
                                    .getStartTime()
                                );

                                chronometer.setBase(
                                  SystemClock.elapsedRealtime()
                                );
                                sessionManager.setCurrentTime(currentTime);
                                chronometer.start();

                                binding.selectedImage.setImageResource(
                                  activityImage[i]
                                );
                                binding.activity1wrapper.setVisibility(
                                  View.VISIBLE
                                );
                                ActivityRunning = activityName[i];

                                // Timeout for ping if the user is still doing the running activity or start a
                                // new activity
                                startTimer();

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                  MainActivity.this,
                                  "Notification"
                                );
                                builder.setContentTitle("Activity Running");
                                builder.setContentText(
                                  "You are " + activityName[i]
                                );
                                builder.setSmallIcon(activityImage[i]);
                                builder.setAutoCancel(false);
                                builder.clearActions();

                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                                  MainActivity.this
                                );
                                managerCompat.notify(1, builder.build());

                                rearrangeActivities();
                              }
                            )
                            .setNegativeButton(
                              "Run simultaneously",
                              (dialogInterface13, i13) -> {
                                for (ActivityModel activity : manager.getRunningActivities()) {
                                  if (
                                    activity.getName().equals(activityName[i])
                                  ) {
                                    return;
                                  }
                                }

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minuteOfDay);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                DateFormat format = new SimpleDateFormat(
                                  "HH:mm:ss"
                                );
                                currentTime = format.format(calendar.getTime());
                                stopTimerTask();
                                // are doing this activity
                                try {
                                  manager =
                                    new ActivityManager(
                                      getApplicationContext(),
                                      activityName[i],
                                      LocalTime.parse(currentTime)
                                    );
                                  rearrangeActivities();
                                } catch (Exception e) {
                                  new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Activity")
                                    .setMessage(
                                      "Please Stop one of the running activities to add another one."
                                    )
                                    .setNegativeButton(
                                      "Dismiss",
                                      (dialogInterface1, i1) -> {}
                                    )
                                    .create()
                                    .show();
                                  throw new RuntimeException(e);
                                }

                                activityTime.setText(
                                  "Activity started at " +
                                  manager
                                    .getRunningActivities()
                                    .get(0)
                                    .getStartTime()
                                );

                                chronometer.setBase(
                                  SystemClock.elapsedRealtime()
                                );
                                sessionManager.setCurrentTime(currentTime);
                                chronometer.start();

                                binding.selectedImage.setImageResource(
                                  activityImage[i]
                                );
                                binding.activity1wrapper.setVisibility(
                                  View.VISIBLE
                                );
                                ActivityRunning = activityName[i];

                                // Timeout for ping if the user is still doing the running activity or start a new activity
                                startTimer();

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                  MainActivity.this,
                                  "Notification"
                                );
                                builder.setContentTitle("Activity Running");
                                builder.setContentText(
                                  "You are " + activityName[i]
                                );
                                builder.setSmallIcon(activityImage[i]);
                                builder.setAutoCancel(false);
                                builder.clearActions();

                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                                  MainActivity.this
                                );
                                managerCompat.notify(1, builder.build());

                                /**  */

                                manager =
                                  new ActivityManager(
                                    getApplicationContext(),
                                    activityName[i]
                                  );
                                binding.activityTime2.setText(
                                  "Activity started at " +
                                  format.format(
                                    manager
                                      .getRunningActivities()
                                      .get(1)
                                      .getDate()
                                  )
                                );

                                binding.chronometer2.setBase(
                                  SystemClock.elapsedRealtime()
                                );
                                sessionManager2.setCurrentTime(currentTime);
                                binding.chronometer2.start();
                                binding.activity2Image.setImageResource(
                                  activityImage[i]
                                );
                                // ActivityRunning = activityName[i];

                                // Timeout for ping if the user is still doing the running activity or start a new activity
                                startTimer();

                                binding.activity2Wrapper.setVisibility(
                                  View.VISIBLE
                                );

                                rearrangeActivities();
                              }
                            )
                            .setNeutralButton(
                              "Dismiss",
                              (dialogInterface14, i14) -> {}
                            )
                            .create()
                            .show();
                        }
                      } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minuteOfDay);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        DateFormat format = new SimpleDateFormat("HH:mm:ss");
                        currentTime = format.format(calendar.getTime());
                        stopTimerTask();
                        // are doing this activity
                        try {
                          manager =
                            new ActivityManager(
                              getApplicationContext(),
                              activityName[i],
                              LocalTime.parse(currentTime)
                            );
                          rearrangeActivities();
                        } catch (Exception e) {
                          new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Activity")
                            .setMessage(
                              "Please Stop one of the running activities to add another one."
                            )
                            .setNegativeButton(
                              "Dismiss",
                              (dialogInterface1, i1) -> {}
                            )
                            .create()
                            .show();
                          throw new RuntimeException(e);
                        }

                        activityTime.setText(
                          "Activity started at " +
                          manager.getRunningActivities().get(0).getStartTime()
                        );

                        chronometer.setBase(SystemClock.elapsedRealtime());
                        sessionManager.setCurrentTime(currentTime);
                        chronometer.start();

                        binding.selectedImage.setImageResource(
                          activityImage[i]
                        );
                        binding.activity1wrapper.setVisibility(View.VISIBLE);
                        ActivityRunning = activityName[i];

                        // Timeout for ping if the user is still doing the running activity or start a new activity
                        startTimer();

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                          MainActivity.this,
                          "Notification"
                        );
                        builder.setContentTitle("Activity Running");
                        builder.setContentText("You are " + activityName[i]);
                        builder.setSmallIcon(activityImage[i]);
                        builder.setAutoCancel(false);
                        builder.clearActions();

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                          MainActivity.this
                        );
                        managerCompat.notify(1, builder.build());
                        rearrangeActivities();
                      }
                    }
                    // There are no activities running start activity
                    else {
                      Calendar calendar = Calendar.getInstance();
                      calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                      calendar.set(Calendar.MINUTE, minuteOfDay);
                      calendar.set(Calendar.SECOND, 0);
                      calendar.set(Calendar.MILLISECOND, 0);

                      DateFormat format = new SimpleDateFormat("HH:mm:ss");
                      currentTime = format.format(calendar.getTime());
                      stopTimerTask();
                      // are doing this activity
                      try {
                        manager =
                          new ActivityManager(
                            getApplicationContext(),
                            activityName[i],
                            LocalTime.parse(currentTime)
                          );
                        rearrangeActivities();
                      } catch (Exception e) {
                        Toast
                          .makeText(
                            MainActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                          )
                          .show();
                        new AlertDialog.Builder(MainActivity.this)
                          .setTitle("Activity")
                          .setMessage(
                            "Please Stop one of the running activities to add another one."
                          )
                          .setNegativeButton(
                            "Dismiss",
                            (dialogInterface1, i1) -> {}
                          )
                          .create()
                          .show();
                        throw new RuntimeException(e);
                      }

                      activityTime.setText(
                        "Activity started at " +
                        manager.getRunningActivities().get(0).getStartTime()
                      );

                      chronometer.setBase(
                        SystemClock.elapsedRealtime() -
                        (
                          Calendar.getInstance().getTimeInMillis() -
                          calendar.getTimeInMillis()
                        )
                      );
                      sessionManager.setCurrentTime(currentTime);
                      chronometer.start();

                      binding.selectedImage.setImageResource(activityImage[i]);
                      binding.activity1wrapper.setVisibility(View.VISIBLE);
                      ActivityRunning = activityName[i];

                      // Timeout for ping if the user is still doing the running activity or start a new activity
                      startTimer();

                      NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        MainActivity.this,
                        "Notification"
                      );
                      builder.setContentTitle("Activity Running");
                      builder.setContentText("You are " + activityName[i]);
                      builder.setSmallIcon(activityImage[i]);
                      builder.setAutoCancel(false);
                      builder.clearActions();

                      NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                        MainActivity.this
                      );
                      managerCompat.notify(1, builder.build());

                      rearrangeActivities();
                    }
                  }
                },
                hour,
                minute,
                true
              );

              timePickerDialog.show();
            }
          }
        )
        .setNegativeButton(
          "No",
          (dialogInterface, o) -> {
            // if (
            //   manager.getRunningActivities() != null &&
            //   manager.getRunningActivities().size() > 0
            // ) {
            //   for (ActivityModel a : manager.getRunningActivities()) {
            //     if (a.getName().equals(activityName[i])) {
            //       /** !ToDo fix this logic to end an activity */
            //       if (manager.runningActivity() != null) {
            //         manager.endActivity(a.getId());
            //         //                                            manager.saveInstance();
            //         stopTimerTask();
            //         stopTimerTask();
            //       }

            //       binding.selectedImage.setImageResource(R.drawable.download);
            //       chronometer.setBase(SystemClock.elapsedRealtime());
            //       sessionManager.setFlag(false);
            //       chronometer.stop();
            //       binding.activity1wrapper.setVisibility(View.VISIBLE);

            //       manager =
            //         new ActivityManager(getApplicationContext(), "NoActivity");

            //       currentTime = format.format(new Date());
            //       /** !ToDo fix this part */
            //       //                                    activityTime.setText("No Activity since " + format.format( manager.getActivity().getDate()));

            //       startTimer();

            //       NotificationCompat.Builder builder = new NotificationCompat.Builder(
            //         MainActivity.this,
            //         "Notification"
            //       );
            //       builder.setContentTitle("Please Note !!! ");
            //       builder.setContentText("No Activity Running Now");
            //       builder.setSmallIcon(R.drawable.exclamation);

            //       // Getting default notification sound
            //       // Uri alarmSound = RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );

            //       MediaPlayer mp = MediaPlayer.create(
            //         getApplicationContext(),
            //         R.raw.notify
            //       );
            //       mp.start();

            //       builder.setAutoCancel(false);
            //       builder.clearActions();

            //       NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
            //         MainActivity.this
            //       );
            //       managerCompat.notify(1, builder.build());

            //       //Dialog
            //       final Dialog dialog = new Dialog(MainActivity.this);
            //       dialog.setContentView(R.layout.custom);
            //       Button dialogButton = (Button) dialog.findViewById(
            //         R.id.dialogButtonOK
            //       );

            //       dialogButton.setOnClickListener(
            //         new View.OnClickListener() {
            //           @Override
            //           public void onClick(View v) {
            //             dialog.dismiss();
            //           }
            //         }
            //       );
            //       dialog.show();
            //       return;
            //     }
            //   }
            // }
          }
        );

      AlertDialog dialog = builder.create();
      // Show the dialog
      dialog.show();

      if (manager != null && manager.getRunningActivities().size() != 0) {
        dialog
          .getButton(DialogInterface.BUTTON_NEUTRAL)
          .setVisibility(View.GONE);
      }
    });

    binding.selectedImage.setOnClickListener(view -> {
      String name = manager.getRunningActivities() != null &&
        manager.getRunningActivities().size() > 0
        ? manager.getRunningActivities().get(0).getName()
        : "No Activity";
      if (name.equals("No Activity")) {
        return;
      }
      builder = new AlertDialog.Builder(MainActivity.this).setTitle("Activity");
      builder
        .setMessage("Are you done " + name + "?")
        .setPositiveButton(
          "Yes",
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              if (
                manager.getRunningActivities() != null &&
                manager.getRunningActivities().size() > 0
              ) {
                // prompt confirmation  to stop activity
                manager.endActivity(
                  manager.getRunningActivities().get(0).getId()
                );
                stopTimerTask();
              }

              binding.selectedImage.setImageResource(R.drawable.download);
              chronometer.setBase(SystemClock.elapsedRealtime());
              sessionManager.setFlag(false);
              chronometer.stop();

              manager =
                new ActivityManager(getApplicationContext(), "NoActivity");
              currentTime = format.format(new Date());

              startTimer();

              rearrangeActivities();
            }
          }
        )
        .setNegativeButton(
          "No",
          (dialogInterface, i) -> {
            System.out.println(
              "1 activities " + manager.getRunningActivities()
            );
          }
        )
        .create()
        .show();
    });

    binding.activity2Wrapper.setOnClickListener(view -> {
      String name = manager.getRunningActivities() != null &&
        manager.getRunningActivities().size() > 0
        ? manager.getRunningActivities().get(1).getName()
        : "No Activity";
      builder = new AlertDialog.Builder(MainActivity.this).setTitle("Activity");
      builder
        .setMessage("Are you done " + name + "?")
        .setPositiveButton(
          "Yes",
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              if (
                manager.getRunningActivities() != null &&
                manager.getRunningActivities().size() > 1
              ) {
                // prompt confirmation  to stop activity
                // if yes
                manager.endActivity(
                  manager.getRunningActivities().get(1).getId()
                );
                stopTimerTask();
              } else {
                if (
                  manager.getRunningActivities() != null &&
                  manager.getRunningActivities().size() > 0
                ) {
                  // prompt confirmation  to stop activity
                  manager.endActivity(
                    manager.getRunningActivities().get(0).getId()
                  );
                  stopTimerTask();
                }

                binding.selectedImage.setImageResource(R.drawable.download);
                binding.activity1wrapper.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                sessionManager.setFlag(false);
                chronometer.stop();

                manager =
                  new ActivityManager(getApplicationContext(), "NoActivity");
                currentTime = format.format(new Date());

                startTimer();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                  MainActivity.this,
                  "Notification"
                );
                builder.setContentTitle("Please Note !!! ");
                builder.setContentText("No Activity Running Now");
                builder.setSmallIcon(R.drawable.exclamation);

                // Getting default notification sound
                // Uri alarmSound = RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );

                MediaPlayer mp = MediaPlayer.create(
                  getApplicationContext(),
                  R.raw.notify
                );
                mp.start();

                builder.setAutoCancel(false);
                builder.clearActions();

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
                  MainActivity.this
                );
                managerCompat.notify(1, builder.build());

                //Dialog
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom);
                Button dialogButton = (Button) dialog.findViewById(
                  R.id.dialogButtonOK
                );

                if (binding.activity2Wrapper.getVisibility() == View.VISIBLE) {
                  binding.activity1wrapper.setVisibility(View.GONE);
                }
                dialogButton.setOnClickListener(
                  new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      dialog.dismiss();
                    }
                  }
                );
                dialog.show();
              }
              startTimer();
              binding.activity2Wrapper.setVisibility(View.GONE);
              binding.activity1wrapper.setVisibility(View.VISIBLE);

              rearrangeActivities();
            }
          }
        )
        .setNegativeButton(
          "No",
          (dialogInterface, i) -> {
            System.out.println(
              "no remove activity 2 " + manager.getRunningActivities()
            );
          }
        )
        .create()
        .show();
    });
  }

  private void rearrangeActivities() {
    ActivityManager manager = new ActivityManager(MainActivity.this);

    manager.resumeActivity();

    switch (manager.getRunningActivities().size()) {
      case 0:
        binding.activity1wrapper.setVisibility(View.VISIBLE);
        binding.activity2Wrapper.setVisibility(View.GONE);
        break;
      case 1:
        // set activity 1
        ActivityModel a = manager.getRunningActivities().get(0);

       Calendar calendar = Calendar.getInstance();
        if (SDK_INT >= Build.VERSION_CODES.O) {
          calendar.setTime(a.getDate());
        }
        chronometer.setBase(
                        SystemClock.elapsedRealtime() -
                        (
                          Calendar.getInstance().getTimeInMillis() -
                          calendar.getTimeInMillis()
                        )
                      );

        chronometer.start();
        binding.activity1wrapper.setVisibility(View.VISIBLE);
        binding.activity2Wrapper.setVisibility(View.GONE);

        // set activity image
        for (int i = 0; i < activityName.length; i++) {
          if (SDK_INT >= Build.VERSION_CODES.O) {
            binding.activityTime.setText(
              "Activity started at " + format.format(a.getDate())
            );
            if (a.getName().equals(activityName[i])) {
              binding.selectedImage.setImageResource(activityImage[i]);
              break;
            }
          }
        }
        break;
      case 2:
        // set activity 1
        ActivityModel a1 = manager.getRunningActivities().get(0);

        Calendar calendar1 = Calendar.getInstance();
        if (SDK_INT >= Build.VERSION_CODES.O) {
          calendar1.setTime(a1.getDate());
        }
        chronometer.setBase(
                        SystemClock.elapsedRealtime() -
                        (
                          Calendar.getInstance().getTimeInMillis() -
                          calendar1.getTimeInMillis()
                        )
                      );
        chronometer.start();
        binding.activity1wrapper.setVisibility(View.VISIBLE);

        // set activity1 image
        for (int i = 0; i < activityName.length; i++) {
          if (SDK_INT >= Build.VERSION_CODES.O) {
            binding.activityTime.setText(
              "Activity started at " + format.format(a1.getDate())
            );
            if (a1.getName().equals(activityName[i])) {
              binding.selectedImage.setImageResource(activityImage[i]);
              break;
            }
          }
        }

        // set activity 2
        ActivityModel a2 = manager.getRunningActivities().get(1);

        if (SDK_INT >= Build.VERSION_CODES.O) {
          binding.activityTime2.setText(
            "Activity started at " +
            format.format(manager.getRunningActivities().get(1).getDate())
          );
        }

        // binding.chronometer2.setBase(SystemClock.elapsedRealtime());
        // if (SDK_INT >= Build.VERSION_CODES.O) {
        //   sessionManager2.setCurrentTime(a2.getStartTime());
        // }
        Calendar calendar2 = Calendar.getInstance();
        if (SDK_INT >= Build.VERSION_CODES.O) {
          calendar2.setTime(a2.getDate());
        }
        binding.chronometer2.setBase(
                        SystemClock.elapsedRealtime() -
                        (
                          Calendar.getInstance().getTimeInMillis() -
                          calendar2.getTimeInMillis()
                        )
                      );
        binding.chronometer2.start();
        binding.activity2Wrapper.setVisibility(View.VISIBLE);

        // set activity2 image
        for (int i = 0; i < activityName.length; i++) {
          if (SDK_INT >= Build.VERSION_CODES.O) {
            if (a2.getName().equals(activityName[i])) {
              binding.activity2Image.setImageResource(activityImage[i]);
              break;
            }
          }
        }
        break;
    }

    if (manager.getRunningActivities().size() == 0) {
      NotificationCompat.Builder builder = new NotificationCompat.Builder(
        MainActivity.this,
        "Notification"
      );
      builder.setContentTitle("Please Note !!! ");
      builder.setContentText("No Activity Running Now");
      builder.setSmallIcon(R.drawable.exclamation);

      // Getting default notification sound
      // Uri alarmSound = RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION );

      MediaPlayer mp = MediaPlayer.create(
        getApplicationContext(),
        R.raw.notify
      );
      mp.start();

      builder.setAutoCancel(false);
      builder.clearActions();

      NotificationManagerCompat managerCompat = NotificationManagerCompat.from(
        MainActivity.this
      );
      managerCompat.notify(1, builder.build());

      //Dialog
      final Dialog dialog = new Dialog(MainActivity.this);
      dialog.setContentView(R.layout.custom);
      Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

      dialogButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            dialog.dismiss();
          }
        }
      );
      dialog.show();
    }
  }

  public void startService() {
    if (SDK_INT >= Build.VERSION_CODES.M) {
      // check if the user has already granted
      // the Draw over other apps permission
      if (Settings.canDrawOverlays(this)) {
        // start the service based on the android version
        if (SDK_INT >= Build.VERSION_CODES.O) {
          startForegroundService(new Intent(this, ForegroundService.class));
        } else {
          startService(new Intent(this, ForegroundService.class));
        }
      }
    } else {
      startService(new Intent(this, ForegroundService.class));
    }
  }

  // method to ask user to grant the Overlay permission
  public void checkOverlayPermission() {
    if (SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(this)) {
        // send user to the device settings
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivity(myIntent);
      }
    }
  }

  //Start Counting minutes for us to ping  the user if the same activity is still running
  final Handler handler = new Handler();

  public void startTimer() {
    timer = new Timer();
    initializeTimerTask();
    if (ActivityRunning.equals("Sleeping and Resting")) {
      Calendar c = Calendar.getInstance();
      int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

      if (timeOfDay >= 10 && timeOfDay < 17) {
       timer.schedule(timerTask, HOUR, 2 * HOUR);
      } else {
       timer.schedule(timerTask,  HOUR, 4 * HOUR);
      }
    } else {
      timer.schedule(timerTask, 30 * MINUTE, 30 * MINUTE);
    }
  }

  public void stopTimerTask() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  public void initializeTimerTask() {
    timerTask =
      new TimerTask() {
        public void run() {
          handler.post(
            new Runnable() {
              @RequiresApi(api = Build.VERSION_CODES.O)
              public void run() {

                PingWindow pingWindow = new PingWindow(MainActivity.this);
                pingWindow.open();
                Intent intent = new Intent(
                  MainActivity.this,
                  PingService.class
                );
                intent.putExtra("Activity", ActivityRunning);

                if (SDK_INT >= Build.VERSION_CODES.O) {
                  startForegroundService(intent);
                } else {
                  startService(intent);
                }
              }
            }
          );
        }
      };
  }

  private boolean checkPermission() {
    // checking of permissions.
    int permission1 = ContextCompat.checkSelfPermission(
      this,
      WRITE_EXTERNAL_STORAGE
    );
    int permission2 = ContextCompat.checkSelfPermission(
      this,
      READ_EXTERNAL_STORAGE
    );
    return (
      permission1 == PackageManager.PERMISSION_GRANTED &&
      permission2 == PackageManager.PERMISSION_GRANTED
    );
  }

  private void requestPermission() {
    // requesting permissions if not provided.
    ActivityCompat.requestPermissions(
      this,
      new String[] { WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE },
      PERMISSION_REQUEST_CODE
    );
  }

  @Override
  public void onRequestPermissionsResult(
    int requestCode,
    @NonNull String[] permissions,
    @NonNull int[] grantResults
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0) {
        // after requesting permissions we are showing
        // users a toast message of permission granted.
        boolean writeStorage =
          grantResults[0] == PackageManager.PERMISSION_GRANTED;
        boolean readStorage =
          grantResults[1] == PackageManager.PERMISSION_GRANTED;

        if (writeStorage && readStorage) {
          Toast
            .makeText(this, "Permission Granted..", Toast.LENGTH_SHORT)
            .show();
        } else {
          Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
          //finish();
        }
      }
    }
  }

  private boolean isRunning(String name) {
    for (ActivityModel activity : manager.getRunningActivities()) {
      if (SDK_INT >= Build.VERSION_CODES.O) {
        if (activity.getName().equals(name)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  protected void onResume() {
    super.onResume();
    manager.resumeActivity();
  }

  //End pinging
  @Override
  protected void onStop() {
    super.onStop();
    // finish();

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  // Helper method to format the time in hh:mm:ss:sss format
  private String formatTime(long milliseconds) {
    int hours = (int) (milliseconds / 3600000);
    int minutes = (int) ((milliseconds % 3600000) / 60000);
    int seconds = (int) ((milliseconds % 60000) / 1000);
    int millis = (int) (milliseconds % 1000);

    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  private boolean verifyActivity(String activityName) {
    for (ActivityModel activity : manager.getRunningActivities()) {
      if (SDK_INT >= Build.VERSION_CODES.O) {
        if (activity.getName().equals(activityName)) {
          return true;
        }
      }
    }
    return false;
  }

  public long calculateDaysBetween(Date startDate, Date endDate) {
    if (startDate == null || endDate == null) {
      // Handle the case where one or both dates are null
      System.out.println("start time "+startDate);
      System.out.println("end time "+endDate);
      return -1; // or throw an exception, depending on your use case
    }

    // Convert Date objects to LocalDate
    LocalDate localStartDate = null;
    long days = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

      localStartDate = Instant.ofEpochMilli(startDate.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDate();

      LocalDate localEndDate = Instant.ofEpochMilli(endDate.getTime())
              .atZone(ZoneId.systemDefault())
              .toLocalDate();

      days = localEndDate.toEpochDay() - localStartDate.toEpochDay();
    }
      System.out.println(days);
      System.out.println("Diff Days: " + days);
      return Math.abs(days); // Use Math.abs to ensure the result is non-negative

  }
}
