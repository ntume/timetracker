package com.codeopticals.activitiestracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoodsAdapter extends RecyclerView.Adapter<MoodsAdapter.ViewHolder> {

    private Context context;
    private List<MoodModel> userMoods;

    public MoodsAdapter(Context context, List<MoodModel> userMoods) {
        this.context = context;
        this.userMoods = userMoods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {

        MoodModel m = userMoods.get(pos);

        holder.description.setText(m.getDescription());
        holder.date.setText(m.getDateString());

    }

    @Override
    public int getItemCount() {
        return userMoods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView description;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.activity);
            date = itemView.findViewById(R.id.duration);
            itemView.findViewById(R.id.ssTime).setVisibility(View.GONE);

            itemView.findViewById(R.id.eeTime).setVisibility(View.GONE);

        }
    }

}
