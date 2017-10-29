package com.example.olga.activityrecognitionapp;

import java.util.Date;

/**
 * Created by Olga on 2016-09-15.
 */
public class ProcessedDataClass {

    int id;
    long date;
    long timeDifference;
    float valueDifference;
    String GoggleApi;

    /**
     * @ param id
     * @ param date
     * @ param timeDifference
     * @ param valueDifference
     * @ param GoogleApi
     */

    public ProcessedDataClass(int id, long date, long timeDifference, float valueDifference, String GoogleApi){
        this.id = id;
        this.date = date;
        this.timeDifference = timeDifference;
        this.valueDifference = valueDifference;
        this.GoggleApi = GoogleApi;
    }

    public int getID(){
        return id;
    }

    public long getDate(){
        return date;
    }

    public long getTimeDifference(){
        return timeDifference;
    }

    public float getValueDifference(){
        return valueDifference;
    }

    public String getGoggleApi(){
        return GoggleApi;
    }

    public void setGoogleApi(String s) {
        GoggleApi = s;
    }

}
