package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID= "param1";
    private static final String ARG_TOKEN = "param2";

    private OnUserListListener mListener;

    // TODO: Rename and change types of parameters
    private String uId;
    private String token;
    private View fView;
    private ListView userListView;

    protected CokModel cm;
    protected UserListAdapter adapter;

    public class UserListAdapter extends ArrayAdapter<UserModel.UserItem> {
        private int layoutResourceId;
        private static final String LOG_TAG = "UserListAdapter";

        public UserListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
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
                        Log.d("Click", " " + cView);
                        if (mListener != null) {
                            mListener.onUserSelect(item._id);
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

    protected class GetUsers extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground (Void... params) {
            try {
                JSONObject uParam = new JSONObject();
                Map<String, String> usr = cm.getUser();

                uParam.put("uId", usr.get("_id"));
                uParam.put("token", usr.get("token"));
                JSONArray uArr = new JSONArray();
                uArr.put(uParam);
                JSONObject jsObj = cm.getJsObj("user.getUserList", uArr);

                String postRes = cm.POST(jsObj.toString());
                JSONObject pR = new JSONObject(postRes);
                JSONArray rA = pR.getJSONArray("result");
                JSONArray uA = rA.getJSONArray(0);


                JSONObject qwe = new JSONObject();
                qwe.put("users", uA);
                return qwe;
            } catch (Exception e) {
                Log.e("post exception ", "" + e.getMessage());
                return e;
            }
        }
        @Override
        protected void onPostExecute(Object result) {

            if (result instanceof Exception) {
                Context context = getActivity();
                CharSequence text = ((Exception) result).getMessage();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {
                GsonBuilder builder = new GsonBuilder();


                builder.setPrettyPrinting().serializeNulls();
                Gson gson = builder.create();

                UserModel.UserList uRes = gson.fromJson(result.toString(), UserModel.UserList.class);
                adapter.addAll(uRes.users);
                userListView.setAdapter(adapter);
            }
        }
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param _uId is a id of current user
     * @param _token is a token for web auth
     * @return A new instance of fragment FragmentUserList.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUserList newInstance(String _uId, String _token) {
        FragmentUserList fragment = new FragmentUserList();
        Bundle args = new Bundle();
        args.putString(ARG_UID, _uId);
        args.putString(ARG_TOKEN, _token);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentUserList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uId = getArguments().getString(ARG_UID);
            token = getArguments().getString(ARG_TOKEN);
        }
        cm = new CokModel(getActivity());
        adapter = new UserListAdapter(getActivity(), R.layout.user_item);
        new GetUsers().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_user_list, container, false);
        userListView = (ListView) fView.findViewById(R.id.userListView);


        return fView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnUserListListener) activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnUserListListener {
        // TODO: Update argument type and name
        public void onUserSelect(String userId);
    }
}
