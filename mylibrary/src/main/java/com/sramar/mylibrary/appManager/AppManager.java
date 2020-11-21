package com.sramar.mylibrary.appManager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.sramar.mylibrary.appManager.callbacks.ForegroundCallback;
import com.sramar.mylibrary.appManager.callbacks.NetStatusReceiver;
import com.sramar.mylibrary.appManager.callbacks.ScreenReceiver;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class AppManager {
    private NetStatus netStatus = NetStatus.NETWORK_MOBILE;
    private boolean isInFore = true;
    private boolean isScreen = true;

    private BroadcastReceiver screenReceiver;
    private BroadcastReceiver netStatusReceiver;
    private Application application;

    private List<OnForegChange> foreListener ;
    private List<OnScreenChange> screListener ;
    private List<OnNetWorkChange> netwListener;

    public AppManager(Application application){
        this.application = application;
        foreListener = new CopyOnWriteArrayList<>();
        screListener = new CopyOnWriteArrayList<>();
        netwListener = new CopyOnWriteArrayList<>();
    }

    private void registScreenManager(){
        if (screenReceiver != null)
            return;
        screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        application.registerReceiver(screenReceiver, intentFilter);
    }
    private void unRegistScreenManager(){
        if (screenReceiver != null){
            application.unregisterReceiver(screenReceiver);
        }
    }
    private void registNetStateManager(){
        if (netStatusReceiver != null)
            return;
        netStatusReceiver = new NetStatusReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(netStatusReceiver,intentFilter);
    }
    private void unRegistNetStateManager(){
        if (netStatusReceiver != null){
            application.unregisterReceiver(netStatusReceiver);
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

    AppManager registAppManager(){
        registScreenManager();
        registNetStateManager();
        registForegManager(application);
        return this;
    }
    public void unregistAppManager(){
        unRegistScreenManager();
        unRegistNetStateManager();
    }

    public NetStatus getNetStatus() {
        return NetStatusReceiver.getNetWorkState(application);
    }

    public void setInFore(boolean isInFore){
        this.isInFore = isInFore;
        Iterator<OnForegChange> it = foreListener.iterator();
        while (it.hasNext()){
            OnForegChange li = it.next();
            if (li != null){
                li.foregStatus(isInFore);
            }else {
                it.remove();
            }
        }

    }
    public void setScreen(boolean isScreen){
        this.isScreen = isScreen;
        Iterator<OnScreenChange> it = screListener.iterator();
        while (it.hasNext()){
            OnScreenChange li = it.next();
            if (li != null){
                li.screenStatus(isScreen);
            }else {
                it.remove();
            }
        }
    }
    public void setNetStatus(NetStatus netStatus) {
        this.netStatus = netStatus;
        Iterator<OnNetWorkChange> it = netwListener.iterator();
        while (it.hasNext()){
            OnNetWorkChange li = it.next();
            if (li != null){
                li.netStatus(netStatus);
            }else {
                it.remove();
            }
        }
    }

    public void registListener(OnAppStatusChangeListener listener){
        if (listener == null){
            Log.e("momo","AppManager: registListener: listener为空");
            return;
        }
        if (listener instanceof OnForegChange){
            foreListener.add((OnForegChange) listener);
        }
        if (listener instanceof OnScreenChange){
            screListener.add((OnScreenChange) listener);
        }
        if (listener instanceof OnNetWorkChange){
            netwListener.add((OnNetWorkChange) listener);
        }
    }
    public void unRegistListener(OnAppStatusChangeListener listener){
        if (listener == null){
            Log.e("momo","AppManager: registListener: listener为空");
            return;
        }
        if (listener instanceof OnForegChange){
            foreListener.remove(listener);
        }
        if (listener instanceof OnScreenChange){
            screListener.remove(listener);
        }
        if (listener instanceof OnNetWorkChange){
            netwListener.remove(listener);
        }
    }

    private interface  OnAppStatusChangeListener{

    }
    public interface OnNetWorkChange extends OnAppStatusChangeListener{
        void netStatus(NetStatus isNetWork);
    }
    public interface OnScreenChange extends OnAppStatusChangeListener{
        void screenStatus(boolean isScreenOn);
    }
    public interface OnForegChange extends OnAppStatusChangeListener{
        void foregStatus(boolean isInFore);
    }

    public enum NetStatus{
        NETWORK_NONE,
        NETWORK_WIFI,
        NETWORK_MOBILE;
    }

}
