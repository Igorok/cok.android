package ru.igor_ok.cokandroid;


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


    private void fragmentInit (Fragment fragment) {
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

    // change views
    private void menuClick (int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = FragmentMain.newInstance();
                break;
            case 1:
                String title = getString(R.string.title_activity_user_list);
                fragment = FragmentUserList.newInstance();
                setTitle(title);
                break;
            default:
                break;
        }
        fragmentInit(fragment);
    }

    @Override
    public void onFragmentMainInteraction (Uri uri) {

    }

    public void setTitle (String _title) {
        android.support.v7.app.ActionBar actionBar = MainActivity.this.getSupportActionBar();
        actionBar.setTitle(_title);
    }

    public void getUserDetail(String userId) {
        Fragment fragment = FragmentUserDetail.newInstance(userId);
        fragmentInit(fragment);
    }

    public void getChatPersonal(String personId) {
        Toast.makeText(getBaseContext(), "" + personId, Toast.LENGTH_SHORT).show();
        Fragment fragment = FragmentChatPersonal.newInstance(personId);
        fragmentInit(fragment);
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
                menuClick(position);
            }
        });

        menuClick(0);
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
