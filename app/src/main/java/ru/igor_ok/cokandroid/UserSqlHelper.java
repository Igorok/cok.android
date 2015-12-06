package ru.igor_ok.cokandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserSqlHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cok_db";
    private static final int DATABASE_VERSION = 3;
    private static final String USER_TABLE_NAME = "users";
    private static final String USER_TABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME +
                    " (_id TEXT, login TEXT, email TEXT, friend INTEGER);";

    UserSqlHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "from version " + oldVersion + " to version " + newVersion);
        this.uDrop();
    }

    public void uDrop () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

    // Adding new users
    public void uInsert(List<UserModel.UserItem> uList) {
        SQLiteDatabase db = this.getWritableDatabase();
        int size = uList.size();
        for (int i = 0; i < size; i++) {
            UserModel.UserItem ui = uList.get(i);
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
        cursor.close();

        // return contact list
        return ul;
    }

    /**
     *
     * @param _id of user
     * @return UserItem detail info about user
     */
    public UserModel.UserItem uGetOne (String _id) {
        UserModel.UserItem ui = null;
        String selectQuery = "SELECT  * FROM " + USER_TABLE_NAME + " where _id = '" + _id + "';";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Integer count = cursor.getCount();
        if (count > 0) {
            cursor.moveToFirst();
            ui = new UserModel().new UserItem();
            ui._id = cursor.getString(0);
            ui.login = cursor.getString(1);
            ui.email = cursor.getString(2);
            ui.friend = cursor.getInt(3);
        }

        cursor.close();
        // return contact list
        return ui;
    }
}


