package ru.igor_ok.cokandroid;


import android.content.Context;
import android.content.SharedPreferences;
import android.app.FragmentManager;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity
    implements FragmentMain.OnFragmentInteractionListener,
        FragmentUserDetail.OnUserDetailListener,
        FragmentFriendDetail.OnFriendDetailListener,
        FragmentChatPersonal.OnChatPersListener,
        FragmentChatRoom.OnChatRoomListener
{
    protected CokModel cm;

    private List<CokModel.mItem> mList;
    private DrawerLayout navLayout;
    private ListView navList;
    private ArrayAdapter<String> strAdapter;
    private Map<String, String> usr;

    public void fragmentLaunch(String _fId, Map<String, String> _fData) {
        SharedPreferences fStorage = getSharedPreferences("fragment", 0);
        SharedPreferences.Editor editor = fStorage.edit();
        editor.putString("fId", _fId);
        if (_fData != null) {
            for (String s : _fData.keySet()) {
                String key = s;
                String value = _fData.get(key);
                editor.putString(key, value);
            }
        }
        editor.commit();


        FragmentManager fManager = MainActivity.this.getFragmentManager();

        switch (_fId) {
            case "Main":
                setTitle(getString(R.string.title_activity_main));
                FragmentMain fMain = (FragmentMain) fManager.findFragmentByTag(_fId);
                if (fMain == null) {
                    fMain = FragmentMain.newInstance();
                }
                fManager.beginTransaction()
                        .replace(R.id.content_frame, fMain, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "uList":
                setTitle(getString(R.string.title_activity_user_list));
                FragmentUserList fUserList = (FragmentUserList) fManager.findFragmentByTag(_fId);
                if (fUserList == null) {
                    fUserList = FragmentUserList.newInstance();
                }
                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, fUserList, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "uDet":
                setTitle(getString(R.string.title_activity_user_detail));
                FragmentUserDetail fUserDetail = (FragmentUserDetail) fManager.findFragmentByTag(_fId);
                if (fUserDetail == null) {
                    fUserDetail = FragmentUserDetail.newInstance(_fData.get("fUId"));
                } else {
                    fUserDetail.setUserId(_fData.get("fUId"));
                }
                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, fUserDetail, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "fList":
                setTitle(getString(R.string.title_activity_friend_list));
                FragmentFriendList friendList = (FragmentFriendList) fManager.findFragmentByTag(_fId);
                if (friendList == null) {
                    friendList = FragmentFriendList.newInstance();
                }
                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, friendList, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "fDet":
                setTitle(getString(R.string.title_activity_friend_detail));
                FragmentFriendDetail friendDetail = (FragmentFriendDetail) fManager.findFragmentByTag(_fId);
                if (friendDetail == null) {
                    friendDetail = FragmentFriendDetail.newInstance(_fData.get("fUId"));
                } else {
                    friendDetail.setUserId(_fData.get("fUId"));
                }
                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, friendDetail, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "cPers":
                setTitle(getString(R.string.title_activity_personal_chat));
                FragmentChatPersonal fChatPersonal = (FragmentChatPersonal) fManager.findFragmentByTag(_fId);
                if (fChatPersonal == null) {
                    fChatPersonal = FragmentChatPersonal.newInstance(_fData.get("fUId"));
                } else {
                    fChatPersonal.setUserId(_fData.get("fUId"));
                }
                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, fChatPersonal, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "cRoomList":
                setTitle(getString(R.string.title_activity_group_chat));
                FragmentRoomList fRoomList = (FragmentRoomList) fManager.findFragmentByTag(_fId);
                if (fRoomList == null) {
                    fRoomList = FragmentRoomList.newInstance();
                }
                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, fRoomList, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            case "cRoom":
                setTitle(getString(R.string.title_activity_group_chat));
                FragmentChatRoom fRoom = (FragmentChatRoom) fManager.findFragmentByTag(_fId);
                String rId = _fData.get("rId");
                if (fRoom == null) {
                    fRoom = FragmentChatRoom.newInstance(rId);
                } else {
                    fRoom.setRoomId(rId);
                }

                fManager
                        .beginTransaction()
                        .replace(R.id.content_frame, fRoom, _fId)
                        .addToBackStack(_fId)
                        .commit();
                fManager.executePendingTransactions();
                break;
            default:
                break;
        }

        navLayout.closeDrawer(navList);
    }


    private void fragmentRestore () {
        SharedPreferences fStorage;
        fStorage = getSharedPreferences("fragment", 0);
        String _fId = fStorage.getString("fId", "Main").trim();
        Map<String, String> _fData = new HashMap<String, String>();

        if (_fId.equals("uDet") || _fId.equals("fDet") || _fId.equals("cPers")) {
            String fUId = fStorage.getString("fUId", "");
            _fData.put("fUId", fUId);
        } else if (_fId.equals("cRoom")) {
            String rId = fStorage.getString("rId", "");
            _fData.put("rId", rId);
        }

        fragmentLaunch(_fId, _fData);
    }


    public void setTitle (String _title) {
        android.support.v7.app.ActionBar actionBar = MainActivity.this.getSupportActionBar();
        actionBar.setTitle(_title);
    }

    public void getUserList() {
        Map<String, String> _fData = new HashMap<String, String>();
        fragmentLaunch("uList", _fData);
    }
    public void getFriendList() {
        Map<String, String> _fData = new HashMap<String, String>();
        fragmentLaunch("fList", _fData);
    }

    public void getChatList() {
        Map<String, String> _fData = new HashMap<String, String>();
        fragmentLaunch("cRoomList", _fData);
    }

    public void getUserDetail(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("uDet", _fData);
    }
    public void getFriendDetail(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("fDet", _fData);
    }

    public void getChatPersonal(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("cPers", _fData);
    }

    public void getChatRoom(String rId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("rId", rId);
        fragmentLaunch("cRoom", _fData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        cm = new CokModel(this);
        usr = cm.getUser();
        mList = cm.getMenu();

        navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.left_drawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavListAdapter adapter = new NavListAdapter(this, R.layout.drawer_nav_item);
        adapter.addAll(mList);
        navList.setAdapter(adapter);

        fragmentRestore();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                navLayout.openDrawer(navList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


