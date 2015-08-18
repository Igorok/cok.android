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
}
