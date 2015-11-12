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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class FragmentRoomList extends Fragment
{

    // TODO: Rename and change types of parameters
    private View fView = null;
    private ListView roomListView;
    private CokModel cm;
    private FriendSqlHelper sh;
    private String uId;
    private String token;



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


    protected class GetUsers extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground (Void... params) {
            try {
                JSONObject uParam = new JSONObject();
                uParam.put("uId", uId);
                uParam.put("token", token);
                uParam.put("date", cm.getDtInfo("dtFList"));

                JSONArray arr = new JSONArray();
                arr.put(uParam);
                JSONObject jsObj = cm.getJsObj("chat.getChatList", arr);

                String postRes = cm.POST(jsObj.toString());
                JSONObject pR = new JSONObject(postRes);
                JSONArray rA = pR.getJSONArray("result");
                JSONArray jsRl = rA.getJSONArray(0);

                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting().serializeNulls();
                Gson gson = builder.create();
                Type collectionType = new TypeToken<List<ChatModel.RoomItem>>() {
                }.getType();
                List<ChatModel.RoomItem> rl = gson.fromJson(jsRl.toString(), collectionType);

                return rl;
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
                RoomListAdapter adapter = new RoomListAdapter(getActivity(), R.layout.room_item);
                adapter.addAll((List<ChatModel.RoomItem>) result);
                roomListView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);

        roomListView = (ListView) fView.findViewById(R.id.roomListView);

        Activity act = FragmentRoomList.this.getActivity();
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
