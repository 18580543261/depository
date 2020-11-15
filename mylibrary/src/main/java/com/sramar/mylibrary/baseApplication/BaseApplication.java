package com.sramar.mylibrary.baseApplication;

import android.app.Application;
import android.content.Context;

import com.sramar.mylibrary.appManager.AppManager;

public class BaseApplication extends Application {
    private static Context context;
    private AppManager appManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        appManager = AppManager.getInstance().registAppManager(this);
    }

    public static Context getContext(){
        return context;
    }
}