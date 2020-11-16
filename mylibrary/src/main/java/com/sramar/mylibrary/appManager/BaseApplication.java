package com.sramar.mylibrary.appManager;

import android.app.Application;
import android.content.Context;

import com.sramar.mylibrary.database.ABeans;
import com.sramar.mylibrary.database.DatabaseHelper;


public class BaseApplication extends Application {
    private static Context context;
    public static AppManager appManager;
    public static DatabaseManager databaseManager;
    public static Constance constance;
    public static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;

        appManager = AppManager.getInstance().registAppManager(this);
        //自己继承DatabaseHelper，调用setDataName();setDataVersion();setMarkerPackageName();
        //其中，setMarkerPackageName的参数，是一个随意的java类，用以标志其包下bean的包名，xxx.xxx.xxx.bean包中，装载着继承了ABean的class
        databaseManager = DatabaseManager.getInstance().changeData(DatabaseHelper.class);
        constance = Constance.getInstance();
    }

    public static Context getContext(){
        return context;
    }
    public static BaseApplication getInstance() {
        return instance;
    }

    public static AppManager getAppManager() {
        return appManager;
    }

    public static Constance getConstance() {
        return constance;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
