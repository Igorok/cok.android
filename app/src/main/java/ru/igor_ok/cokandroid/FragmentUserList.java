package ru.igor_ok.cokandroid;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.Date;


public class FragmentUserList extends Fragment
{
    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView userListView;
    private CokModel cm;
    private UserSqlHelper sh;

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

        Activity act = FragmentUserList.this.getActivity();
        cm = new CokModel(act.getApplicationContext());
        sh = new UserSqlHelper(act.getApplicationContext());
        sh.getWritableDatabase();

        GetUserList getUList = new GetUserList(act.getApplicationContext(), new OnTaskCompleted () {
            @Override
            public void onTaskCompleted(Object result) {
                UserModel.UserList ul = (UserModel.UserList) result;

                if (! ul.act) {
                    sh.uDrop();
                    sh.uInsert(ul.data);
                    cm.setDtInfo("dtUList", new Date());
                }

                UserListAdapter adapter = new UserListAdapter(getActivity(), R.layout.user_item);
                adapter.addAll(sh.uGetAll());
                userListView.setAdapter(adapter);
            }
        });
        getUList.execute();
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
