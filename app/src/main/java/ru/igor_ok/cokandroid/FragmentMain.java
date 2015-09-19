package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class FragmentMain extends Fragment {
    private CokModel cm;
    private Map<String, String> usr;
    private String login;
    private String email;

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
        cm = new CokModel(getActivity());
        usr = cm.getUser();
        login = usr.get("login");
        email = usr.get("email");
        if (mListener != null) {
            mListener.setTitle("Profile " + login);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        TextView user_name = (TextView) v.findViewById(R.id.user_name);
        TextView user_email = (TextView) v.findViewById(R.id.user_email);

        user_name.setText(login);
        user_email.setText(email);


        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getActivity(), login, duration);
        toast.show();
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
        public void onFragmentMainInteraction(Uri uri);
        public void setTitle(String login);
    }

}
