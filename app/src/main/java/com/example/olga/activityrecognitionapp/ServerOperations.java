package com.example.olga.activityrecognitionapp;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olga on 2016-09-15.
 */
public class ServerOperations {

    public String toServer(String s){
        try{
            HttpClient httpClient = new DefaultHttpClient();
            //x- IP, y - port number
            HttpPost httpPost = new HttpPost("http://xxx.xx.xx.xx:yyyy/WebApp/JSONServlet");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("param1", s));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    public String fromServer(String s){
        BufferedReader in = null;
        s = "tekst";
        String line="";
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            //x-IP, y- port number
            URI website = new URI("http://xxx.xx.xx.xx:yyyy/WebApp/JSONServlet");
            request.setURI(website);
            HttpResponse response = httpclient.execute(request);
            in = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            // NEW CODE
            line = in.readLine();
            Log.d("DBG", line);
        }catch(Exception e){
            Log.e("log_tag", "Error in http connection "+e.toString());
        }
        return line;
    }


}
