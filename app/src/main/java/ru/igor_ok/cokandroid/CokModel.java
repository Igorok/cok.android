package ru.igor_ok.cokandroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Map;




/**
 * Created by igor on 04.07.15.
 */
public class CokModel {
    Context mContext;
    public CokModel(Context mContext) {
        this.mContext = mContext;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService(this.mContext.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

    protected String POST(String url, String jsonData) throws Exception {
        Exception ex = null;
        if (! this.isNetworkConnected()) {
            ex = new Exception("internet connection not found");
        }

        InputStream inputStream = null;
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            StringEntity se = new StringEntity(jsonData.toString());
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            ex = e;
            Log.e("Http Post ", "" + e.getMessage());
        }
        if (ex != null) {
            throw ex;
        }
        try {
            JSONObject postRes = new JSONObject(result);
            if (postRes.has("error")) {
                Log.e("Http Post ", "" + postRes.getString("error"));
                throw new Exception("web error");
            }
        } catch (JSONException e) {
            Log.e("JSONException ", "" + e.getMessage());
            throw e;
        }
        Log.d("answer", result.trim());
        return result.toString().trim();
    }
}
