package com.example.olga.activityrecognitionapp;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Olga on 2016-09-15.
 */
public class DataOperations {

    DataBase dataBase = null;

    /**
     * @ param context
     */
    public DataOperations(Context context){
        dataBase = new DataBase(context);
    }

    public void transformData(){
        //zamień status z 0 na 1
        dataBase.updateRawData();
        //zamien raw data na processed data
        RawDataToProcessedData();
        //usun z bazy danych raw data o statusie = 1;
        dataBase.deleteRawData();
    }

    public void send(){
        List<ProcessedDataClass> processedDataClassList = new ArrayList<ProcessedDataClass>();
        //wczytaj z bazy danych przetworzone dane
        processedDataClassList = dataBase.getProcessedData(dataBase.getLastID()-1);

        for(int i=0; i<processedDataClassList.size(); i++){
            String message =    processedDataClassList.get(i).getID()               +";"+
                                processedDataClassList.get(i).getDate()             +";"+
                                processedDataClassList.get(i).getTimeDifference()   +";"+
                                processedDataClassList.get(i).getValueDifference()  +";"+
                                processedDataClassList.get(i).getGoggleApi()        +";"+
                                "smartphone;"+"user4";
            new SendAsyncTask().execute(message);
        }


        //usuń z bazy danych przetworzone dane o wyslanym ID
        dataBase.deleteProcessedData(dataBase.getLastID());
    }

    public void RawDataToProcessedData(){
        List<RawDataClass> rawDataClassList = new ArrayList<RawDataClass>();
        //wczytaj do listy raw data o statusie = 1
        rawDataClassList = dataBase.getRawData();
        if (rawDataClassList.size() != 0) {

            List<ProcessedDataClass> processedDataClassList = new ArrayList<ProcessedDataClass>();

            long date = rawDataClassList.get(0).getDate();
            long  timeDifference = 0;
            float valueDiffernce = 0;

            boolean sameDirection;
            float diff1, diff2;
            ///long l1;
            //float l2;

            for (int i = 1; i<rawDataClassList.size();i++){
                //wyznaczanie kierunku
                if (i==1){
                    sameDirection = valueDiffernce <= rawDataClassList.get(1).getFloatValue();
                } else {
                    diff1 = rawDataClassList.get(i).getFloatValue() - rawDataClassList.get(i-1).getFloatValue();
                    diff2 = rawDataClassList.get(i-1).getFloatValue() - rawDataClassList.get(i-2).getFloatValue();
                    if(diff1>=0) diff1=1; else diff1=-1;
                    if(diff2>=0) diff2=1; else diff2=-1;
                    sameDirection = diff1==diff2;
                }
                //wyliczenie
                if(sameDirection){
                    //l1= rawDataClassList.get(i).getDate()- rawDataClassList.get(i-1).getDate();
                    //l2=  (rawDataClassList.get(i).getFloatValue()- rawDataClassList.get(i-1).getFloatValue());
                    timeDifference=timeDifference+rawDataClassList.get(i).getDate()- rawDataClassList.get(i-1).getDate();
                    valueDiffernce=valueDiffernce+rawDataClassList.get(i).getFloatValue()- rawDataClassList.get(i-1).getFloatValue();

                } else {
                    processedDataClassList.add(new ProcessedDataClass(dataBase.getLastID(),date,timeDifference,valueDiffernce,"null"));
                    date = rawDataClassList.get(i).getDate();
                    timeDifference=rawDataClassList.get(i).getDate()- rawDataClassList.get(i-1).getDate();
                    valueDiffernce=rawDataClassList.get(i).getFloatValue()- rawDataClassList.get(i-1).getFloatValue();
                }
            }
            processedDataClassList.add(new ProcessedDataClass(dataBase.getLastID(),date,timeDifference,valueDiffernce,"null"));

            List<ApiDataClass> apiDataClassList = new ArrayList<ApiDataClass>();
            apiDataClassList = dataBase.getApiData(processedDataClassList.get(0).getDate(),processedDataClassList.get(processedDataClassList.size()-1).getDate());
            //Log.d("aaaaaaa",apiObj.getDate() + "," + processedObj.getDate()+processedObj.getTimeDifference() );

            //if (apiDataClassList.size() > 0){
            ApiDataClass apiObj = null;
            ProcessedDataClass processedObj=null;
            for (int i=0;i<apiDataClassList.size();i++){
                apiObj = apiDataClassList.get(i);
                for (int j=0;j<processedDataClassList.size();j++){
                    processedObj = processedDataClassList.get(j);
                    if (processedObj.getDate()<= apiObj.getDate() && apiObj.getDate()<=processedObj.getDate()+processedObj.getTimeDifference()){
                        processedObj.setGoogleApi(apiObj.getGoogleApi());
                        break;
                    }
                }
            }
            //Log.d("aaaaaaa",apiObj.getDate() + "," + processedObj.getDate()+processedObj.getTimeDifference() );
            //}

            dataBase.insertProcessedData(processedDataClassList);
            dataBase.getNextID();

        }




        /*int a = 1;
        long startTime = rawDataClassList.get(0).getDate();
        float startValue = rawDataClassList.get(0).getFloatValue();
        float startDirection = rawDataClassList.get(1).getFloatValue() - rawDataClassList.get(0).getFloatValue();
        long previousTime;
        float previousValue;
        long currentTime;
        float currentValue;
        long diffTime;
        float diffValue;
        float direction=1;

        if (startDirection>0){
            startDirection = 1;
        } else {
            startDirection = -1;
        }

        while (a < rawDataClassList.size()){

            currentTime = rawDataClassList.get(a).getDate();
            currentValue = rawDataClassList.get(a).getFloatValue();

            previousTime = rawDataClassList.get(a-1).getDate();
            previousValue = rawDataClassList.get(a-1).getFloatValue();



            if(currentValue != previousValue){
                direction = currentValue - previousValue;
                if (direction > 0){
                    direction = 1;
                } else {
                    direction = -1;
                }
            }

            if (rawDataClassList.size()<a+1 || direction!=startDirection){
                diffTime = previousTime - startTime;
                diffValue = previousValue - startValue;
                //
                //List<ApiDataClass> apiDataClassList = new ArrayList<ApiDataClass>();
                //apiDataClassList = dataBase.getApiData(startTime,diffTime);
                //
                processedDataClassList.add(new ProcessedDataClass(dataBase.getLastID(),rawDataClassList.get(a).getDate(),diffTime,diffValue,"null"));
                startTime = previousTime;
                startValue = previousValue;
                startDirection = direction;
            }
            a++;
        }
*/





    }



}
