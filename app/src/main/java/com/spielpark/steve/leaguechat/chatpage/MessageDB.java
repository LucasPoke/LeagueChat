package com.spielpark.steve.leaguechat.chatpage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Steve on 2/6/2015.
 */
public class MessageDB extends SQLiteOpenHelper {

    private static MessageDB mInstance;
    private static SQLiteDatabase db;
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE " + TableEntry.TABLE_NAME;
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TableEntry.TABLE_NAME + " ( " +
            TableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TableEntry.COLUMN_FROM + " varchar, " +
            TableEntry.COLUMN_TO + " varchar, " +
            TableEntry.COLUMN_MESSAGE + " text, " +
            TableEntry.COLUMN_TIME + " int, " +
            TableEntry.COLUMN_PROFILE + " int);";

    public MessageDB(Context context) {
        super(context, TableEntry.DB_NAME, null, 22);
        db = getWritableDatabase();
    }

    public static MessageDB getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new MessageDB(ctx.getApplicationContext());
            return mInstance;
        } else {
            return mInstance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("MessageDB/onCreate", "Created DB.");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        Log.d("MessageDB/onUpgrade", "Upgraded DB.");
    }

    public static abstract class TableEntry implements BaseColumns {
        public TableEntry() {

        }

        public static final String DB_NAME = "messages.db";
        public static final String TABLE_NAME = "messages";
        public static final String _ID = "_id";
        public static final String COLUMN_FROM = "_from";
        public static final String COLUMN_TO = "_to";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_PROFILE = "profilepic";
    }
}
