package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;


public class FragmentRoomList extends Fragment
{

    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView roomListView;



    public static FragmentRoomList newInstance() {
        FragmentRoomList fragment = new FragmentRoomList();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_room_list, container, false);
        return fView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        roomListView = (ListView) fView.findViewById(R.id.roomListView);
        Activity act = FragmentRoomList.this.getActivity();
        GetRoomList getRL = new GetRoomList(act.getApplicationContext(), new OnTaskCompleted () {
            @Override
            public void onTaskCompleted(Object result) {
                RoomListAdapter adapter = new RoomListAdapter(getActivity(), R.layout.room_item);
                adapter.addAll((List<ChatModel.RoomItem>) result);
                roomListView.setAdapter(adapter);
            }
        });
        getRL.execute();
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
