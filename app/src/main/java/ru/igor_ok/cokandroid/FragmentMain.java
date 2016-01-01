package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;


public class FragmentMain extends Fragment {
    private CokModel cm;
    private Map<String, String> usr;
    private String login;
    private String email;
    private Button shUsers;
    private Button shFriends;
    private Button shChats;


    private OnFragmentInteractionListener mListener;

    public static FragmentMain newInstance() {
        FragmentMain fragment = new FragmentMain();
        return fragment;
    }

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        cm = new CokModel(getActivity());
        usr = cm.getUser();
        login = usr.get("login");
        email = usr.get("email");
        if (mListener != null) {
            mListener.setTitle("Profile " + login);
        }

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        TextView user_name = (TextView) v.findViewById(R.id.user_name);
        TextView user_email = (TextView) v.findViewById(R.id.user_email);


        shUsers = (Button) v.findViewById(R.id.show_users);
        shUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cView) {
                if (mListener != null) {
                    mListener.getUserList();
                }
            }
        });

        shFriends = (Button) v.findViewById(R.id.show_friends);
        shFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cView) {
                if (mListener != null) {
                    mListener.getFriendList();
                }
            }
        });

        shChats = (Button) v.findViewById(R.id.show_chats);
        shChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cView) {
                if (mListener != null) {
                    mListener.getChatList();
                }
            }
        });


        user_name.setText(login);
        user_email.setText(email);
        return v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            Exception ex = e;
            Log.e("onAttach ", "" + ex.getMessage().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        public void getUserList();
        public void getFriendList();
        public void getChatList();
        public void setTitle(String login);
    }

}
