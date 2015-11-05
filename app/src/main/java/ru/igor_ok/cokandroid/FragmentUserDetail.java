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



public class FragmentUserDetail extends Fragment {
    private static final String ARG_USR_ID = "userId";
    private UserOpenHelper uSql;

    private String userId;

    private Button btn_chat_personal;
    private TextView user_name;
    private TextView user_email;
    private View fView;

    private OnUserDetailListener mListener;

    public static FragmentUserDetail newInstance(String _userId) {
        FragmentUserDetail fragment = new FragmentUserDetail();
        Bundle args = new Bundle();
        args.putString(ARG_USR_ID, _userId);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentUserDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        user_name = (TextView) fView.findViewById(R.id.user_name);
        user_email = (TextView) fView.findViewById(R.id.user_email);
        btn_chat_personal = (Button) fView.findViewById(R.id.btn_chat_personal);
        btn_chat_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View cView) {
                if (mListener != null) {
                    mListener.getChatPersonal(userId);
                }
            }
        });

        return fView;
    }

    public void setUserId(String _id) {
        Bundle a = getArguments();
        a.putString(ARG_USR_ID, _id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        userId = getArguments().getString(ARG_USR_ID);
        uSql = new UserOpenHelper(getActivity());

        UserModel.UserItem ui = uSql.uGetOne(userId);
        user_name.setText(ui.login);
        user_email.setText(ui.email);

        if (mListener != null) {
            mListener.setTitle(ui.login);
        }


    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUserDetailListener) activity;
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

    public interface OnUserDetailListener {
        // TODO: Update argument type and name
        public void getChatPersonal(String personId);
        public void setTitle(String login);
    }

}
