package ru.igor_ok.cokandroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 16.08.15.
 */
public class ChatModel {

    public static class MsgItem {
        public String date;
        public String login;
        public String msg;
        public String uId;
    }

    public class UsrItem {
        public String _id;
        public String login;
        public String email;
        public String status;
    }

    public class CRoom {
        public String _id;
        public ArrayList<MsgItem> history = new ArrayList<MsgItem>();
        public Map<String, UsrItem> users = new HashMap<>();
    }

    public static class MsgListAdapter extends ArrayAdapter<MsgItem> {
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




    /*
    {
        _id: "558fead4c919627b174c607c"
        cDate: "2015-06-28T12:38:44.444Z"
        creator: "54f2f627b87d9dae196238a5"
        fcDate: "06/28/2015"
        fuDate: null
        type: "group"
        users: [
            {_id: "54f8a9344b0baf021614f8a9", login: "test", email: "test@test"},
        ]
    }
    */
    public class RoomItem {
        public String _id;
        public String cDate;
        public String creator;
        public String fcDate;
        public String fuDate;
        public String type;
        public ArrayList<UsrItem> users = new ArrayList<UsrItem>();
    }



}

class RoomListAdapter extends ArrayAdapter<ChatModel.RoomItem> {
    private int layoutResourceId;
    private Context mContext;
    public RoomListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
        layoutResourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final ChatModel.RoomItem item = getItem(position);
            View v = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(layoutResourceId, null);
            } else {
                v = convertView;
            }

            int uLength = item.users.size();
            String users = "";
            for (int i = 0; i < uLength; i++) {
                ChatModel.UsrItem u = item.users.get(i);
                users += u.login + "; ";
            }


            TextView roomName = (TextView) v.findViewById(R.id.roomName);
            TextView roomDate = (TextView) v.findViewById(R.id.roomDate);

            roomName.setText(users);
            roomDate.setText(item.fcDate);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View cView) {
                    if (mContext instanceof MainActivity) {
                        ((MainActivity) mContext).getChatRoom(item._id);
                    }
                }
            });
            return v;
        } catch (Exception ex) {
            Log.e("adapter exception ", "" + ex.getMessage());
            return null;
        }
    }
}



