package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.app.LoaderManager;
import android.app.Fragment;
import android.content.Loader;
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


public class FragmentUserList extends Fragment
{

    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView userListView;
    private CokModel cm;
    private UserOpenHelper sh;
    private String uId;
    private String token;



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
                try {
                    uParam.put("uId", uId);
                    uParam.put("token", token);
                    uParam.put("date", cm.getDtInfo("dtUList"));

                    JSONArray uArr = new JSONArray();
                    uArr.put(uParam);
                    JSONObject jsObj = cm.getJsObj("user.getMobileUserList", uArr);

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
                        sh.uInsert(ul.data);
                        cm.setDtInfo("dtUList", new Date());
                    }
                } catch (Exception e) {
                    cm.errToast(e);
                }

                return sh.uGetAll();
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
                UserListAdapter adapter = new UserListAdapter(getActivity(), R.layout.user_item);
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

        Activity act = FragmentUserList.this.getActivity();
        cm = new CokModel(act.getApplicationContext());
        sh = new UserOpenHelper(act.getApplicationContext());
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
