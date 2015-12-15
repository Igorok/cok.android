package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.Date;


public class FragmentFriendList extends Fragment
{

    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView userListView;
    private CokModel cm;
    private FriendSqlHelper sh;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        userListView = (ListView) fView.findViewById(R.id.userListView);

        Activity act = FragmentFriendList.this.getActivity();
        cm = new CokModel(act.getApplicationContext());
        sh = new FriendSqlHelper(act.getApplicationContext());
        sh.getWritableDatabase();

        GetFriendList getFList = new GetFriendList(act.getApplicationContext(), new OnTaskCompleted () {
            @Override
            public void onTaskCompleted(Object result) {
                UserModel.UserList fl = (UserModel.UserList) result;
                if (! fl.act) {
                    sh.uDrop();
                    sh.insert(fl.data);
                    cm.setDtInfo("dtFList", new Date());
                }

                FriendListAdapter adapter = new FriendListAdapter(getActivity(), R.layout.user_item);
                adapter.addAll(sh.getAll());
                userListView.setAdapter(adapter);
            }
        });
        getFList.execute();
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
