package com.codeopticals.activitiestracking;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager2
{
    private SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;

    public  SessionManager2(Context context){

        sharedPreferences = context.getSharedPreferences("AppKey",0);

        editor = sharedPreferences.edit();
        editor.apply();


    }
    public  void setFlag(Boolean flag)
    {
        editor.putBoolean("KEY_FLAG2",flag);
        editor.commit();
    }

    public boolean getFlag()
    {
        return sharedPreferences.getBoolean("KEY_FLAG2",false);
    }

    public  void  setCurrentTime(String currentTime)
    {
        editor.putString("KEY_TIME2",currentTime);

        editor.commit();
    }

    public  String getCurrentTime()
    {
        return sharedPreferences.getString("KEY_TIME2","");
    }
}
