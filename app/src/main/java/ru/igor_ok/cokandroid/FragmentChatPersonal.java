package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.HashMap;
import java.util.Map;


public class FragmentChatPersonal extends Fragment {
    private static final String ARG_PID = "pId";
    private OnChatPersListener mListener;

    private CokModel cm;
    Activity mActivity;

    private String pId;
    private String uLogin;
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

                            msgAdp = new ChatModel.MsgListAdapter(mActivity, R.layout.chat_msg_item);
                            msgAdp.addAll(cr.history);


                            lv.setAdapter(msgAdp);

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
        if (getArguments() != null) {
            pId = getArguments().getString(ARG_PID);
        }
        mActivity = getActivity();
        cm = new CokModel(mActivity);
        Map<String, String> usr = cm.getUser();
        token = usr.get("token");
        uId = usr.get("_id");
        uLogin = usr.get("login");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fView = inflater.inflate(R.layout.fragment_chat_personal, container, false);
        lv = (ListView) fView.findViewById(R.id.msgListView);
        sendBtn = (ImageButton) fView.findViewById(R.id.send_button);
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

        sendText = (EditText) fView.findViewById(R.id.send_text);


        socketio = mActivity.getString(R.string.socketio);
        {
            try {
                mSocket = IO.socket(socketio);
            } catch (Exception e) {
                Exception ex = e;
                Log.e("socketio ", "" + ex.getMessage());
                cm.errToast(ex);
            }
        }



        mSocket.on("joinPersonal", joinPersonal);
        mSocket.on("message", getMessage);
        mSocket.connect();

        JSONObject jData = new JSONObject();
        try {
            jData.put("uId", uId);
            jData.put("token", token);
            jData.put("personId", pId);
        } catch (JSONException e) {
            Exception ex = e;
            cm.errToast(ex);
        }
        mSocket.emit("joinPersonal", jData);
        return fView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnChatPersListener) activity;
        } catch (ClassCastException e) {
            Exception ex = e;
            Log.e("onAttach ", "" + ex.getMessage().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSocket.disconnect();
        mSocket.off("message", getMessage);
        mSocket.off("joinPersonal", joinPersonal);

        mListener = null;
    }
    public interface OnChatPersListener {
        public void setTitle(String title);
    }

}
