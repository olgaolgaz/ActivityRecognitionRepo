package com.example.olga.activityrecognitionapp;

/**
 * Created by Olga on 2016-09-15.
 */
public class ApiDataClass {

    long date;
    String GoogleApi;

    /**
     * @ param date
     * @ param GoogleApi
     */

    public ApiDataClass(long date, String GoogleApi){
        this.date = date;
        this.GoogleApi = GoogleApi;
    }

    public long getDate(){
        return date;
    }

    public String getGoogleApi(){
        return GoogleApi;
    }



}
