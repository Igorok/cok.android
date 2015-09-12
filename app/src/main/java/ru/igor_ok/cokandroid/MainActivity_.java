package ru.igor_ok.cokandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

public class MainActivity_ extends ActionBarActivity {
    protected TextView user_name;
    protected TextView user_email;
    protected CokModel cm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cm = new CokModel(this);
        Map<String, String> usr = cm.getUser();

        user_name = (TextView) findViewById(R.id.user_name);
        user_email = (TextView) findViewById(R.id.user_email);
        user_name.setText(usr.get("login"));
        user_email.setText(usr.get("email"));
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
}
