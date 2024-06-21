package com.codeopticals.activitiestracking;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ActivityModel {

    private String id;
    private String name;

    private LocalTime start_time;

    private String startTime;
    private LocalTime end_time;

    Date endDate;
    private long duration;
    private Date date;

    public ActivityModel() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ActivityModel(String name, LocalTime time) {
        this.id =  new RandomKeyGenerator().getKey(); // assign a random key to the
        this.name = name;
        this.start_time = time;
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getStart_time() {
        return start_time;
    }

    public String getStartTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return start_time.format(formatter);
    }

    public void setStart_time(LocalTime time) {
        this.start_time = time;
    }

    public LocalTime getEnd_time() {
        return end_time;
    }
    public String getEndTime(){
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(end_time);
        return currentTime;
    }

    public void setEnd_time(LocalTime time) {
        end_time = time;
        setDuration(start_time, end_time);
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(LocalTime sTime, LocalTime eTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Duration diffDuration = Duration.between(sTime, eTime);


            //Duration duration1 = Duration.between((Temporal) date, (Temporal) endDate);
            //long days = duration1.toDays();
            long daysbetween = calculateDaysBetween(date,endDate);


            // Check if the end time is before the start time
            //////////if (diffDuration.isNegative()) {
                // Add  hours (number of  days) to the duration repeatedly until it's positive

                    diffDuration = diffDuration.plusDays(daysbetween);

            ///}

            this.duration = diffDuration.getSeconds();
        }
    }

    public long calculateDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            // Handle the case where one or both dates are null
            System.out.println("start time "+startDate);
            System.out.println("end time "+endDate);
            return -1; // or throw an exception, depending on your use case
        }

        // Convert Date objects to LocalDate
        LocalDate localStartDate = Instant.ofEpochMilli(startDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localEndDate = Instant.ofEpochMilli(endDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        long days = localEndDate.toEpochDay() - localStartDate.toEpochDay();

        System.out.println(days);
        System.out.println("Diff Days: " + days);
        return Math.abs(days); // Use Math.abs to ensure the result is non-negative
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
        System.out.println("The end date is "+endDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDateString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }
    public String getEndDateString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(endDate);
    }
}
