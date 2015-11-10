package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class FragmentFriendList extends Fragment
{

    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView userListView;
    private CokModel cm;
    private FriendSqlHelper sh;
    private String uId;
    private String token;



    public static FragmentFriendList newInstance() {
        FragmentFriendList fragment = new FragmentFriendList();
        return fragment;
    }

    public FragmentFriendList() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_user_list, container, false);
        return fView;
    }


    protected class GetUsers extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground (Void... params) {
            try {
                JSONObject uParam = new JSONObject();
                uParam.put("uId", uId);
                uParam.put("token", token);
                uParam.put("date", cm.getDtInfo("dtFList"));

                JSONArray uArr = new JSONArray();
                uArr.put(uParam);
                JSONObject jsObj = cm.getJsObj("user.getMobileFriendList", uArr);

                String postRes = cm.POST(jsObj.toString());
                JSONObject pR = new JSONObject(postRes);
                JSONArray rA = pR.getJSONArray("result");
                JSONObject resObj = rA.getJSONObject(0);

                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting().serializeNulls();
                Gson gson = builder.create();
                UserModel.UserList ul = gson.fromJson(resObj.toString(), UserModel.UserList.class);


                if (! ul.act) {
                    sh.uDrop();
                    sh.insert(ul.data);
                    cm.setDtInfo("dtFList", new Date());
                }

                return sh.getAll();
            } catch (Exception e) {
                Log.e("post exception ", "" + e.getMessage());
                return e;
            }
        }
        @Override
        protected void onPostExecute(Object result) {

            if (result instanceof Exception) {
                cm.errToast((Exception) result);
            }
            else {
                FriendListAdapter adapter = new FriendListAdapter(getActivity(), R.layout.user_item);
                adapter.addAll((List<UserModel.UserItem>) result);
                userListView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        userListView = (ListView) fView.findViewById(R.id.userListView);

        Activity act = FragmentFriendList.this.getActivity();
        cm = new CokModel(act.getApplicationContext());
        sh = new FriendSqlHelper(act.getApplicationContext());
        Map<String, String> user = cm.getUser();
        uId = user.get("_id");
        token = user.get("token");

        new GetUsers().execute();
    }




    @Override
    public void onAttach(Activity _act) {
        super.onAttach(_act);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
