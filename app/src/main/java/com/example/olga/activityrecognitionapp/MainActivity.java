package com.example.olga.activityrecognitionapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Olga on 2016-09-14.
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;

    DataOperations dataOperations;

    public GoogleApiClient mApiClient;

    MyReceiver myReceiver;

    Button button;
    EditText editText1;
    EditText editText2;
    public int t=0;

    String login = "";

    TextView textView, textView2, textView3, textView4, textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        /*
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //login = editText1.getText().toString();
                Date d = new Date();
                long date = d.getTime();
                Random r = new Random();
                float value = r.nextFloat();
                dataOperations.dataBase.insertRawData(date,value);
                dataOperations.dataBase.insertRawData(date,value);
                dataOperations.dataBase.insertRawData(date+200,value+1);
                dataOperations.dataBase.insertRawData(date+400,value+2);
                dataOperations.dataBase.insertRawData(date+600,value+3);
                dataOperations.dataBase.insertRawData(date+800,value+4);
                dataOperations.dataBase.insertRawData(date+1000,value+5);
                dataOperations.dataBase.insertRawData(date+1200,value+3);
                dataOperations.dataBase.insertRawData(date+1400,value+2);
                dataOperations.dataBase.insertRawData(date+1600,value+8);
                dataOperations.dataBase.insertRawData(date+1800,value+9);
                dataOperations.dataBase.updateRawData();
                List<RawDataClass> raw = new ArrayList<RawDataClass>();
                raw = dataOperations.dataBase.getRawData();
                Log.d("raw",""+raw.size());
                for (int i=0;i<raw.size();i++) {
                    Log.d("data operations", raw.get(i).getDate()+", "+raw.get(i).getFloatValue());
                }

                dataOperations.dataBase.insertApiTable(date+1600,"STILL");
                List<ApiDataClass> api = new ArrayList<ApiDataClass>();
                api = dataOperations.dataBase.getApiData(date, date + 1800);
                for (int i=0;i<api.size();i++) {
                    Log.d("api","api: "+ api.get(i).getDate()+", "+api.get(i).getGoogleApi());
                }
                dataOperations.transformData();

                List<ProcessedDataClass> processed = new ArrayList<ProcessedDataClass>();
                processed = dataOperations.dataBase.getProcessedData(dataOperations.dataBase.getLastID()-1);
                for (int i=0;i<processed.size();i++) {
                    Log.d("processed",processed.get(i).getDate()+", "+processed.get(i).getValueDifference()+", "+processed.get(i).getTimeDifference()+", "+processed.get(i).getGoggleApi());
                }

                Log.d("czas",new Date().getTime()+"");
                textView.setText(new Date().getTime()+"");
            }
        });
        */
        //
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_UI);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

        dataOperations = new DataOperations(this);

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                //new SendAsyncTask().execute("Hello");
                //Log.d("TIMER","Hello");

                t=t+1;
                if(t>1){
                    dataOperations.transformData();
                    dataOperations.send();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, new Date(), 3000);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        float xyz = (float) Math.sqrt(x*x + y*y + z*z);
        //Log.d("xyz",""+xyz);
        Date d = new Date();
        long date = d.getTime();
        dataOperations.dataBase.insertRawData(date,xyz);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(this, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 1000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        //Register BroadcastReceiver to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ActivityRecognizedService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        //Start our own service
        Intent intent = new Intent(MainActivity.this, com.example.olga.activityrecognitionapp.ActivityRecognizedService.class);
        startService(intent);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int datapassed = arg1.getIntExtra("DATAPASSED", 0);
            String activity = "";
            Date d = new Date();
            long date = d.getTime();
            switch(datapassed){
                case 0:
                    activity = "IN_VEHICLE";
                    break;
                case 1:
                    activity = "ON_BICYCLE";
                    break;
                case 2:
                    activity = "ON_FOOT";
                    break;
                case 3:
                    activity = "STILL";
                    break;
                case 4:
                    activity = "UNKNOWN";
                    break;
                case 5:
                    activity = "TILTING";
                    break;
                case 7:
                    activity = "WALKING";
                    break;
                case 8:
                    activity = "RUNNING";
                    break;
            }
            Toast.makeText(MainActivity.this, "Triggered by Service!\n" + "Data passed: " + activity, Toast.LENGTH_SHORT).show();
            dataOperations.dataBase.insertApiTable(date,activity);
        }
    }
}
