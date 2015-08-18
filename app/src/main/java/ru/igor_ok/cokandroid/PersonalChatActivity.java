package ru.igor_ok.cokandroid;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ru.igor_ok.cokandroid.ChatModel.*;


public class PersonalChatActivity extends ActionBarActivity {
    protected CokModel cm;
    protected String personId = null;
    private String uId;
    private String uLogin;
    private String token;
    private String socketio;

    private ImageButton sendBtn;
    private EditText sendText;


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

    private String rId = null;

    private Socket mSocket;
    private MsgListAdapter msgAdp;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);
        sendBtn = (ImageButton) findViewById(R.id.send_button);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendMessage(v);
                } catch (Exception e) {
                    Exception ex = e;
                    cm.errToast(ex);
                }
            }
        });

        sendText = (EditText) findViewById(R.id.send_text);


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
        uLogin = usr.get("login");

        mSocket.on("joinPersonal", joinPersonal);
        mSocket.on("message", getMessage);
        mSocket.connect();

        JSONObject jData = new JSONObject();
        try {
            jData.put("uId", uId);
            jData.put("token", token);
            jData.put("personId", getPersId());
        } catch (JSONException e) {
            Exception ex = e;
            cm.errToast(ex);
            return;
        }
        mSocket.emit("joinPersonal", jData);
    }




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

                            ChatModel.CRoom cr = gson.fromJson(argObj.toString(), ChatModel.CRoom.class);
                            rId = cr._id;
                            String uLogins = "";
                            for (HashMap.Entry<String, ChatModel.UsrItem> entry : cr.users.entrySet()) {
                                ChatModel.UsrItem myStr = entry.getValue();
                                uLogins += myStr.login + " ";
                            }

                            android.support.v7.app.ActionBar actionBar = PersonalChatActivity.this.getSupportActionBar();
                            actionBar.setTitle(uLogins);


                            msgAdp = new MsgListAdapter(PersonalChatActivity.this, R.layout.chat_msg_item);
                            msgAdp.addAll(cr.history);

                            ListView lv = (ListView) findViewById(R.id.msgListView);
                            lv.setAdapter(msgAdp);

                        } catch (Exception e) {
                            Exception ex = e;
                            cm.errToast(ex);
                        }
                    }
                });

            } catch (Exception e) {
                Exception ex = e;
                cm.errToast(ex);
            }

        }
    };

    public void sendMessage (View view) throws JSONException {
        String msgStr = sendText.getText().toString().trim();
        JSONObject msgNew = new JSONObject();
        msgNew.put("uId", uId);
        msgNew.put("token", token);
        msgNew.put("message", msgStr);
        msgNew.put("rId", rId);

        mSocket.emit("message", msgNew);
        sendText.setText("");
    }

    private Emitter.Listener getMessage = new Emitter.Listener() {
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

                            ChatModel.MsgItem msgItem = gson.fromJson(argObj.toString(), ChatModel.MsgItem.class);
                            msgAdp.add(msgItem);
                            msgAdp.notifyDataSetChanged();

                        } catch (Exception e) {
                            Exception ex = e;
                            cm.errToast(ex);
                        }
                    }
                });
            } catch (Exception e) {
                Exception ex = e;
                cm.errToast(ex);
            }

        }
    };

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
