package ru.igor_ok.cokandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by igor on 04.07.15.
 */
public class CokModel {
    private Context mContext;
    private Map<String, String> user = new HashMap<>();
    private String restUrl = "http://192.168.0.45:3000/jsonrpc";

    public CokModel(Context mContext) {
        this.mContext = mContext;
        SharedPreferences userStorage;
        userStorage = mContext.getSharedPreferences("user", 0);
        user.put("token", userStorage.getString("token", ""));
        user.put("_id", userStorage.getString("_id", ""));
        user.put("login", userStorage.getString("login", ""));
        user.put("email", userStorage.getString("email", ""));
    }



    protected Map<String, String> getUser () {
        return user;
    }

    protected JSONObject getJsObj (String _method, JSONArray _params) throws Exception {
        JSONObject jsObj = new JSONObject();
        Exception ex = null;
        try {
            jsObj.accumulate("id", 1);
            jsObj.accumulate("jsonrpc", "2.0");
            jsObj.accumulate("method", _method);
            jsObj.put("params", _params);
        } catch (JSONException e) {
            Log.e("JSONException ", "" + e.getMessage());
            ex = e;
        }
        if (ex != null) {
            throw ex;
        }
        return jsObj;
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

    protected String POST(String jsonData) throws Exception {
        Exception ex = null;
        if (! this.isNetworkConnected()) {
            ex = new Exception("internet connection not found");
        }

        InputStream inputStream = null;
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(restUrl);
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
