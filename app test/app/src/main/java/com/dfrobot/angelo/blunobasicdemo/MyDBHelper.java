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
        db.execSQL("CREATE TABLE SL (_id " + "integer primary key autoincrement, " + "PTOTAL text no null, time text no null, LR text no null, SL1 text no null, SL2 text no null, SL3 text no null, SL4 text no null, SL5 text no null, SL6 text no null, SL7 text no null, SL8 text no null)");

        db.execSQL("CREATE TABLE SR (_id " + "integer primary key autoincrement, " + "PTOTAL text no null, time text no null, LR text no null, SR1 text no null, SR2 text no null, SR3 text no null, SR4 text no null, SR5 text no null, SR6 text no null, SR7 text no null, SR8 text no null)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS titles");
        onCreate(db);
    }
}