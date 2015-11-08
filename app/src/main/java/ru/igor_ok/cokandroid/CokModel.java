package ru.igor_ok.cokandroid;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by igor on 04.07.15.
 */
public class CokModel {
    private Context mContext;
    private Map<String, String> user = new HashMap<>();
    private String restUrl = null;
    public SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public class mItem {
        public String key;
        public String title;
    }
    private List<mItem> mList = new ArrayList<mItem>();


    public CokModel(Context mContext) {
        this.mContext = mContext;
        SharedPreferences userStorage;
        userStorage = mContext.getSharedPreferences("user", 0);
        user.put("token", userStorage.getString("token", ""));
        user.put("_id", userStorage.getString("_id", ""));
        user.put("login", userStorage.getString("login", ""));
        user.put("email", userStorage.getString("email", ""));

        restUrl = mContext.getString(R.string.jsonrpc);

        mItem mi = new mItem();
        mi.key = "Main";
        mi.title = mContext.getString(R.string.title_activity_main) + " " + user.get("login");
        mList.add(mi);

        mi = new mItem();
        mi.key = "uList";
        mi.title = mContext.getString(R.string.title_activity_user_list);
        mList.add(mi);

        mi = new mItem();
        mi.key = "fList";
        mi.title = "Friends";
        mList.add(mi);

        mi = new mItem();
        mi.key = "cRom";
        mi.title = "Chat rooms";
        mList.add(mi);
    }


    /**
     * @param _t type of the date (dtIfo - info about profile, dtUlist - last list of users)
     * @return d date for requested type
     */
    public String getDtInfo(String _t) {
        SharedPreferences userStorage;
        userStorage = mContext.getSharedPreferences("user", 0);
        return userStorage.getString(_t, null);
    }

    /**
     * @param _t type of the date (dtIfo - info about profile, dtUlist - last list of users)
     * @param d date of the last request for current type
     */
    public void setDtInfo(String _t, Date d) {
        String dStr = dFormat.format(d);
        SharedPreferences userStorage = mContext.getSharedPreferences("user", 0);
        SharedPreferences.Editor editor = userStorage.edit();
        editor.putString(_t, dStr);
        editor.commit();
    }


    public void errToast(Exception e) {
        Exception ex = e;
        Log.e("get message ", "" + ex.getMessage());
        Context context = this.mContext.getApplicationContext();
        CharSequence text = ex.getMessage();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    protected Map<String, String> getUser() {
        return user;
    }

    protected List getMenu() {
        return mList;
    }

    protected JSONObject getJsObj(String _method, JSONArray _params) throws Exception {
        JSONObject jsObj = new JSONObject();
        Exception ex = null;
        try {
            jsObj.accumulate("id", 1);
            jsObj.accumulate("jsonrpc", "2.0");
            jsObj.accumulate("method", _method);
            jsObj.put("params", _params);
        } catch (JSONException e) {
            Log.e("JSONException ", "" + e.getMessage());
            ex = e;
        }
        if (ex != null) {
            throw ex;
        }
        return jsObj;
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService(this.mContext.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }

    protected String POST(String jsonData) throws Exception {
        Exception ex = null;
        if (!this.isNetworkConnected()) {
            ex = new Exception("internet connection not found");
        }

        InputStream inputStream = null;
        String result = "";
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(restUrl);
        try {
            StringEntity se = new StringEntity(jsonData.toString());
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            ex = e;
            Log.e("Http Post ", "" + e.getMessage());
        }
        if (ex != null) {
            throw ex;
        }
        try {
            JSONObject postRes = new JSONObject(result);
            if (postRes.has("error")) {
                Log.e("Http Post ", "" + postRes.getString("error"));
                throw new Exception("web error");
            }
        } catch (JSONException e) {
            Log.e("JSONException ", "" + e.getMessage());
            throw e;
        }
        Log.d("answer", result.trim());
        return result.toString().trim();
    }
}


/**
 * get user list from http
 */
class UserListLoader extends AsyncTaskLoader<List<UserModel.UserItem>> {
    private CokModel cm = null;
    private String uId = null;
    private String token = null;
    private UserOpenHelper sh = null;
    public UserListLoader(Context context) {
        super(context);
        cm = new CokModel(context);
        sh = new UserOpenHelper(context);
        Map<String, String> user = cm.getUser();
        uId = user.get("_id");
        token = user.get("token");

        onContentChanged();
    }

    @Override
    public List<UserModel.UserItem> loadInBackground() {
        JSONObject uParam = new JSONObject();
        try {
            uParam.put("uId", uId);
            uParam.put("token", token);
            uParam.put("date", cm.getDtInfo("dtUList"));

            JSONArray uArr = new JSONArray();
            uArr.put(uParam);
            JSONObject jsObj = cm.getJsObj("user.getMobileUserList", uArr);

            String postRes = cm.POST(jsObj.toString());
            JSONObject pR = new JSONObject(postRes);
            JSONArray rA = pR.getJSONArray("result");
            JSONObject resObj = rA.getJSONObject(0);

            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting().serializeNulls();
            Gson gson = builder.create();
            UserModel.UserList ul = gson.fromJson(resObj.toString(), UserModel.UserList.class);


            if (! ul.act) {
                sh.uDrop();
                sh.uInsert(ul.data);
                cm.setDtInfo("dtUList", new Date());
            }
        } catch (Exception e) {
            Log.e("User loader ", "" + e.getMessage());
            cm.errToast(e);
        }

        return sh.uGetAll();
    }


    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<UserModel.UserItem> ul) {
        Boolean start = isStarted();
        Boolean reset = isReset();
        Boolean abadon = isAbandoned();
        if (start) {
            super.deliverResult(ul);
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<UserModel.UserItem> ul) {
        super.onCanceled(ul);

    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();
    }
}



//getUserUpdated