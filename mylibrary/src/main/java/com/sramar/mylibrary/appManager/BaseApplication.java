package com.sramar.mylibrary.appManager;

import android.app.Application;
import android.content.Context;


public class BaseApplication extends Application {
    private static Context context;
    public static AppManager appManager;
    public static Constance constance;
    public static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;

        appManager = AppManager.getInstance().registAppManager(this);
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
}
