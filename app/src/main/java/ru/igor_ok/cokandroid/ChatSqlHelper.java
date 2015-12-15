package ru.igor_ok.cokandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class ChatSqlHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cok_db";
    private static final int DATABASE_VERSION = 3;
    private static final String MESSAGE_TABLE_NAME = "messages";
    private static final String MESSAGE_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + MESSAGE_TABLE_NAME +
        " (date TEXT, login TEXT, msg TEXT, uId TEXT, rId TEXT, pId TEXT, dt INTEGER);";

    ChatSqlHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "from version " + oldVersion + " to version " + newVersion);
        this.mDrop();
    }

    public void mDrop () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
        onCreate(db);
    }

    // insert new message
    public void insertMsg(List<ChatModel.MsgItem> mList, String pId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            ChatModel.MsgItem msg = mList.get(i);
            ContentValues values = new ContentValues();

            values.put("date", msg.date);
            values.put("login", msg.login);
            values.put("msg", msg.msg);
            values.put("uId", msg.uId);
            values.put("rId", msg.rId);
            values.put("pId", pId);
            values.put("dt", msg.dt);
            db.insert(MESSAGE_TABLE_NAME, null, values);
        }
        db.close();
    }

    // select messages for the chat room with the limit
    public List<ChatModel.MsgItem> getMsg (String _type, String _id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        List<ChatModel.MsgItem> mList = new ArrayList<ChatModel.MsgItem>();

        String selectQuery = "SELECT  * FROM " + MESSAGE_TABLE_NAME;
        if (_type == "room") {
            selectQuery += " where rId = '" + _id + "' ";
        } else {
            selectQuery += " where pId = '" + _id + "' ";
        }
        selectQuery += "order by dt desc;";


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatModel.MsgItem msg = new ChatModel.MsgItem();
                msg.date = cursor.getString(0);
                msg.login = cursor.getString(1);
                msg.msg = cursor.getString(2);
                msg.uId = cursor.getString(3);
                msg.rId = cursor.getString(4);
                msg.dt = cursor.getInt(5);
                mList.add(msg);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return mList;
    }

    // select messages for the chat room with the limit
    public Integer removeOld (String _tp, String _id, Integer dt) {
        SQLiteDatabase db = this.getWritableDatabase();
        String dq = "";
        if (_tp == "room") {
            dq += "rId = " + _id;
        } else {
            dq += "pId = " + _id;
        }
        dq += "and dt < " + dt;
        int delCount = db.delete(MESSAGE_TABLE_NAME, dq, null);
        return delCount;
    }
}


