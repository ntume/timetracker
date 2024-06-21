package com.codeopticals.activitiestracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.codeopticals.activitiestracking.databinding.FragmentDashboardBinding;

public class HistoryActivity extends AppCompatActivity {

    private ActivityManager manager;
    private RecyclerView rv;
    private Button export;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        manager = new ActivityManager(this);
        export = findViewById(R.id.export);

        rv = findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));


        rv.setAdapter(new DashboardAdapter( this, filter24Hours( manager.readAllActivities())));

        export.setOnClickListener(v ->{
            ExportToExcel.CreateExcel(this,  manager.readAllActivities());
        });

    }


//    private List<ActivityModel> filter24Hours(List<ActivityModel> data) {
//        Date today = new Date();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return data.stream()
//                           .filter(activity -> activity.getDate() == today)
//                           .collect(Collectors.toList());
//            }
//
//        }
//
//        return data;
//    }
//

     private List<ActivityModel> filter24Hours(List<ActivityModel> data){
         List<ActivityModel> list = new ArrayList<>();
         Date today = new Date();
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

         for (ActivityModel activity : data) {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                 if(activity.getDateString().equals(dateFormat.format(today))){
                     list.add(activity);
                 }
             }
         }
         return list;
     }
}