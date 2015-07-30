package ru.igor_ok.cokandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class UserDetailActivity extends ActionBarActivity {
    protected CokModel cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cm = new CokModel(this);
        setContentView(R.layout.activity_user_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_detail, menu);
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





//    protected class GetUser extends AsyncTask<Void, Void, Object> {
//        @Override
//        protected Object doInBackground (Void... params) {
//            try {
//                JSONObject uParam = new JSONObject();
//                Map<String, String> usr = cm.getUser();
//
//                uParam.put("_id", usr.get("_id"));
//                uParam.put("token", usr.get("token"));
//                JSONArray uArr = new JSONArray();
//                uArr.put(uParam);
//                JSONObject jsObj = cm.getJsObj("user.getUserDetail", uArr);
//
//                String postRes = cm.POST(jsObj.toString());
//                JSONObject pR = new JSONObject(postRes);
//                JSONArray rA = pR.getJSONArray("result");
//                JSONArray uA = rA.getJSONArray(0);
//
//
//                JSONObject qwe = new JSONObject();
//                qwe.put("users", uA);
//                return qwe;
//            } catch (Exception e) {
//                Log.e("post exception ", "" + e.getMessage());
//                return e;
//            }
//        }
//        @Override
//        protected void onPostExecute(Object result) {
//
//            if (result instanceof Exception) {
//                Context context = getApplicationContext();
//                CharSequence text = ((Exception) result).getMessage();
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();
//            }
//            else {
//                GsonBuilder builder = new GsonBuilder();
//
//
//                builder.setPrettyPrinting().serializeNulls();
//                Gson gson = builder.create();
//
//                UserModel.UserList uRes = gson.fromJson(result.toString(), UserModel.UserList.class);
//                adapter.addAll(uRes.users);
//                ListView lv = (ListView) findViewById(R.id.userListView);
//                lv.setAdapter(adapter);
//            }
//        }
//    }
}
