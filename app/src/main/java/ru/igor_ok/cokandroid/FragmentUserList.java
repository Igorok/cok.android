package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.app.LoaderManager;
import android.app.Fragment;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.Map;



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
    private View fView = null;
    private ListView userListView;

    protected CokModel cm;
    protected SqlHelper.UserOpenHelper sql;
    protected UserListAdapter adapter;
    private Activity activity;


    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        Loader l = new UserListLoader(activity);
        return l;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object result) {
        Integer lId = loader.getId();
        renderUserList(result);
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
        Log.e("Loader reset", "user list");
    }





    private void renderUserList (Object result) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        Gson gson = builder.create();
        UserModel.UserList uRes = gson.fromJson(result.toString(), UserModel.UserList.class);
        adapter.addAll(uRes.users);
        userListView.setAdapter(adapter);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_user_list, container, false);
        userListView = (ListView) fView.findViewById(R.id.userListView);
        return fView;
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        activity = getActivity();
        cm = new CokModel(activity);
        sql = new SqlHelper().new UserOpenHelper(activity);
        usr = cm.getUser();
        uId = usr.get("_id");
        token = usr.get("token");
        adapter = new UserListAdapter(activity, R.layout.user_item);

        LoaderManager lm = activity.getLoaderManager();
        Loader loader = lm.initLoader(0, null, this);
        loader.forceLoad();
    }


    @Override
    public void onAttach(Activity _act) {
        super.onAttach(_act);

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
