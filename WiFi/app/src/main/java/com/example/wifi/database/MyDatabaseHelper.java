package com.example.wifi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE = "create table location_info(" +
            "location integer primary key, " +
            "ap_id_01 integer, " +
            "ap_mac_01 text, " +
            "ap_rss_01 integer, " +
            "ap_id_02 integer, " +
            "ap_mac_02 text, " +
            "ap_rss_02 integer, " +
            "ap_id_03 integer, " +
            "ap_mac_03 text, " +
            "ap_rss_03 integer, " +
            "ap_id_04 integer, " +
            "ap_mac_04 text, " +
            "ap_rss_04 integer)";

    private final Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行建表语句
        db.execSQL(CREATE_TABLE);
        Toast.makeText(mContext, "建表成功！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}