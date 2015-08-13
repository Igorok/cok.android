package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class UserListActivity extends ActionBarActivity {
    protected CokModel cm;
    protected MemoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        cm = new CokModel(this);

        // the adapter is a member field in the activity
        adapter = new MemoListAdapter(this, R.layout.user_item);

        new GetUsers().execute();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
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

    public class MemoListAdapter extends ArrayAdapter<UserModel.UserItem> {
        private int layoutResourceId;
        private static final String LOG_TAG = "MemoListAdapter";

        public MemoListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            layoutResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                final UserModel.UserItem item = getItem(position);
                View v = null;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(layoutResourceId, null);
                } else {
                    v = convertView;
                }

                TextView header = (TextView) v.findViewById(R.id.uLogin);
                TextView description = (TextView) v.findViewById(R.id.uEmail);

                header.setText(item.login);
                description.setText(item.email);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View cView) {
                    Intent intent = new Intent(getApplicationContext(), UserDetailActivity.class);
                    intent.putExtra("userId", item._id);
                    startActivity(intent);
                    }
                });




                return v;
            } catch (Exception ex) {
                Log.e("adapter exception ", "" + ex.getMessage());
                return null;
            }
        }
    }




    protected class GetUsers extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground (Void... params) {
            try {
                JSONObject uParam = new JSONObject();
                Map<String, String> usr = cm.getUser();

                uParam.put("uId", usr.get("_id"));
                uParam.put("token", usr.get("token"));
                JSONArray uArr = new JSONArray();
                uArr.put(uParam);
                JSONObject jsObj = cm.getJsObj("user.getUserList", uArr);

                String postRes = cm.POST(jsObj.toString());
                JSONObject pR = new JSONObject(postRes);
                JSONArray rA = pR.getJSONArray("result");
                JSONArray uA = rA.getJSONArray(0);


                JSONObject qwe = new JSONObject();
                qwe.put("users", uA);
                return qwe;
            } catch (Exception e) {
                Log.e("post exception ", "" + e.getMessage());
                return e;
            }
        }
        @Override
        protected void onPostExecute(Object result) {

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

                UserModel.UserList uRes = gson.fromJson(result.toString(), UserModel.UserList.class);
                adapter.addAll(uRes.users);
                ListView lv = (ListView) findViewById(R.id.userListView);
                lv.setAdapter(adapter);
            }
        }
    }





}
