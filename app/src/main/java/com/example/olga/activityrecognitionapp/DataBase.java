package com.example.olga.activityrecognitionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.common.api.Api;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Olga on 2016-09-15.
 */

public class DataBase extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "Data.db";
    public static final int DATABASE_VERSION = 25;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE RawData               " +
                    "(                                  " +
                    "STATUS INTEGER NOT NULL,           " +
                    "TIME INTEGER NOT NULL,           " +
                    "value DOUBLE PRECISION NOT NULL    " +
                    ");                                 " );
        db.execSQL( "CREATE TABLE ProcessedData                 " +
                    "(                                          " +
                    "ID INTEGER NOT NULL,                       " +
                    "TIME INTEGER NOT NULL,                   " +
                    "TIME_DIFFERENCE INTEGER NOT NULL, " +
                    "VALUE_DIFFERENCE DOUBLE PRECISION NOT NULL," +
                    "GOOGLE_API TEXT NOT NULL                   " +
                    ");                                         " );
        db.execSQL( "CREATE TABLE LastID            " +
                    "(                              " +
                    "LAST_ID INTEGER NOT NULL       " +
                    ");                             " );
        db.execSQL( "CREATE TABLE ApiTable          " +
                    "(                              " +
                    "TIME INTEGER NOT NULL,         " +
                    "GOOGLE_API TEXT NOT NULL       " +
                    ")                              ");
        db.execSQL("INSERT INTO LastID VALUES (1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RawData");
        db.execSQL("DROP TABLE IF EXISTS ProcessedData");
        db.execSQL("DROP TABLE IF EXISTS LastID");
        db.execSQL("DROP TABLE IF EXISTS ApiTable");
        onCreate(db);
    }

    public boolean insertRawData(long time, float value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATUS", 0);
        contentValues.put("TIME", time);
        contentValues.put("VALUE", value);
        db.insert("RawData", null, contentValues);
        return true;
    }

    public boolean insertProcessedData(List<ProcessedDataClass> processedDataClassList){
        for (int i=0; i<processedDataClassList.size(); i++) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", processedDataClassList.get(i).getID());
            contentValues.put("TIME", processedDataClassList.get(i).getDate());
            contentValues.put("TIME_DIFFERENCE", processedDataClassList.get(i).getTimeDifference());
            contentValues.put("VALUE_DIFFERENCE", processedDataClassList.get(i).getValueDifference());
            contentValues.put("GOOGLE_API", processedDataClassList.get(i).getGoggleApi());
            db.insert("ProcessedData", null, contentValues);
        }
        return true;
    }

    public boolean insertApiTable(long time, String googleApi){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TIME", time);
        contentValues.put("GOOGLE_API",googleApi);
        db.insert("ApiTable", null, contentValues);
        return true;
    }

    public List<RawDataClass> getRawData(){
        List<RawDataClass> rawDataClassList = new ArrayList<RawDataClass>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from RawData where status = 1;",null);
        c.moveToFirst();
        while (c.moveToNext()){
            rawDataClassList.add(new RawDataClass(c.getLong(1),c.getFloat(2)));
        }
        return rawDataClassList;
    }

    public List<ProcessedDataClass> getProcessedData(int id){
        List<ProcessedDataClass> processedDataClassList = new ArrayList<ProcessedDataClass>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c=db.rawQuery("select * from ProcessedData where id=" + id + ";",null);
        c.moveToFirst();
        while (c.moveToNext()){
            processedDataClassList.add(new ProcessedDataClass(c.getInt(0),c.getLong(1),c.getLong(2),c.getFloat(3), c.getString(4)));
        }
        return processedDataClassList;
    }

    public int getLastID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c=db.rawQuery("select * from LastID",null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public void getNextID(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE LastID SET LAST_ID=LAST_ID+1;");
    }

    public List<ApiDataClass> getApiData(long fromTime, long toTime){
        List<ApiDataClass> apiDataClassList = new ArrayList<ApiDataClass>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from ApiTable where " + fromTime + " <= time and time <= " + toTime + " order by time;",null);
        while (c.moveToNext()){
            apiDataClassList.add(new ApiDataClass(c.getLong(0),c.getString(1)));
        }
        return apiDataClassList;
    }

    public void deleteRawData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM RawData where status = 1");
    }

    public void deleteProcessedData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ProcessedData WHERE id=" + id + ";");
    }

    public void deleteApiData(long fromTime, long toTime){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ApiTable WHERE " + fromTime + "<= time and time <=" + toTime + ";");
    }

    public void updateRawData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE RawData SET STATUS=1 WHERE STATUS=0");
    }


    //do testow
    public Cursor select(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM ApiTable",null);
        return c;
    }

    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM ApiTable;");
    }

}
