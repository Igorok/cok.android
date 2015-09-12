package ru.igor_ok.cokandroid;


import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends ActionBarActivity
        implements FragmentMain.OnFragmentInteractionListener {
    protected TextView user_name;
    protected TextView user_email;
    protected CokModel cm;


    private String[] navTitles;
    private DrawerLayout navLayout;
    private ListView navList;
    private ArrayAdapter<String> strAdapter;
    private Map<String, String> usr;


    private void fragmentInit (int position) {
        Toast.makeText(getBaseContext(), "" + position, Toast.LENGTH_SHORT).show();
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = FragmentMain.newInstance(usr.get("login"), usr.get("email"));
                break;
            case 1:
                fragment = FragmentUserList.newInstance(usr.get("_id"), usr.get("token"));
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        } else {
            Log.e("Empty fragment ", " " + position);
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = new CokModel(this);
        usr = cm.getUser();

        navTitles = new String[] {"Profile " + usr.get("login"), "Users", "Friends", "Chat rooms"};
        navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList = (ListView) findViewById(R.id.left_drawer);






        strAdapter = new ArrayAdapter<String> (this, R.layout.drawer_nav_item);
        strAdapter.addAll(navTitles);
        navList.setAdapter(strAdapter);
        navList.setClickable(true);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentInit(position);
            }
        });



        fragmentInit(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.show_users:
                Intent i = new Intent(getApplicationContext(), UserListActivity.class);
                startActivity(i);
            case R.id.show_friends:
                return true;
            case R.id.show_chat_room:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void appShowUsers (View view) {
        Intent i = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(i);
    }

    @Override
    public void onFragmentMainInteraction(Uri uri) {

    }


    public void onFragmentUserListInteraction(Uri uri) {

    }
}
