package com.codeopticals.activitiestracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

public class Mood extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
    }

    public void onVerySadClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new MoodsManager(this, "Very Sad");
        }
        Paper.book("tempMood").write("Response", true);
        // Add actions specific to "Very Sad" mood here
        Toast.makeText(this, "Very Sad Mood Selected", Toast.LENGTH_SHORT).show();
        showSuccessAlertDialog();

    }

    private void showSuccessAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage("You have successfully logged the mood for the day.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Handle the OK button click if needed
                Intent intent = new Intent(Mood.this, MainActivity.class);
                startActivity(intent);
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSadClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new MoodsManager(this, "Sad");
        }
        Paper.book("tempMood").write("Response", true);
        // Add actions specific to "Sad" mood here
        Toast.makeText(this, "Sad Mood Selected", Toast.LENGTH_SHORT).show();
        showSuccessAlertDialog();
    }

    public void onNormalClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new MoodsManager(this, "Normal");
        }
        Paper.book("tempMood").write("Response", true);
        // Add actions specific to "Normal" mood here
        Toast.makeText(this, "Normal Mood Selected", Toast.LENGTH_SHORT).show();
        showSuccessAlertDialog();
    }

    public void onHappyClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new MoodsManager(this, "Happy");
        }
        Paper.book("tempMood").write("Response", true);
        // Add actions specific to "Happy" mood here
        Toast.makeText(this, "Happy Mood Selected", Toast.LENGTH_SHORT).show();
        showSuccessAlertDialog();
    }

    public void onVeryHappyClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new MoodsManager(this, "Very Happy");
        }
        Paper.book("tempMood").write("Response", true);
        // Add actions specific to "Very Happy" mood here
        Toast.makeText(this, "Very Happy Mood Selected", Toast.LENGTH_SHORT).show();
        showSuccessAlertDialog();
    }
}
