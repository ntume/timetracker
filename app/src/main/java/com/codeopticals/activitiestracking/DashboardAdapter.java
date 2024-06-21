package com.codeopticals.activitiestracking;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private Context context;
    private List<ActivityModel> activities;

    public DashboardAdapter(Context context, List<ActivityModel> activities) {
        this.context = context;
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_layout, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        ActivityModel a = activities.get(pos);
        holder.activity.setText(activities.get(pos).getName());
        String duration = a.getDuration().toString();
        holder.duration.setText(duration);
        holder.sTime.setText(a.getStart_time().toString());
        holder.eTime.setText(a.getEnd_time().toString());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView activity;
        TextView duration;
        TextView sTime;
        TextView eTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activity = itemView.findViewById(R.id.activity);
            duration = itemView.findViewById(R.id.duration);
            sTime = itemView.findViewById(R.id.sTime);
            eTime = itemView.findViewById(R.id.eTime);
        }
    }

}
