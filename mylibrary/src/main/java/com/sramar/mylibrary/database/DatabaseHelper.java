package com.sramar.mylibrary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "db_name";
    private static int DATABASE_VERSION = 1;
    private Class markerPackageName = ABeans.class;

    public DatabaseHelper(Context context, String name,
                          CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("UseDatabase", "创建数据库: "+DATABASE_NAME+", 版本: "+DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newVersion) {
        switch (old){
            case 1:
            case 2:


        }
    }

    public static void setDataName(String databaseName) {
        DATABASE_NAME = databaseName;
    }

    public static void setDataVersion(int databaseVersion) {
        DATABASE_VERSION = databaseVersion;
    }

    public void setMarkerPackageName(Class markerPackageName) {
        this.markerPackageName = markerPackageName;
    }

    public static String getDataName() {
        return DATABASE_NAME;
    }

    public static int getDataVersion() {
        return DATABASE_VERSION;
    }

    public Class getMarkerPackageName() {
        return markerPackageName;
    }
    class a extends DatabaseHelper{
        public a(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);

        }
    }
}