package com.sramar.mylibrary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;


public abstract class IDatabaseHelper extends SQLiteOpenHelper {

    private static String databaseName = "db_name";
    private static int databaseVersion = 1;
    private Class markerPackageName = IBeans.class;

    public IDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        markerPackageName = setMarkerPackageName();
    }

    public IDatabaseHelper(Context context) {
        this(context,databaseName,null,databaseVersion);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("UseDatabase", "创建数据库: "+databaseName+", 版本: "+databaseVersion);
    }

    @Override
    public abstract void onUpgrade(SQLiteDatabase db, int old, int newVersion);


    public abstract Class setMarkerPackageName();

    public String getDataName() {
        return databaseName;
    }

    public int getDataVersion() {
        return databaseVersion;
    }

    public Class getMarkerPackageName() {
        return markerPackageName;
    }

}