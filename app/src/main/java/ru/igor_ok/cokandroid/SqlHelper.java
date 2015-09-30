package ru.igor_ok.cokandroid;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by igor on 26.09.15.
 */
public class SqlHelper {
    private static final String DATABASE_NAME = "cok_db";
    private static final int DATABASE_VERSION = 3;
    private static final String USER_TABLE_NAME = "users";
    private static final String USER_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME +
        " (_id TEXT, login TEXT, email TEXT, friend INTEGER);";


    public class UserOpenHelper extends SQLiteOpenHelper {
        UserOpenHelper(Context mContext) {
            super(mContext, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(USER_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("SQLite", "from version " + oldVersion + " to version " + newVersion);
            db.execSQL("DROP TABLE IF IT EXISTS " + USER_TABLE_NAME);
            onCreate(db);
        }

        // Adding new users
        public void uInsert(UserModel.UserList uList) {
            SQLiteDatabase db = this.getWritableDatabase();
            List<UserModel.UserItem> ul = uList.users;
            int size = ul.size();
            for (int i = 0; i < size; i++) {
                UserModel.UserItem ui = ul.get(i);
                ContentValues values = new ContentValues();
                values.put("_id", ui._id);
                values.put("login", ui.login);
                values.put("email", ui.email);
                values.put("friend", ui.friend);
                db.insert(USER_TABLE_NAME, null, values);
            }
            db.close();
        }

        // select all users
        public List<UserModel.UserItem> uGetAll () {
            List<UserModel.UserItem> ul = new ArrayList<UserModel.UserItem>();
            String selectQuery = "SELECT  * FROM " + USER_TABLE_NAME;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    UserModel.UserItem ui = new UserModel().new UserItem();
                    ui._id = cursor.getString(0);
                    ui.login = cursor.getString(1);
                    ui.email = cursor.getString(2);
                    ui.friend = cursor.getInt(3);
                    ul.add(ui);
                } while (cursor.moveToNext());
            }

            // return contact list
            return ul;
        }
    }
}
