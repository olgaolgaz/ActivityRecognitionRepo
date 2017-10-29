package com.example.olga.activityrecognitionapp;

import android.os.AsyncTask;

/**
 * Created by Olga on 2016-09-15.
 */
public class ReceiveAsyncTask extends AsyncTask<String, Void, String>{

        ServerOperations serverOperations = new ServerOperations();

        @Override
        protected String doInBackground(String... urls) {
            return serverOperations.fromServer(urls[0]);

        }

}
