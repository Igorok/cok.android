package ru.igor_ok.cokandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class LoginActivity extends ActionBarActivity {
    protected EditText loginField;
    protected EditText passwordField;
    protected Button loginBtn;
    protected TextView loginMessage;
    protected Intent intent;
    protected Object result = null;
    CokModel cm;
    AuthHelper ah;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cm = new CokModel(this); //Here the context is passing
        ah = new AuthHelper();

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



    public void appLogin (View view) {
        ah.setLogin(loginField.getText().toString().trim());
        ah.setPassword(passwordField.getText().toString().trim());
        if (! ah.isValid()) {
            ArrayList<String> errors = ah.validError();
            for (int i = 0; i < errors.size(); i++) {
                if (errors.get(i) == "login") {
                    loginField.setError("Login is required");
                }
                if (errors.get(i) == "password") {
                    passwordField.setError("Password is required");
                }
            }
            return;
        }

        new AsyncTask<Void, Void, Object> (){
            @Override
            protected Object doInBackground (Void... params) {
                try {
                    String postRes = cm.POST("http://192.168.0.45:3000/jsonrpc", ah.getJson());
                    JSONObject pR = new JSONObject(postRes);
                    return pR;
                } catch (Exception e) {
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object result) {
                if (result != null)
                {
                    if (result instanceof Exception) {
                        Context context = getApplicationContext();
                        CharSequence text = ((Exception) result).getMessage();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting().serializeNulls();
                        Gson gson = builder.create();

                        AuthHelper.Authorised authUser = gson.fromJson(result.toString(), AuthHelper.Authorised.class);
                        AuthHelper.AuthResult authRes = authUser.result.get(0);
                        String login = authRes.login;
                        String email = authRes.email;
                        String token = authRes.token;
                        String _id = authRes._id;

                        SharedPreferences user = getSharedPreferences("user", 0);
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("login", login);
                        editor.putString("email", email);
                        editor.putString("token", token);
                        editor.putString("_id", _id);
                        editor.commit();

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }

                }
            }
        }.execute();


    }
}