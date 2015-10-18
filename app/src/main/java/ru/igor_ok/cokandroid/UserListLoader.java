package ru.igor_ok.cokandroid;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by igor on 18.10.15.
 */
public class UserListLoader extends AsyncTaskLoader<Object> {
    private JSONObject qwe = new JSONObject();
    private CokModel cm = null;
    private String uId = null;
    private String token = null;

    public UserListLoader(Context context) {
        super(context);

        cm = new CokModel(context);
        Map<String, String> user = cm.getUser();
        uId = user.get("_id");
        token = user.get("token");


    }

    @Override
    public Object loadInBackground() {
        JSONObject uParam = new JSONObject();
        try {
            uParam.put("uId", uId);
            uParam.put("token", token);

            JSONArray uArr = new JSONArray();
            uArr.put(uParam);
            JSONObject jsObj = cm.getJsObj("user.getUserList", uArr);

            String postRes = cm.POST(jsObj.toString());
            JSONObject pR = new JSONObject(postRes);
            JSONArray rA = pR.getJSONArray("result");
            JSONArray uA = rA.getJSONArray(0);
            qwe.put("users", uA);
        } catch (Exception e) {
            Log.e("User loader ", "" + e.getMessage());
        }

        return qwe;
    }

}