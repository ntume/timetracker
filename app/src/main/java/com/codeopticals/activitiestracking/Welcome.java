package com.codeopticals.activitiestracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import io.paperdb.Paper;


public class Welcome extends AppCompatActivity {

    String theName;
    String theIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paper.init(this);

        theName = Paper.book().contains("username") ? Paper.book().read("username") : "";
        theIdentity = Paper.book().contains("identity") ? Paper.book().read("identity") : "";


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(theIdentity.isEmpty() || theName.isEmpty())
                {
                    Intent i = new Intent(Welcome.this, Register.class);
                    startActivity(i);
                }
                else
                {
                    Intent i = new Intent(Welcome.this, MainActivity.class);
                    startActivity(i);
                }

                finish();
            }
        }, 2000);

    }
}