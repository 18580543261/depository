package com.sramar.mylibrary.appManager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.sramar.mylibrary.appManager.callbacks.ForegroundCallback;
import com.sramar.mylibrary.appManager.callbacks.NetStatusReceiver;
import com.sramar.mylibrary.appManager.callbacks.ScreenReceiver;
import com.sramar.mylibrary.baseApplication.BaseApplication;

public class AppManager {
    private boolean isNetWork = true;
    private boolean isInFore = true;
    private boolean isScreen = true;


    private BroadcastReceiver screenReceiver;
    private BroadcastReceiver netStatusReceiver;
    private static Context context;

    private static AppManager instance;

    private AppManager(){
        if (instance != null){
            return;
        }
    }

    public synchronized static AppManager getInstance(){
        if (instance == null){
            synchronized (AppManager.class){
                if (instance == null){
                    context = BaseApplication.getContext();
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    private void registScreenManager(){
        screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        context.registerReceiver(screenReceiver, intentFilter);
    }
    private void unRegistScreenManager(){
        if (screenReceiver != null){
            context.unregisterReceiver(screenReceiver);
        }
    }
    private void registNetStateManager(){
        netStatusReceiver = new NetStatusReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(netStatusReceiver,intentFilter);
    }
    private void unRegistNetStateManager(){
        if (netStatusReceiver != null){
            context.unregisterReceiver(netStatusReceiver);
        }
    }
    private void registForegManager(Application application){
        ForegroundCallback.init(application).addListener(new ForegroundCallback.Listener() {
            @Override
            public void onBecameForeground() {
                setInFore(true);
            }

            @Override
            public void onBecameBackground() {
                setInFore(false);
            }
        });
    }

    public AppManager registAppManager(Application application){
        registScreenManager();
        registNetStateManager();
        registForegManager(application);
        return instance;
    }
    public void unregistAppManager(){
        unRegistScreenManager();
        unRegistNetStateManager();
    }

    public void setNetStatus(boolean isNetWork){
        this.isNetWork = isNetWork;
    }
    public void setInFore(boolean isInFore){
        this.isInFore = isInFore;
    }
    public void setScreen(boolean isScreen){
        this.isScreen = isScreen;
    }

    public interface onNetWorkChange{
        void netStatus(boolean isNetWork);
    }
    public interface onScreenChange{
        void screenStatus(boolean isScreenOn);
    }
    public interface onForegChange{
        void foregStatus(boolean isInFore);
    }


}
