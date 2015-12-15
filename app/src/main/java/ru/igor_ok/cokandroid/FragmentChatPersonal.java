package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
import java.util.List;
import java.util.Map;


public class FragmentChatPersonal extends Fragment {
    private static final String ARG_PID = "pId";
    private OnChatPersListener mListener;

    private CokModel cm;
    private ChatSqlHelper sh;
    Activity mActivity;

    private String pId;
    private String uId;
    private String token;

    private View fView;
    private ListView lv;
    private String socketio;

    private ImageButton sendBtn;
    private EditText sendText;

    private String rId = null;

    private Socket mSocket;
    private ChatModel.MsgListAdapter msgAdp;

    private Emitter.Listener joinPersonal = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                mActivity.runOnUiThread(new Runnable() {
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
                            sh.insertMsg(cr.history, pId);

                            msgAdp.addAll(cr.history);
                            msgAdp.notifyDataSetChanged();
                            mListener.setTitle(uLogins);

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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject argObj = (JSONObject) args[0];
                            GsonBuilder builder = new GsonBuilder();
                            builder.setPrettyPrinting().serializeNulls();
                            Gson gson = builder.create();

                            ChatModel.MsgItem msgItem = gson.fromJson(argObj.toString(), ChatModel.MsgItem.class);

                            List<ChatModel.MsgItem> lMsg = new ArrayList<ChatModel.MsgItem>();
                            lMsg.add(msgItem);
                            sh.insertMsg(lMsg, pId);

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

    // TODO: Rename and change types and number of parameters
    public static FragmentChatPersonal newInstance(String pId) {
        FragmentChatPersonal fragment = new FragmentChatPersonal();
        Bundle args = new Bundle();
        args.putString(ARG_PID, pId);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentChatPersonal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_chat_personal, container, false);
        lv = (ListView) fView.findViewById(R.id.msgListView);
        sendBtn = (ImageButton) fView.findViewById(R.id.send_button);
        sendText = (EditText) fView.findViewById(R.id.send_text);
        return fView;
    }

    public void setUserId(String _id) {
        Bundle a = getArguments();
        a.putString(ARG_PID, _id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            pId = getArguments().getString(ARG_PID);
        }
        mActivity = FragmentChatPersonal.this.getActivity();
        cm = new CokModel(mActivity);
        sh = new ChatSqlHelper(mActivity);
        SQLiteDatabase db = sh.getWritableDatabase();
        sh.onCreate(db);

        Map<String, String> usr = cm.getUser();
        token = usr.get("token");
        uId = usr.get("_id");

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


        socketio = mActivity.getString(R.string.socketio);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            if (mListener == null) {
                mListener = (OnChatPersListener) mActivity;
            }
            if (mSocket == null) {
                mSocket = IO.socket(socketio);
                mSocket.on("joinPersonal", joinPersonal);
                mSocket.on("message", getMessage);
                mSocket.connect();
            }


            List<ChatModel.MsgItem> mList = sh.getMsg("pers", pId);
            msgAdp = new ChatModel.MsgListAdapter(mActivity, R.layout.chat_msg_item);
            lv.setAdapter(msgAdp);


            if (mList.size() != 0) {
                if (mList.size() > 100) {
                    Integer dCount = sh.removeOld("pers", pId, mList.get(100).dt);
                }
                msgAdp.addAll(mList);
                msgAdp.notifyDataSetChanged();
            }

            JSONObject jData = new JSONObject();
            jData.put("uId", uId);
            jData.put("token", token);
            jData.put("personId", pId);
            if (mList.size() != 0) {
                jData.put("fDate", mList.get(0).dt);
            } else {
                jData.put("limit", 100);
            }
            mSocket.emit("joinPersonal", jData);
        } catch (Exception e) {
            Exception ex = e;
            Log.e("socketio ", "" + ex.getMessage());
            cm.errToast(ex);
        }
    }



    @Override
    public void onPause() {
        super.onPause();

        mSocket.off("message", getMessage);
        mSocket.off("joinPersonal", joinPersonal);
        mSocket.disconnect();

        mListener = null;
        mSocket = null;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
    public interface OnChatPersListener {
        public void setTitle(String title);
    }

}
