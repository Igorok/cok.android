package ru.igor_ok.cokandroid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 08.07.15.
 */
public class AuthHelper {
    private String login = "";
    private String password = "";
    private boolean validated = false;
    private ArrayList<String> errors = new ArrayList<String>();

    public void setLogin (String login) {
        this.login = login;
    }
    public void setPassword (String password) {
        this.password = password;
    }



    public boolean isValid () {
        this.errors.clear();
        if (this.login.length() == 0) {
            errors.add("login");
        }
        if (this.password.length() == 0) {
            errors.add("password");
        }
        if (errors.isEmpty()) {
            this.validated = true;
        }
        return this.validated;
    }

    public ArrayList<String> validError () {
        return this.errors;
    }
    public String getJson() {
        Auth auth = new Auth();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting().serializeNulls();
        Gson gson = builder.create();

        AuthParams aParam = new AuthParams();
        aParam.login = this.login;
        aParam.password = this.password;
        auth.params.add(aParam);

        return gson.toJson(auth);
    }



    public class AuthParams {
        public String login;
        public String password;
    }
    public class Auth {
        public String jsonrpc = "2.0";
        public String method = "user.Authorise";
        public List<AuthParams> params = new ArrayList<AuthParams>();
    }

    public class AuthResult {
        public String _id;
        public String token;
        public String login;
        public String email;
        public String group;
        public Integer status;
    }
    public class Authorised {
        public String jsonrpc;
        public Integer id;
        public List<AuthResult> result = new ArrayList<AuthResult>();
    }

    public Object aRes;
}
