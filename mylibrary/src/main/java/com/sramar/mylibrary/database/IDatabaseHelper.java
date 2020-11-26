package com.sramar.mylibrary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

/*
**需要继承，并且设置单参数构造函数
 */
public abstract class IDatabaseHelper extends SQLiteOpenHelper {

    private  String databaseName = "db_name";
    private  int databaseVersion = 1;
    private Class markerPackageName = IBeans.class;

    public IDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        markerPackageName = setMarkerPackageName();
        databaseName = setDatabaseName();
        databaseVersion = setDatabaseVersion();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("UseDatabase", "创建数据库: "+databaseName+", 版本: "+databaseVersion);
    }

    @Override
    public abstract void onUpgrade(SQLiteDatabase db, int old, int newVersion);


    public abstract Class setMarkerPackageName();
    public abstract String setDatabaseName();
    public abstract int setDatabaseVersion();

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