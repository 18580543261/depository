package com.sramar.myapplication.appManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;


import com.sramar.myapplication.baseApplication.Constances;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


//网络监听
public class NetStatusReceiver extends BroadcastReceiver {

    Context context;
    private static long WIFI_TIME=0;
    private static long ETHERNET_TIME=0;
    private static long NONE_TIME=0;

    private static int LAST_TYPE=-3;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            long time=getTime();
            Log.e("momo","NetStatusReceiver: onReceive: time: "+time);
            if(time!=WIFI_TIME&&time!=ETHERNET_TIME&&time!=NONE_TIME){
                final int netWorkState=getNetWorkState(context);
                if((netWorkState==0&&LAST_TYPE!=0) || (netWorkState==1&&LAST_TYPE!=1)){

                    if (netWorkState==0&&LAST_TYPE!=0){
                        WIFI_TIME=time;
                        LAST_TYPE=netWorkState;
                        Constances.isNetWork = true;
                    }else {
                        ETHERNET_TIME=time;
                        LAST_TYPE=netWorkState;
                        Constances.isNetWork = true;
                    }
                }else if(netWorkState==-1&&LAST_TYPE!=-1){
                    NONE_TIME=time;
                    Toast.makeText(context, "网络状态不可用", Toast.LENGTH_SHORT).show();
                    LAST_TYPE=netWorkState;
                    Constances.isNetWork = false;
                }
            }
        }
    }
    public long getTime(){
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String    date    =    sDateFormat.format(new Date().getTime());
        return Long.valueOf(date);
    }

    private static final int NETWORK_NONE=-1; //无网络连接
    private static final int NETWORK_WIFI=0; //wifi
    private static final int NETWORK_MOBILE=1; //数据网络

    public static int getNetWorkState(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null&&activeNetworkInfo.isConnected()){
            if(activeNetworkInfo.getType()==(ConnectivityManager.TYPE_WIFI)){
                return NETWORK_WIFI;
            }else if(activeNetworkInfo.getType()==(ConnectivityManager.TYPE_MOBILE)){
                return NETWORK_MOBILE;
            }
        }else{
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

}
