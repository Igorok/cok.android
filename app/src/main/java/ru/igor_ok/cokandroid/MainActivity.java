package ru.igor_ok.cokandroid;


import android.content.SharedPreferences;
import android.net.Uri;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity
    implements FragmentMain.OnFragmentInteractionListener,
        FragmentUserList.OnUserListListener,
        FragmentUserDetail.OnUserDetailListener,
        FragmentChatPersonal.OnChatPersListener
{
    protected CokModel cm;

    private String[] navTitles;


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
        Fragment fragment = null;
        switch (_fId) {
            case "0":
                title = getString(R.string.title_activity_main);
                fragment = FragmentMain.newInstance();
                setTitle(title);
                break;
            case "1":
                title = getString(R.string.title_activity_user_list);
                fragment = FragmentUserList.newInstance();
                setTitle(title);
                break;
            case "uDetail":
                title = getString(R.string.title_activity_user_detail);
                fragment = FragmentUserDetail.newInstance(_fData.get("fUId"));
                setTitle(title);
                break;
            case "cPersonal":
                title = getString(R.string.title_activity_personal_chat);
                fragment = FragmentChatPersonal.newInstance(_fData.get("fUId"));
                setTitle(title);
                break;
            default:
                break;
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();

            navLayout.closeDrawer(navList);
        } else {
            Log.d("Empty fragment ", "");
        }

    }

    private void fragmentRestore () {
        SharedPreferences fStorage;
        fStorage = getSharedPreferences("fragment", 0);
        String _fId = fStorage.getString("fId", "0").trim();
        Map<String, String> _fData = new HashMap<String, String>();

        if (_fId.equals("uDetail") || _fId.equals("cPersonal")) {
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
        fragmentLaunch("uDetail", _fData);
    }

    public void getChatPersonal(String userId) {
        Map<String, String> _fData = new HashMap<String, String>();
        _fData.put("fUId", userId);
        fragmentLaunch("cPersonal", _fData);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = new CokModel(this);
        usr = cm.getUser();

        navTitles = new String[] {
            "Profile " + usr.get("login"),
            "Users",
            "Friends",
            "Chat rooms"
        };
        navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.left_drawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        strAdapter = new ArrayAdapter<String> (this, R.layout.drawer_nav_item);
        strAdapter.addAll(navTitles);
        navList.setAdapter(strAdapter);
        navList.setClickable(true);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentLaunch("" + position, null);
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
