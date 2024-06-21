package com.codeopticals.activitiestracking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class MyAdapter extends BaseAdapter {
    Context context;
    String[] activityName;
    int[] activityImage;

    LayoutInflater inflater;

    public MyAdapter(Context context, String[] activityName, int[] activityImage) {
        this.context = context;
        this.activityName = activityName;
        this.activityImage = activityImage;
    }

    @Override
    public int getCount() {
        return activityName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(inflater == null)
            inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null)
        {
            view = inflater.inflate(R.layout.grid_lay,null);

        }
        ImageView imageView1 = view.findViewById(R.id.imageview);
        TextView textView = view.findViewById(R.id.textview);

        imageView1.setImageResource(activityImage[i]);
        textView.setText(activityName[i]);

        return view;
    }
}
