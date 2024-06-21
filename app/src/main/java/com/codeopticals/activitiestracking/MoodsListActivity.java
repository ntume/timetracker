package com.codeopticals.activitiestracking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class MoodsListActivity extends AppCompatActivity {

    private MoodsManager manager;
    private RecyclerView rv;
    private Button export,LogMood;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moods_list);

        manager = new MoodsManager(this);
        context = this;
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MoodsAdapter(this, manager.readAllUserMoods()));
        int imageResource = R.drawable.mood;
        LinearLayout linearLayout = findViewById(R.id.linear);
        export = linearLayout.findViewById(R.id.export);
        LogMood = linearLayout.findViewById(R.id.log_mood);

        LogMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //boolean is20thHour = LocalTime.now().getHour() == 20 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 20;
                MoodsManager mm = new MoodsManager(context);
                boolean response = false;

                for (MoodModel m : mm.readAllUserMoods()) {
                    String currentDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                    String theDate = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
                    response = m.getDateString().equals(currentDate);
                    if (response) break;
                }

                if ( !response) {
                    //Window window = new Window(context);
                    //window.open();
                    Intent intent = new Intent(MoodsListActivity.this, Mood.class);
                    startActivity(intent);
                    //System.out.println("It is the 20th hour of the day (8:00 PM).");
                }
                else {
                    String theDate = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());

                    AlertDialog.Builder builder = new AlertDialog.Builder(MoodsListActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.custom_alert_dialog, null);

                    builder.setTitle(""+theDate); // Set the title here

                    ImageView imageView = dialogLayout.findViewById(R.id.imagepic);
                    imageView.setImageResource(imageResource);


                    builder.setView(dialogLayout)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle OK button click
                                    dialog.dismiss();
                                }
                            });


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();



                }
                    //Toast.makeText(context, "You have already submitted your mood for the day", Toast.LENGTH_SHORT).show();



                //Window window = new Window(context);
                //window.open();
            };
        });

        export.setOnClickListener(v ->{
            ExportToExcel.CreateExcel(MoodsListActivity.this, manager.readAllUserMoods(), null);
        });

    }

}