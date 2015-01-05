package ru.igor_ok.cokandroid;

import android.content.Context;
import android.content.Intent;
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
            return POST(urls[0], getLogin, getPassword);
        }
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