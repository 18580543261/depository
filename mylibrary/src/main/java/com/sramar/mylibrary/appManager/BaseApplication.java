package com.sramar.mylibrary.appManager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.Log;

import com.sramar.mylibrary.database.aImpl;


public class BaseApplication extends Application {
    private static Context context;
    private static BaseApplication instance;

    private Constants constants;
    private AppManager appManager;
    private DatabaseManager databaseManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("momo","BaseApplication: onCreate: "+this.getClass());
        context = getApplicationContext();
        instance = this;

        appManager = registAppManager().registAppManager();
        constants = registConstants();
        DatabaseManager databaseManager = DatabaseManager.getInstance().changeData(aImpl.class);
        databaseManager.openDatabase();
        databaseManager.closeDatabase();
        databaseManager.changeData(registDatabaseHelper());
        databaseManager.openDatabase();
        databaseManager.closeDatabase();
        this.databaseManager = databaseManager;


    }

    public static Context getContext(){
        return context;
    }
    public static BaseApplication getInstance() {
        return instance;
    }

    public AppManager getAppManager() {
        return appManager;
    }
    public Constants getConstants() {
        return constants;
    }
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @CallSuper
    protected Constants registConstants(){
        return new Constants();
    }
    @CallSuper
    protected AppManager registAppManager(){
        return new AppManager(this);
    }
    @CallSuper
    protected Class registDatabaseHelper(){
        //自己继承DatabaseHelper，并且设置单参数构造函数
        //其中，setMarkerPackageName的参数，是一个随意的java类，用以标志其包下bean的包名，xxx.xxx.xxx.bean包中，装载着继承了ABean的class

        return aImpl.class;
    }
}
