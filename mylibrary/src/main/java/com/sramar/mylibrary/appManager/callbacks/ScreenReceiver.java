package com.sramar.mylibrary.appManager.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sramar.mylibrary.appManager.AppManager;
import com.sramar.mylibrary.appManager.BaseApplication;


//一像素activity的广播接受者
public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {    //屏幕关闭的时候接受到广播
            BaseApplication.getAppManager().setScreen(false);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {   //屏幕打开的时候发送广播
            BaseApplication.getAppManager().setScreen(true);
        }
    }
}