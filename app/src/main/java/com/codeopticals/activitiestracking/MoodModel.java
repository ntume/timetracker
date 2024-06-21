package com.codeopticals.activitiestracking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MoodModel {

    private String id;
    private String description;
    private Date date;
    private int value;

    public MoodModel() {
    }

    public MoodModel(String id, String description, Date date) {
        this.id = id;
        this.description = description;
        this.date = date;
        switch(description){
            case "Very Sad":
                this.value = 1;
                break;
            case "Sad":
                this.value = 2;
                break;
            case "Normal":
                this.value = 3;
                break;
            case "Happy":
                this.value = 4;
                break;
            case "Very Happy":
                this.value = 5;
                break;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        switch(value){
            case 1:
                this.description = "Very Sad";
                break;
            case 2:
                this.description = "Sad";
                break;
            case 3:
                this.description = "Normal";
                break;
            case 4:
                this.description = "Happy";
                break;
            case 5:
                this.description = "Very Happy";
                break;
        }
    }


    public void setDate(Date date) {
        this.date = date;
    }
    public String getDateString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }
}
