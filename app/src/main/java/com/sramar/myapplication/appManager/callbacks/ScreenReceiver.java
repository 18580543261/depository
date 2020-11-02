package com.sramar.myapplication.appManager.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sramar.myapplication.appManager.AppManager;


//一像素activity的广播接受者
public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {    //屏幕关闭的时候接受到广播
            AppManager.getInstance().setScreen(false);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {   //屏幕打开的时候发送广播
            AppManager.getInstance().setScreen(true);
        }
    }
}