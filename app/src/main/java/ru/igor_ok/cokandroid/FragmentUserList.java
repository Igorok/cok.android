package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import static ru.igor_ok.cokandroid.SqlHelper.*;


public class FragmentUserList extends Fragment
    implements LoaderManager.LoaderCallbacks<Object>
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID= "param1";
    private static final String ARG_TOKEN = "param2";

    private OnUserListListener mListener;

    // TODO: Rename and change types of parameters
    private Map<String, String> usr;
    private String uId;
    private String token;
    private View fView;
    private ListView userListView;

    protected CokModel cm;
    protected SqlHelper.UserOpenHelper sql;
    protected UserListAdapter adapter;
    private Activity activity;



    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        return new UserListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object result) {
        GsonBuilder builder = new GsonBuilder();


        builder.setPrettyPrinting().serializeNulls();
        Gson gson = builder.create();

        UserModel.UserList uRes = gson.fromJson(result.toString(), UserModel.UserList.class);


//        List<UserModel.UserItem> ul = sql.uGetAll();
//        if (ul.size() == 0) {
//            sql.uInsert(uRes);
//            ul = sql.uGetAll();
//        }
        adapter.addAll(uRes.users);
        userListView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        Log.e("Loader reset", "user list");
    }

    public class UserListAdapter extends ArrayAdapter<UserModel.UserItem> {
        private int layoutResourceId;

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
                        if (mListener != null) {
                            mListener.getUserDetail(item._id);
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


                List<UserModel.UserItem> ul = sql.uGetAll();
                if (ul.size() == 0) {
                    sql.uInsert(uRes);
                    ul = sql.uGetAll();
                }
                adapter.addAll(ul);
                userListView.setAdapter(adapter);



            }
        }
    }

    public static FragmentUserList newInstance() {
        FragmentUserList fragment = new FragmentUserList();
        return fragment;
    }

    public FragmentUserList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            uId = getArguments().getString(ARG_UID);
            token = getArguments().getString(ARG_TOKEN);
        }
        activity = getActivity();


        cm = new CokModel(getActivity());
        sql = new SqlHelper().new UserOpenHelper(getActivity());
        usr = cm.getUser();
        uId = usr.get("_id");
        token = usr.get("token");
        adapter = new UserListAdapter(getActivity(), R.layout.user_item);

        Bundle args = new Bundle();
        try {
            Loader loader = getActivity().getLoaderManager().initLoader(0, args, this);
            loader.forceLoad();
        } catch (Exception e) {
            Exception ex = e;

        }




        /*
        java.lang.IllegalArgumentException: Object returned from onCreateLoader must not be a non-static inner member class: UserListLoader{424a3750 id=0}



            if(date1.after(date2)){
                System.out.println("Date1 is after Date2");
            }
        */
        /*
        Date lastReqDate = cm.getLastReqDate();
        List<UserModel.UserItem> ul = sql.uGetAll();





        if ((ul.size() == 0) || (lastReqDate == null)) {
            new GetUsers().execute();
        } else {
            adapter.addAll(ul);
            userListView.setAdapter(adapter);
        }

        */

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

    public interface OnUserListListener {
        // TODO: Update argument type and name
        public void getUserDetail(String userId);
    }
}
