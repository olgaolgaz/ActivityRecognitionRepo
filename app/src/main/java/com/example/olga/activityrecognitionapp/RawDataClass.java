package com.example.olga.activityrecognitionapp;

import java.util.Date;

/**
 * Created by Olga on 2016-09-15.
 */
public class RawDataClass {

    long date;
    float value;

    /**
     * @ param date
     * @ param value
     */

    public RawDataClass(long date, float value){
        this.date = date;
        this.value = value;
    }

    public long getDate(){
        return date;
    }

    public float getFloatValue(){
        return value;
    }
}
