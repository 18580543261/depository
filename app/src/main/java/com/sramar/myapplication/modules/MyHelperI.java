package com.sramar.myapplication.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.sramar.myapplication.database.Marker;
import com.sramar.mylibrary.database.IDatabaseHelper;

public class MyHelperI extends IDatabaseHelper {

    private static String databaseName = "m_db_name";
    private static int databaseVersion = 1;

    public MyHelperI(Context context) {
        super(context,databaseName,null,databaseVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newVersion) {

    }

    @Override
    public Class setMarkerPackageName() {
        return Marker.class;
    }

    @Override
    public String setDatabaseName() {
        return databaseName;
    }

    @Override
    public int setDatabaseVersion() {
        return databaseVersion;
    }
}
