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
import java.util.List;



public class FragmentUserList extends Fragment
    implements LoaderManager.LoaderCallbacks<List<UserModel.UserItem>>
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_UID= "param1";
//    private static final String ARG_TOKEN = "param2";


    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView userListView;


    @Override
    public Loader<List<UserModel.UserItem>> onCreateLoader(int i, Bundle bundle) {
        Loader l = new UserListLoader(getActivity());
        return l;
    }

    @Override
    public void onLoadFinished(Loader<List<UserModel.UserItem>> loader, List<UserModel.UserItem> userItems) {
        UserListAdapter adapter = new UserListAdapter(getActivity(), R.layout.user_item);
        adapter.addAll(userItems);
        userListView.setAdapter(adapter);
    }


    @Override
    public void onLoaderReset(Loader<List<UserModel.UserItem>> loader) {
        Log.e("Loader reset", "user list");
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_user_list, container, false);
        return fView;
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        userListView = (ListView) fView.findViewById(R.id.userListView);

        LoaderManager lm = getActivity().getLoaderManager();
        Loader loader = lm.getLoader(0);
        if( loader == null ) {
            loader = lm.initLoader(0, null, this);
            loader.forceLoad();
        }
        else {
            lm.restartLoader(0, null, this).forceLoad();
        }
    }




    @Override
    public void onAttach(Activity _act) {
        super.onAttach(_act);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Loader l = getActivity().getLoaderManager().getLoader(0);
        if (l != null) {
            getActivity().getLoaderManager().destroyLoader(0);
        }
    }
}
