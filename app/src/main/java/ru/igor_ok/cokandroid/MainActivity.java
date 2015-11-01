package ru.igor_ok.cokandroid;


import android.content.SharedPreferences;
import android.net.Uri;
import android.app.Fragment;
import android.app.FragmentManager;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity
    implements FragmentMain.OnFragmentInteractionListener,
        FragmentUserList.OnUserListListener,
        FragmentUserDetail.OnUserDetailListener,
        FragmentChatPersonal.OnChatPersListener
{
    protected CokModel cm;

    private List<CokModel.mItem> mList;



    private FragmentManager fManager;
    private android.app.FragmentTransaction fTransaction;

    private DrawerLayout navLayout;
    private ListView navList;
    private ArrayAdapter<String> strAdapter;
    private Map<String, String> usr;





    private FragmentMain fMain = null;
    private FragmentUserList fUserList = null;
    private FragmentUserDetail fUserDetail = null;
    private FragmentChatPersonal fChatPersonal = null;


    private void fragmentLaunch(String _fId, Map<String, String> _fData, Boolean rest) {
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
        switch (_fId) {
            case "Main":
                setTitle(getString(R.string.title_activity_main));
                break;
            case "uList":
                setTitle(getString(R.string.title_activity_user_list));
                break;
            case "uDet":
                setTitle(getString(R.string.title_activity_user_detail));
                break;
            case "cPers":
                setTitle(getString(R.string.title_activity_personal_chat));
                break;
            default:
                break;
        }

        Fragment fragment = null;
        if (rest) {
            fragment = fManager.findFragmentById(R.id.content_frame);
        }









        if (fragment == null) {
            fTransaction = fManager.beginTransaction();
            switch (_fId) {
                case "Main":
                    fMain = (FragmentMain) getFragmentManager().findFragmentByTag(_fId);
                    if (fMain == null) {
                        fMain = FragmentMain.newInstance();
                    }
                    fTransaction.replace(R.id.content_frame, fMain, _fId);
                    break;
                case "uList":
                    fUserList = (FragmentUserList) getFragmentManager().findFragmentByTag(_fId);
                    if (fUserList == null) {
                        fUserList = FragmentUserList.newInstance();
                    }
                    fTransaction.replace(R.id.content_frame, fUserList, _fId);
                    break;
                case "uDet":
                    fUserDetail = (FragmentUserDetail) getFragmentManager().findFragmentByTag(_fId);
                    Bundle arg = new Bundle();
                    arg.putString("userId", _fData.get("fUId"));
                    if (fUserDetail == null) {
                        fUserDetail = FragmentUserDetail.newInstance(_fData.get("fUId"));
                    } else {
                        fUserDetail.setArguments(arg);
                    }
                    fTransaction.replace(R.id.content_frame, fUserDetail, _fId);
                    break;
                case "cPers":
                    fChatPersonal = (FragmentChatPersonal) getFragmentManager().findFragmentByTag(_fId);
                    if (fChatPersonal == null) {
                        fChatPersonal = FragmentChatPersonal.newInstance(_fData.get("fUId"));
                    }
                    fTransaction.replace(R.id.content_frame, fChatPersonal, _fId);
                    break;
                default:
                    break;
            }
            fTransaction
                .addToBackStack(_fId)
                .commit();
        }
        navLayout.closeDrawer(navList);
    }

    private void fragmentRestore () {
        SharedPreferences fStorage;
        fStorage = getSharedPreferences("fragment", 0);
        String _fId = fStorage.getString("fId", "Main").trim();
        Map<String, String> _fData = new HashMap<String, String>();

        if (_fId.equals("uDet") || _fId.equals("cPers")) {
            String fUId = fStorage.getString("fUId", "");
            _fData.put("fUId", fUId);
        }

        fragmentLaunch(_fId, _fData, true);
    }

    @Override
    public void onFragmentMainInteraction (Uri uri) {

    }

    public void setTitle (String _title) {
        android.support.v7.app.ActionBar actionBar = MainActivity.this.getSupportActionBar();
        actionBar.setTitle(_title);
    }

    public void getUserDetail(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("uDet", _fData, false);
    }

    public void getChatPersonal(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("cPers", _fData, false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        cm = new CokModel(this);
        usr = cm.getUser();
        mList = cm.getMenu();
        fManager = getFragmentManager();

        navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.left_drawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        strAdapter = new ArrayAdapter<String> (this, R.layout.drawer_nav_item);
        int mSize = mList.size();
        for (int i = 0; i < mSize; i ++) {
            strAdapter.add(mList.get(i).title);
        }
        navList.setAdapter(strAdapter);

        navList.setClickable(true);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentLaunch(mList.get(position).key, null, false);
            }
        });

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
