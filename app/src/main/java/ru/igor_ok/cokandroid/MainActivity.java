package ru.igor_ok.cokandroid;


import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
    private android.support.v4.app.FragmentTransaction fTransaction;

    private DrawerLayout navLayout;
    private ListView navList;
    private ArrayAdapter<String> strAdapter;
    private Map<String, String> usr;

    private void fragmentLaunch(String _fId, Map<String, String> _fData) {
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


        String title = null;
        Fragment fragment = fManager.findFragmentByTag(_fId);
        switch (_fId) {
            case "Main":
                if (fragment == null) {
                    fragment = FragmentMain.newInstance();
                    fTransaction = fManager.beginTransaction();
                    fTransaction.replace(R.id.content_frame, fragment);
                    fTransaction.commit();
                }
                title = getString(R.string.title_activity_main);
                setTitle(title);
                break;
            case "uList":
                if (fragment == null) {
                    fragment = FragmentUserList.newInstance();
                    fTransaction = fManager.beginTransaction();
                    fTransaction.replace(R.id.content_frame, fragment);
                    fTransaction.commit();
                }
                title = getString(R.string.title_activity_user_list);
                setTitle(title);
                break;
            case "uDet":
                if (fragment == null) {
                    fragment = FragmentUserDetail.newInstance(_fData.get("fUId"));
                    fTransaction = fManager.beginTransaction();
                    fTransaction.replace(R.id.content_frame, fragment);
                    fTransaction.commit();
                }
                title = getString(R.string.title_activity_user_detail);
                setTitle(title);
                break;
            case "cPers":
                if (fragment == null) {
                    fragment = FragmentChatPersonal.newInstance(_fData.get("fUId"));
                    fTransaction = fManager.beginTransaction();
                    fTransaction.replace(R.id.content_frame, fragment);
                    fTransaction.commit();
                }
                title = getString(R.string.title_activity_personal_chat);
                setTitle(title);
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

        if (_fId.equals("uDet") || _fId.equals("cPers")) {
            String fUId = fStorage.getString("fUId", "");
            _fData.put("fUId", fUId);
        }

        fragmentLaunch(_fId, _fData);
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
        fragmentLaunch("uDet", _fData);
    }

    public void getChatPersonal(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("cPers", _fData);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = new CokModel(this);
        usr = cm.getUser();
        mList = cm.getMenu();
        fManager = getSupportFragmentManager();

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
                fragmentLaunch(mList.get(position).key, null);
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
