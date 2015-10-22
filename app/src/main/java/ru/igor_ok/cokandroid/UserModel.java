package ru.igor_ok.cokandroid;

import android.content.Context;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igor on 26.07.15.
 */
public class UserModel {

    public class UserItem {
        public String _id;
        public String login;
        public String email;
        public Integer friend;
    }

    public class UserList {
        public List<UserItem> users = new ArrayList<UserItem>();
    }
}


class UserListAdapter extends ArrayAdapter<UserModel.UserItem> {
    private int layoutResourceId;
    private Context mContext;
    public UserListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mContext = context;
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
                    if(mContext instanceof MainActivity){
                        ((MainActivity)mContext).getUserDetail(item._id);
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
