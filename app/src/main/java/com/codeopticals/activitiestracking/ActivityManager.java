package com.codeopticals.activitiestracking;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ActivityManager extends Paper {

    private ActivityModel activity;
    private Context context;
    private final String BOOK = "Activities";
    private final String TEMP = "temp";

    private final String TEMP_LIST = "tempActivities";

    private List<ActivityModel> runningActivities;

    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userNameRef;

    public ActivityManager(Context context) {
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ActivityManager(Context context, String name) {
        init(context);
        if(name.equals("NoActivity")) return;
        this.context = context;
        activity = new ActivityModel(name, getCurrentTime());
        if(getRunningActivities().size() < 2 ) book(TEMP_LIST).write(activity.getId(), activity);
//        CallMood();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ActivityManager(Context context, String name, LocalTime startTime) throws Exception {
        init(context);
        this.context = context;
        if(name.equals("NoActivity")) return;
        activity = new ActivityModel(name, startTime);
        if(getRunningActivities().size() < 2 ) book(TEMP_LIST).write(activity.getId(), activity);
        else throw new Exception("Too many activities running");
//        CallMood();
    }

    public void resumeActivity(){
        System.out.println("Running activities " + getRunningActivities());
        runningActivities = runningActivity();
    }

    public List<ActivityModel> getRunningActivities() {
        return runningActivity() == null ? new ArrayList<>() : runningActivity();
    }

    public ActivityModel getActivity(String id) {
        for (ActivityModel a: runningActivities)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && a.getId().equals(id)) return a;

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void endActivity(String id){
        for (ActivityModel activity: runningActivity()) {
            if(activity.getId().equals(id)){
                activity.setEndDate(new Date());
                activity.setEnd_time(getCurrentTime());

                saveInstance(activity);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalTime getCurrentTime(){
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        LocalTime t = LocalTime.parse(currentTime);
        return t;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveInstance(ActivityModel activity){

        /**!ToDo check the delete and save instance block */
        // Delete the temporary
        book(TEMP_LIST).delete(activity.getId()); // contains {runningActivityId, 'firstActivity'}

        // Save the current instance of the activity to the database.
        book(BOOK).write(activity.getId(), activity);

        System.out.println("Running activities " + getRunningActivities());
        //Firebase checking if user is connected to internet
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                //if (connected) {
            //Toast.makeText(context,"Connected, Online",Toast.LENGTH_SHORT);
                
                    // Retrieve all the data from local db
                    List<ActivityModel> activities = readAllActivities();

                    String theName = Paper.book().read("username");
                    String theIdentity = Paper.book().read("identity");

                    assert theIdentity != null;
                    userNameRef = rootRef.child(theIdentity);

                    userNameRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           // if(!snapshot.exists()){
                                HashMap<String, Object> o = new HashMap<>();

                                o.put("username", theName);
                                o.put("user_id", theIdentity);

                                userNameRef.updateChildren(o);
                           // }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //Inserting Data Into Database
                    for (ActivityModel a: activities ) {
                        userNameRef.child("Activities").child(a.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists())
                                {
                                    HashMap<String, Object> activity = new HashMap<>();
                                    activity.put("id", a.getId());
                                    activity.put("name", a.getName());
                                    activity.put("start_time", a.getStart_time().toString());
                                    activity.put("end_time", a.getEnd_time().toString());
                                    activity.put("duration (Sec) ", a.getDuration().toString());
                                    activity.put("date", a.getDateString());
                                    activity.put("end_date", a.getEndDateString());



                                    snapshot.getRef().updateChildren(activity);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });
                    }
                    //End inserting
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Sync ", Toast.LENGTH_SHORT).show();
            }
        });
        //End of firebase internet check
    }

    public Context getContext() {
        return context;
    }

    public List<ActivityModel> runningActivity(){
        runningActivities = new ArrayList<>();
        List<String> keys = book(TEMP_LIST).getAllKeys();

        /**!ToDO remove this log */
        Log.i("runningActivities", String.valueOf(keys));

        if(keys.size() > 0) {
            for (String key: keys) {
                runningActivities.add(book(TEMP_LIST).read(key));
            }

            return runningActivities;
        }
        return null;
    }

    public List<ActivityModel> readAllActivities()
    {
        List<ActivityModel> list = new ArrayList<>();
        for (String id: book(BOOK).getAllKeys())
        {
            list.add(readActivity(id));
        }
        return list;
    }

    public String timeToString(LocalTime t){
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(t);
        return currentTime;
    }

    private void CallMood(){
        /**!ToDO check this logic */
        if(book(TEMP).contains("fistActivity")){
            ActivityModel a = book(TEMP).read("fistActivity");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert a != null;
                String aDate = dateFormat.format(a.getDate());
                String todayDate = dateFormat.format(a.getDate());
                if(!aDate.equals(todayDate)){
//                    book(TEMP).write("fistActivity", getActivity());
                    book(TEMP).delete("secMood");
                    Window window = new Window(context);
                    window.open();
                }
            }
        }
        else{
            book(TEMP).delete("secMood");
//            book(TEMP).write("fistActivity", getActivity());

            Window window = new Window(context);
            window.open();
        }
    }

    public ActivityModel readActivity(String id){
        return book(BOOK).read(id);
    }
}
