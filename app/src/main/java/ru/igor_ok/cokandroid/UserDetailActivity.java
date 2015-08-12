package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class UserDetailActivity extends ActionBarActivity {
    protected CokModel cm;
    private String personId = null;
    private String uId;
    private String token;

    private TextView user_name;
    private TextView user_email;

    private String getPersId() {
        if (personId != null) {
            return personId;
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Context context = getApplicationContext();
            CharSequence text = "User not found";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }
        personId = extras.getString("userId");
        return personId;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);


        cm = new CokModel(this);
        Map<String, String> usr = cm.getUser();
        token = usr.get("token");
        uId = usr.get("_id");
        user_name = (TextView) findViewById(R.id.user_name);
        user_email = (TextView) findViewById(R.id.user_email);

        new GetUserDetail().execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.chat_personal:
                Intent intent = new Intent(getApplicationContext(), PersonalChatActivity.class);
                intent.putExtra("userId", getPersId());
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private class GetUserDetail extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground (Void... params) {
            try {
                JSONObject uParam = new JSONObject();
                uParam.put("_id", getPersId());
                uParam.put("uId", uId);
                uParam.put("token", token);
                JSONArray uArr = new JSONArray();
                uArr.put(uParam);
                JSONObject jsObj = cm.getJsObj("user.getUserDetail", uArr);

                String postRes = cm.POST(jsObj.toString());
                JSONObject pR = new JSONObject(postRes);
                JSONArray rA = pR.getJSONArray("result");
                JSONObject uDet = rA.getJSONObject(0);
                return uDet;
            } catch (Exception e) {
                Log.e("post exception ", "" + e.getMessage());
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

                    UserModel.UserItem uDetail = gson.fromJson(result.toString(), UserModel.UserItem.class);
                    String login = uDetail.login;
                    String email = uDetail.email;

                    user_name.setText(login);
                    user_email.setText(email);


                }

            }
        }
    }











}
