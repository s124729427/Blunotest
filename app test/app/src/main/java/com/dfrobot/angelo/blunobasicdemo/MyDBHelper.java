package com.dfrobot.angelo.blunobasicdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyBooks";
    private static final int DATABASE_VERSION = 1;
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ONE (_id " + "integer primary key autoincrement, " + "x real no null, LR real no null, time text no null, value real no null, value1 real no null, value2 real no null, value3 real no null, value4 real no null, value5 real no null, value6 real no null, value7 real no null, value8 real no null)");

        db.execSQL("CREATE TABLE TWO (_id " + "integer primary key autoincrement, " + "x real no null, LR real no null, time text no null, value real no null, value1 real no null, value2 real no null, value3 real no null, value4 real no null, value5 real no null, value6 real no null, value7 real no null, value8 real no null)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS titles");
        onCreate(db);
    }
}