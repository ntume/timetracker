package com.codeopticals.activitiestracking;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class MoodsManager extends Paper {

    private Context context;
    private MoodModel mood;
    private final String MOOD_DB = "MOODS";


    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference userNameRef;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MoodsManager(Context context, String description) {
        init(context);
        this.context = context;
        String id = new RandomKeyGenerator().getKey();
        this.mood = new MoodModel(id,description,new Date());
        saveInstance();
    }

    public MoodsManager(Context context) {
        this.context = context;
        init(context);
    }

    public MoodModel getMood() {
        return mood;
    }

    public void setMood(MoodModel mood) {
        this.mood = mood;
    }

    private void saveInstance(){
        book(MOOD_DB).write(mood.getId(), mood);

        //Firebase checking if user is connected to internet
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                //if (connected) {
                    // Retrieve all the data from local db

                    List<MoodModel> moods = readAllUserMoods();

                    String theName = Paper.book().read("username");
                    String theIdentity = Paper.book().read("identity");

                    userNameRef = rootRef.child(theIdentity);

                    userNameRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                HashMap<String, Object> o = new HashMap<>();

                                o.put("username", theName);
                                o.put("user_id", theIdentity);

                                userNameRef.updateChildren(o);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //Inserting Data Into Database
                    for (MoodModel a: moods ) {

                        userNameRef.child("Moods").child(a.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()){
                                    HashMap<String, Object> mood = new HashMap<>();
                                    mood.put("id", a.getId());
                                    mood.put("description", a.getDescription());
                                    mood.put("date", a.getDateString());
                                    mood.put("value", a.getValue());

                                    snapshot.getRef().updateChildren(mood);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    //End inserting
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //End of firebase internet check
    }


    public List<MoodModel> readAllUserMoods(){
        List<MoodModel> list = new ArrayList<>();

        for (String id: book(MOOD_DB).getAllKeys()) {
            list.add(readMoods(id));
        }

        return list;
    }


    public MoodModel readMoods(String id){
        return book(MOOD_DB).read(id);
    }

}
