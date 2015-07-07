package ru.igor_ok.cokandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ActionBarActivity {
    protected EditText loginField;
    protected EditText passwordField;
    protected Button loginBtn;
    protected TextView loginMessage;
    protected Intent intent;
    CokModel cm;
    CokHelper ch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cm = new CokModel(this); //Here the context is passing
        ch = new CokHelper();

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


    /**
     * Created by igor on 03.07.15.
     */
    private class AuthParams {
        public String login;
        public String password;
    }
    private class Auth {
        public String jsonrpc = "2.0";
        public String method = "user.Authorise";
        public List<AuthParams> params = new ArrayList<AuthParams>();
    }

    private class AuthResult {
        public String _id;
        public String token;
        public String login;
        public String email;
        public String group;
        public Integer status;
    }
    private class Authorised {
        public String jsonrpc;
        public Integer id;
        public List<AuthResult> result = new ArrayList<AuthResult>();
    }



    private class CokHelper {
        private String login = "";
        private String password = "";
        private boolean validated = false;


        public void setLogin (String login) {
            this.login = login;
        }
        public void setPassword (String password) {
            this.password = password;
        }
        public String getLogin () {
            return this.login;
        }
        public String getPassword () {
            return this.password;
        }


        public boolean isValid () {
            if (this.login.length() == 0) {
                loginField.setError("Login is required");
            }
            if (this.password.length() == 0) {
                passwordField.setError("Password is required");
            }
            if (this.login.length() > 0 && this.password.length() > 0) {
                this.validated = true;
            }
            return this.validated;
        }
        public String getJson() {
            Auth auth = new Auth();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting().serializeNulls();
            Gson gson = builder.create();

            AuthParams aParam = new AuthParams();
            aParam.login = this.login;
            aParam.password = this.password;
            auth.params.add(aParam);

            return gson.toJson(auth);
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return cm.POST(urls[0], ch.getJson());
        }
        @Override
        protected void onPostExecute(String result) {
/*
{
"jsonrpc":"2.0",
"id":1,
"result":[
    {
    "_id":"54f2f627b87d9dae196238a5",
    "login":"demo",
    "email":"demo@demo",
    "group":"SimpleUser",
    "created":"2015-03-01T11:21:11.315Z",
    "status":1,
    "token":"ec3f305659fee7603497256f2630bf19fa753c973923fcad7d059db3100df044832f0de347a71421a811681480a8608a",
    "selfFriendRequests":[],
    "friends":[{"_id":"5570ab064c32a55b1585d7b9"},{"_id":"54f8a9344b0baf021614f8a9"}],
    "friendRequests":[]
    }
]
}
 */
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting().serializeNulls();
            Gson gson = builder.create();

            Authorised authUser = gson.fromJson(result, Authorised.class);
            AuthResult authRes = authUser.result.get(0);
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


    public void appLogin (View view) {
        ch.setLogin(loginField.getText().toString().trim());
        ch.setPassword(passwordField.getText().toString().trim());
        if (! ch.isValid()) {
            loginMessage.setTextColor(getResources().getColor(R.color.red));
            loginMessage.setText("Name required!");
            return;
        }
        new HttpAsyncTask().execute("http://192.168.0.45:3000/jsonrpc");






//        it is alert
//        Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
//        new HttpAsyncTask().execute("http://192.168.0.45:3000/jsonrpc");






    }


//    public void appLogin(View view) {
//        String getLogin = loginField.getText().toString();
//        String getPassword= passwordField.getText().toString();
//
//        if (isNetworkConnected() && (getLogin.length() > 0) && (getPassword.length() > 0)) {
//            Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
//            new HttpAsyncTask().execute("http://192.168.0.45:3000/jsonrpc");
//        } else {
//            loginMessage.setTextColor(getResources().getColor(R.color.red));
//            loginMessage.setText("Name required!");
//        }
//    }
}