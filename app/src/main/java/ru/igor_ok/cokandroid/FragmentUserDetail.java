package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


public class FragmentUserDetail extends Fragment {
    private static final String ARG_USR_ID = "userId";

    private CokModel cm;

    private String userId;
    private String uId;
    private String token;
    private String login;
    private String email;

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

    private class GetUserDetail extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground (Void... params) {
            try {
                JSONObject uParam = new JSONObject();
                uParam.put("_id", userId);
                uParam.put("uId", uId);
                uParam.put("token", token);
                JSONArray uArr = new JSONArray();
                uArr.put(uParam);
                JSONObject jsObj = cm.getJsObj("user.getUserDetail", uArr);

                String postRes = cm.POST(jsObj.toString());
                JSONObject pR = new JSONObject(postRes);
                JSONArray rA = pR.getJSONArray("result");
                JSONObject uDet = rA.getJSONObject(0);
                return uDet;
            } catch (Exception e) {
                Log.e("post exception ", "" + e.getMessage());
                return e;
            }
        }
        @Override
        protected void onPostExecute(Object result) {
            if (result != null)
            {
                if (result instanceof Exception) {
                    cm.errToast((Exception) result);
                }
                else {
                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting().serializeNulls();
                    Gson gson = builder.create();

                    UserModel.UserItem uDetail = gson.fromJson(result.toString(), UserModel.UserItem.class);
                    login = uDetail.login;
                    email = uDetail.email;

                    user_name.setText(login);
                    user_email.setText(email);

                    if (mListener != null) {
                        mListener.setTitle(login);
                    }

                }

            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USR_ID);
        }
        cm = new CokModel(getActivity());
        Map<String, String> usr = cm.getUser();
        token = usr.get("token");
        uId = usr.get("_id");
        new GetUserDetail().execute();
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
