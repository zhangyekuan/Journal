package com.kk.android.dayonejournal;

import java.util.UUID;

public class Journal {
    private UUID mId;
    private String mTitle;
    private String mContent;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mPath;
    private String mLocation;
    public void setLocation(String location){
        mLocation = location;
    }
    public String getLocation() {
        return mLocation;
    }
    public void setPath(String path){
        mPath = path;
    }
    public String getPath(){
        return mPath;
    }
    public Journal(){
        this(UUID.randomUUID());
    }
    public Journal(UUID id){
        mId = id;
    }
    public String getContent(){
        return mContent;
    }
    public void setContent(String text) {
        mContent = text;
    }
    public UUID getId(){
        return mId;
    }
    public String getTitle(){
        return mTitle;
    }
    public void setTitle(String title){
       mTitle = title;
    }
    public void setYear(int year){
        mYear = year;
    }
    public void setMonth(int month){
        mMonth = month;
    }
    public void setDay(int day){
        mDay = day;
    }
    public int getYear(){
        return mYear;
    }
    public int getMonth(){
        return mMonth;
    }
    public int getDay(){
        return mDay;
    }
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
