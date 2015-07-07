package ru.igor_ok.cokandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    protected Intent intent;
    protected TextView hello_world;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences user = getSharedPreferences("user", 0);
        String login = user.getString("login", "");
        String email = user.getString("email", "");
        String token = user.getString("token", "");
        String _id = user.getString("_id", "");




        Bundle extras = getIntent().getExtras();
        String json = "";
        if (extras != null) {
            json = extras.getString("json");
        }



        hello_world = (TextView) findViewById(R.id.hello_world);
        hello_world.setTextSize(40);
        hello_world.setText(String.format(
                "Main activity login %s, email %s, token %s, _id %s", login, email, token, _id
        ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
