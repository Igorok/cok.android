package ru.igor_ok.cokandroid;

import android.app.Activity;
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

import java.util.HashMap;
import java.util.Map;


public class FragmentChatRoom extends Fragment {
    private static final String ARG_RID = "rId";
    private OnChatRoomListener mListener;

    private CokModel cm;
    Activity mActivity;

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

    private Emitter.Listener joinRoom = new Emitter.Listener() {
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
    public static FragmentChatRoom newInstance(String _rId) {
        FragmentChatRoom fragment = new FragmentChatRoom();
        Bundle args = new Bundle();
        args.putString(ARG_RID, _rId);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentChatRoom() {
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

    public void setRoomId(String _id) {
        Bundle a = getArguments();
        a.putString(ARG_RID, _id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            rId = getArguments().getString(ARG_RID);
        }
        mActivity = FragmentChatRoom.this.getActivity();
        cm = new CokModel(mActivity);
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
                mListener = (OnChatRoomListener) mActivity;
            }
            if (mSocket == null) {
                mSocket = IO.socket(socketio);
                mSocket.on("joinRoom", joinRoom);
                mSocket.on("message", getMessage);
                mSocket.connect();
            }





            JSONObject jData = new JSONObject();
            jData.put("uId", uId);
            jData.put("token", token);
            jData.put("rId", rId);
            jData.put("limit", 100);
            mSocket.emit("joinRoom", jData);
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
        mSocket.off("joinRoom", joinRoom);
        mSocket.disconnect();

        mListener = null;
        mSocket = null;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
    public interface OnChatRoomListener {
        void setTitle(String title);
    }

}
