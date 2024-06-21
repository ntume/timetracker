package com.codeopticals.activitiestracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;

import androidx.annotation.RequiresApi;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class PingWindow {

    // declaring required variables
    private Context context;
    private View mView;
    TextView Title;
    private final int SECOND = 1000;
    private final int MINUTE = SECOND * 60;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private LayoutInflater layoutInflater;
    private ActivityManager manager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PingWindow(Context context){
        this.context=context;

        Paper.init(context);
        manager = new ActivityManager(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);

        }

        String[] activityName = {
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

        int[] activityImage = {
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


        String theName = Paper.book().read("username");

        String activityRunning = "No Activity";
        if(manager.runningActivity() != null && manager.runningActivity().get(0) != null) activityRunning = manager.runningActivity().get(0).getName();
        
        

        // getting a LayoutInflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created
        mView = layoutInflater.inflate(R.layout.ping_popup_window, null);

        Title = (TextView)mView.findViewById(R.id.titleText);

        for(int i=0; i< activityName.length;i++)
        {
            if(activityRunning.equals(activityName[i]))
            {
                mView.findViewById(R.id.sad).setBackgroundResource(activityImage[i]);
                Title.setText("Hello "+theName+" , are you still "+activityRunning);


                i=activityName.length+1;
            }
            else
            {
                Title.setText("Hello "+theName+", are you still doing no activity?");
                mView.findViewById(R.id.sad).setBackgroundResource(R.drawable.download);
            }

        }

        // set onClickListener on the remove button, which removes
        // the view from the window
        mView.findViewById(R.id.window_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        mView.findViewById(R.id.sad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                close();

            }
        });

        mParams.gravity = Gravity.CENTER;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

    }

    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if(mView.getWindowToken()==null) {
                if(mView.getParent()==null) {
                    mWindowManager.addView(mView, mParams);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            close();
                        }
                    },MINUTE*15);  //MINUTE*15
                }
            }
        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }

    }

    public void close() {

        try {
            // remove the view from the window
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup)mView.getParent()).removeAllViews();

            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {
            Log.d("Error2",e.toString());
        }
    }
}