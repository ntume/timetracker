package com.codeopticals.activitiestracking;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import io.paperdb.Paper;

public class ExportToExcel {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void CreateExcel(Context context, List<ActivityModel> data) {
        String username = Paper.book().read("username", "default_username");
        String identity = Paper.book().read("identity", "default_identity");
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet(""+identity+" "+username);

        HSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Activity Name");
        header.createCell(1).setCellValue("Start Time");
        header.createCell(2).setCellValue("End Time");
        header.createCell(3).setCellValue("Duration (sec)");
        header.createCell(4).setCellValue("Date");
        int size = data.size();
        for (int i = 0; i < size; i++) {
            ActivityModel a = data.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(a.getName());
            row.createCell(1).setCellValue(a.getStart_time().toString());
            row.createCell(2).setCellValue(a.getEnd_time().toString());
            row.createCell(3).setCellValue(a.getDuration().toString());
            row.createCell(4).setCellValue(a.getDateString());
        }

        try {
            File filePath = new File(context.getExternalFilesDir(null), "Activities.xls");
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            String filePathMessage = "File Saved in Local Storage As Activities Excel Format\n" + filePath.getAbsolutePath();
            Toast.makeText(context, filePathMessage, Toast.LENGTH_LONG).show();

            // Automatically open the Excel file using an Intent
            openExcelFile(context, filePath);

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void CreateExcel(Context context, List<MoodModel> data, String controller) {
        String username = Paper.book().read("username", "default_username");
        String identity = Paper.book().read("identity", "default_identity");
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet(""+identity+" "+username);

        HSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("Description");
        header.createCell(1).setCellValue("Date");
        int size = data.size();
        for (int i = 0; i < size; i++) {
            MoodModel a = data.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(a.getDescription());
            row.createCell(1).setCellValue(a.getDateString());
        }

        try {
            File filePath = new File(context.getExternalFilesDir(null), "User's Moods.xls");
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            String filePathMessage = "File Saved\n" + filePath.getAbsolutePath();
            Toast.makeText(context, filePathMessage, Toast.LENGTH_SHORT).show();

            // Automatically open the Excel file using an Intent
            openExcelFile(context, filePath);

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static void openExcelFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
}

