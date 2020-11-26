package com.sramar.mylibrary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sramar.mylibrary.database.bean.ExampelBean;

public class aImpl extends IDatabaseHelper{
    private static String databaseName = "my_db_name";
    private static int databaseVersion = 1;
    public aImpl(Context context) {
        super(context,databaseName,null,databaseVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newVersion) {

    }

    @Override
    public Class setMarkerPackageName() {
        return IBeans.class;
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
