package com.sramar.myapplication.appManager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class PermissionManager {
    static PermissionManager instance;
    Activity activity;
    static AtomicInteger reqCode = new AtomicInteger();
    static AtomicInteger resCode = new AtomicInteger();

    private PermissionManager(){}
    public static synchronized PermissionManager getInstance() {
        if (instance == null) {
            instance = new PermissionManager();
        }
        return instance;

    }

    public synchronized void init(Activity activity){
        if (activity == null){
            Log.e("momo","PermissionManager: init: activity为空:");
            return;
        }
        this.activity = activity;
    }

    public boolean isPermited(PermissionType permissionType){
        if (activity == null){
            Log.e("momo","PermissionManager: isPermited: activity为空:");
            return false;
        }
        boolean isGrant = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            isGrant =  ActivityCompat.checkSelfPermission(activity, permissionType.permission[0]) == PackageManager.PERMISSION_GRANTED;
        }
        return isGrant;
    }

    public boolean reqPermission (PermissionType permissionType){
        if (activity == null){
            Log.e("momo","PermissionManager: reqPermission: activity为空:");
            return false;
        }
        Log.e("momo","开始申请权限："+permissionType.permission[0]);
        boolean isGrant = isPermited(permissionType);
        if (!isGrant)ActivityCompat.requestPermissions(activity, permissionType.permission, permissionType.requestCode);
        return isGrant;
    }
    public void reqPermission (PermissionType permissionType,StatusListener listener){
        if (reqPermission(permissionType))
            listener.onPermitted();
    }
    public void reqPermissions(PermissionType[] permissionTypes){
        for (PermissionType permissionType:permissionTypes) {
            reqPermission(permissionType);
        }
    }

    public interface StatusListener{
        void onPermitted();
    }

    public enum PermissionType{
        //临时申请
        CALL_PHONE(getReqCode(), getResCode(),new String[]{Manifest.permission.CALL_PHONE}),
        RECORD_AUDIO(getReqCode(), getResCode(),new String[]{Manifest.permission.RECORD_AUDIO}),
        CAMERA(getReqCode(), getResCode(),new String[]{Manifest.permission.CAMERA}),

        //必须要的
        READ_PHONE_STATE(getReqCode(), getResCode(),new String[]{Manifest.permission.READ_PHONE_STATE}),
        WRITE_EXTERNAL_STORAGE(getReqCode(), getResCode(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}),
        READ_EXTERNAL_STORAGE(getReqCode(), getResCode(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}),
        ACCESS_COARSE_LOCATION(getReqCode(), getResCode(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}),
        ACCESS_FINE_LOCATION(getReqCode(), getResCode(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}),
        ;
        final int requestCode;
        final int resultCode;
        final String[] permission;
        PermissionType(int requestCode, int resultCode, String[] permission){
            this.permission = permission;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }

        public String[] getPermission() {
            return permission;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public static PermissionType getPermissionType(int requestCode){
            for (PermissionType permissionType: PermissionType.values()){
                if (permissionType.requestCode == requestCode){
                    return permissionType;
                }
            }
            return null;
        }
    }
    protected static int getReqCode(){
        return reqCode.incrementAndGet();
    }
    protected static int getResCode(){
        return resCode.incrementAndGet();
    }
}
