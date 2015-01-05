package ru.igor_ok.cokandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends ActionBarActivity {
    protected EditText loginField;
    protected EditText passwordField;
    protected Button loginBtn;
    protected TextView loginMessage;
    protected Intent intent;

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginMessage = (TextView) findViewById(R.id.login_message);
        intent = new Intent(this, MainActivity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:3000");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }*/


    public static String POST(String url, String login, String password){
        InputStream inputStream = null;
        String result = "";
        String jsonString = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        JSONObject formData = new JSONObject();
        try {
            formData.accumulate("id", 1);
            formData.accumulate("method", "user.Authorise");
            JSONObject params = new JSONObject();
            params.put("login", login);
            params.put("password", password);
            JSONArray jsArr = new JSONArray();
            jsArr.put(params);
            formData.put("params", jsArr);

            jsonString = formData.toString();
            StringEntity se = new StringEntity(jsonString);
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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }




    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        String getLogin = loginField.getText().toString();
        String getPassword= passwordField.getText().toString();
//        SharedPreferences settings = getSharedPreferences("cokStorage", 0);
//        SharedPreferences.Editor editor = settings.edit();
        @Override
        protected String doInBackground(String... urls) {
            return POST("http://192.168.0.64:3000/jsonrpc", getLogin, getPassword);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            loginMessage.setText(result);
        }
    }


    public void appLogin(View view) {
        String getLogin = loginField.getText().toString();
        String getPassword= passwordField.getText().toString();

        if (isNetworkConnected() && (getLogin.length() > 0) && (getPassword.length() > 0)) {
            Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
            new HttpAsyncTask().execute("http://192.168.0.64:3000/jsonrpc");
        } else {
            loginMessage.setTextColor(getResources().getColor(R.color.red));
            loginMessage.setText("Name required!");
        }
    }
}




/*

package com.hmkcode.android;
         
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import org.apache.http.HttpResponse;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.entity.StringEntity;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONObject;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.app.Activity;
        import com.hmkcode.android.vo.Person;
         
public class MainActivity extends Activity implements OnClickListener {
     
                TextView tvIsConnected;
        EditText etName,etCountry,etTwitter;
        Button btnPost;
     
                Person person;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
         
                // get reference to the views
                tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
                etName = (EditText) findViewById(R.id.etName);
                etCountry = (EditText) findViewById(R.id.etCountry);
                etTwitter = (EditText) findViewById(R.id.etTwitter);
                btnPost = (Button) findViewById(R.id.btnPost);
         
                // check if you are connected or not
                if(isConnected()){
                        tvIsConnected.setBackgroundColor(0xFF00CC00);
                        tvIsConnected.setText("You are conncted");
                    }
                else{
                        tvIsConnected.setText("You are NOT conncted");
                    }
         
                // add click listener to Button "POST"
                btnPost.setOnClickListener(this);
         
            }
     
                public static String POST(String url, Person person){
                InputStream inputStream = null;
                String result = "";
                try {
             
                        // 1. create HttpClient
                        HttpClient httpclient = new DefaultHttpClient();
             
                        // 2. make POST request to the given URL
                        HttpPost httpPost = new HttpPost(url);
             
                        String json = "";
             
                        // 3. build jsonObject
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.accumulate("name", person.getName());
                        jsonObject.accumulate("country", person.getCountry());
                        jsonObject.accumulate("twitter", person.getTwitter());
             
                        // 4. convert JSONObject to JSON to String
                        json = jsonObject.toString();
             
                        // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                        // ObjectMapper mapper = new ObjectMapper();
                        // json = mapper.writeValueAsString(person);
             
                        // 5. set json to StringEntity
                        StringEntity se = new StringEntity(json);
             
                        // 6. set httpPost Entity
                        httpPost.setEntity(se);
             
                        // 7. Set some headers to inform server about the type of the content   
                        httpPost.setHeader("Accept", "application/json");
                        httpPost.setHeader("Content-type", "application/json");
             
                        // 8. Execute POST request to the given URL
                        HttpResponse httpResponse = httpclient.execute(httpPost);
             
                        // 9. receive response as inputStream
                        inputStream = httpResponse.getEntity().getContent();
             
                        // 10. convert inputstream to string
                        if(inputStream != null)
                            result = convertInputStreamToString(inputStream);
                        else
                            result = "Did not work!";
             
                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    }
         
                // 11. return result
                return result;
            }
     
                public boolean isConnected(){
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected())
                        return true;
                    else
                        return false;    
            }
        @Override
        public void onClick(View view) {
         
                switch(view.getId()){
                        case R.id.btnPost:
                                if(!validate())
                                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                                // call AsynTask to perform network operation on separate thread
                                new HttpAsyncTask().execute("http://hmkcode.appspot.com/jsonservlet");
                            break;
                    }
         
            }
        private class HttpAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... urls) {
             
                        person = new Person();
                        person.setName(etName.getText().toString());
                        person.setCountry(etCountry.getText().toString());
                        person.setTwitter(etTwitter.getText().toString());
             
                        return POST(urls[0],person);
                    }
                // onPostExecute displays the results of the AsyncTask.
                        @Override
                protected void onPostExecute(String result) {
                        Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
                   }
            }
     
                private boolean validate(){
                if(etName.getText().toString().trim().equals(""))
                    return false;
                else if(etCountry.getText().toString().trim().equals(""))
                    return false;
                else if(etTwitter.getText().toString().trim().equals(""))
                    return false;
                else
                    return true;    
            }
        private static String convertInputStreamToString(InputStream inputStream) throws IOException{
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line = "";
                String result = "";
                while((line = bufferedReader.readLine()) != null)
                    result += line;
         
                inputStream.close();
                return result;
         
            }   
}*/
