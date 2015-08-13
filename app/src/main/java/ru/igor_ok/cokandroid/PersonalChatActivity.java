package ru.igor_ok.cokandroid;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PersonalChatActivity extends ActionBarActivity {
    protected CokModel cm;
    protected String personId = null;
    private String uId;
    private String token;
    private String socketio;

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
    }


    private class MsgItem {
        public String date;
        public String login;
        public String msg;
        public String uId;
    }

    private class UsrItem {
        public String _id;
        public String login;
        public String status;
    }

    private class CRoom {
        public String _id;
        public ArrayList<MsgItem> history = new ArrayList<MsgItem>();
        public Map<String, UsrItem> users = new HashMap<>();
    }



    private Socket mSocket;



    private Emitter.Listener joinPersonal = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
        try {
            PersonalChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                try {
                    JSONObject argObj = (JSONObject) args[0];
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting().serializeNulls();
                    Gson gson = builder.create();

                    CRoom cr = gson.fromJson(argObj.toString(), CRoom.class);
                    String uLogins = "";
                    for (HashMap.Entry<String, UsrItem> entry : cr.users.entrySet()) {
                        UsrItem myStr = entry.getValue();
                        uLogins += myStr.login + " ";
                    }

                    android.support.v7.app.ActionBar actionBar = PersonalChatActivity.this.getSupportActionBar();
                    actionBar.setTitle(uLogins);

                    ArrayList<MsgItem> arrMsg = cr.history;


                    MsgListAdapter msgAdp = new MsgListAdapter(PersonalChatActivity.this, R.layout.chat_msg_item);
                    msgAdp.addAll(cr.history);

                    ListView lv = (ListView) findViewById(R.id.msgListView);
                    lv.setAdapter(msgAdp);

                } catch (Exception e) {
                    Exception ex = e;
                    Log.e("post exception ", "" + ex.getMessage());
                    Context context = getApplicationContext();
                    CharSequence text = ex.getMessage();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                }
            });

        } catch (Exception e) {
            Exception ex = e;
            Log.e("Error ", "" + ex.getMessage());
        }

        }
    };

    public class MsgListAdapter extends ArrayAdapter<MsgItem> {
        private int layoutResourceId;
        private static final String LOG_TAG = "MsgListAdapter";

        public MsgListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            layoutResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                final MsgItem item = getItem(position);
                View v = null;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(layoutResourceId, null);
                } else {
                    v = convertView;
                }

                TextView msgLogin = (TextView) v.findViewById(R.id.msgLogin);
                TextView msgDate = (TextView) v.findViewById(R.id.msgDate);
                TextView msgText = (TextView) v.findViewById(R.id.msgText);

                msgLogin.setText(item.login);
                msgDate.setText(item.date);
                msgText.setText(item.msg);

                return v;
            } catch (Exception ex) {
                Log.e("adapter exception ", "" + ex.getMessage());
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);

        socketio = this.getString(R.string.socketio);
        {
            try {
                mSocket = IO.socket(socketio);
            } catch (Exception e) {
                Exception ex = e;
                Log.e("post exception ", "" + ex.getMessage());
                Context context = getApplicationContext();
                CharSequence text = ex.getMessage();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

        cm = new CokModel(this);
        Map<String, String> usr = cm.getUser();
        token = usr.get("token");
        uId = usr.get("_id");

//        mSocket.on("message", getMessage);
        mSocket.on("joinPersonal", joinPersonal);
        mSocket.connect();

        JSONObject jData = new JSONObject();
        try {
            jData.put("uId", uId);
            jData.put("token", token);
            jData.put("personId", getPersId());
        } catch (JSONException e) {
            Exception ex = e;
            Log.e("post exception ", "" + ex.getMessage());
            return;
        }
        mSocket.emit("joinPersonal", jData);
    }






    public void sendMessage (View view) {
//        mSocket.emit("new message", message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_chat, menu);
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
}
