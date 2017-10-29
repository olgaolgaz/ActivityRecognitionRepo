package com.example.olga.activityrecognitionapp;

import android.os.AsyncTask;

/**
 * Created by Olga on 2016-09-15.
 */
public class SendAsyncTask extends AsyncTask<String, Void, String> {

    ServerOperations serverOperations = new ServerOperations();

    @Override
    protected String doInBackground(String... urls) {
        return serverOperations.toServer(urls[0]);

    }

}
