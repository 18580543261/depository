package com.sramar.mylibrary.appManager.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sramar.mylibrary.appManager.AppManager;
import com.sramar.mylibrary.appManager.BaseApplication;

import java.text.SimpleDateFormat;
import java.util.Date;



//网络监听
public class NetStatusReceiver extends BroadcastReceiver {

    Context context;
    private static long WIFI_TIME=0;
    private static long ETHERNET_TIME=0;
    private static long NONE_TIME=0;

    private static AppManager.NetStatus LAST_TYPE= AppManager.NetStatus.NETWORK_MOBILE;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            long time=getTime();
            Log.e("momo","NetStatusReceiver: onReceive: time: "+time);
            if(time!=WIFI_TIME&&time!=ETHERNET_TIME&&time!=NONE_TIME){
                final AppManager.NetStatus netWorkState=getNetWorkState(context);
                if((netWorkState== AppManager.NetStatus.NETWORK_MOBILE&&LAST_TYPE!=AppManager.NetStatus.NETWORK_MOBILE) || (netWorkState== AppManager.NetStatus.NETWORK_WIFI&&LAST_TYPE!=AppManager.NetStatus.NETWORK_WIFI)){

                    if (netWorkState==AppManager.NetStatus.NETWORK_WIFI&&LAST_TYPE!=AppManager.NetStatus.NETWORK_WIFI){
                        WIFI_TIME=time;
                        LAST_TYPE=netWorkState;
                        BaseApplication.getAppManager().setNetStatus(AppManager.NetStatus.NETWORK_WIFI);
                    }else {
                        ETHERNET_TIME=time;
                        LAST_TYPE=netWorkState;
                        BaseApplication.getAppManager().setNetStatus(AppManager.NetStatus.NETWORK_MOBILE);
                    }
                }else if(netWorkState== AppManager.NetStatus.NETWORK_NONE&&LAST_TYPE!=AppManager.NetStatus.NETWORK_NONE){
                    NONE_TIME=time;
                    LAST_TYPE=netWorkState;
                    BaseApplication.getAppManager().setNetStatus(AppManager.NetStatus.NETWORK_NONE);
                }
            }
        }
    }
    public long getTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String    date    =    sDateFormat.format(new Date().getTime());
        return Long.valueOf(date);
    }



    public static AppManager.NetStatus getNetWorkState(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null&&activeNetworkInfo.isConnected()){
            if(activeNetworkInfo.getType()==(ConnectivityManager.TYPE_WIFI)){
                return AppManager.NetStatus.NETWORK_WIFI;
            }else if(activeNetworkInfo.getType()==(ConnectivityManager.TYPE_MOBILE)){
                return AppManager.NetStatus.NETWORK_MOBILE;
            }
        }else{
            return AppManager.NetStatus.NETWORK_NONE;
        }
        return AppManager.NetStatus.NETWORK_NONE;
    }

}
