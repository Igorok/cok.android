package ru.igor_ok.cokandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {
    protected EditText loginField;
    protected EditText passwordField;
    protected Button loginBtn;
    protected CokModel cm;
    protected Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cm = new CokModel(this); //Here the context is passing

        loginField = (EditText) findViewById(R.id.login_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        loginBtn = (Button) findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                appLogin(v);
            }
        });

        context = getApplicationContext();
    }


    public void appLogin (View view) {
        String login = loginField.getText().toString().trim();
        if (login.length() == 0) {
            loginField.setError("Login is required");
            return;
        }
        String password = passwordField.getText().toString().trim();
        if (password.length() == 0) {
            passwordField.setError("Password is required");
            return;
        }

        AppLogin appLogin = new AppLogin(login, password, context, new OnTaskCompleted () {
            @Override
            public void onTaskCompleted(Object result) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
            }
        });
        appLogin.execute();
    }
}