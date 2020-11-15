package com.sramar.myapplication.permissionManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PermissionManager {
    PermissionManager instance;
    Activity activity;
    static AtomicInteger reqCode = new AtomicInteger();
    static AtomicInteger resCode = new AtomicInteger();

    private PermissionManager(Activity activity){
        this.activity = activity;
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









    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try{
            if (Build.VERSION.SDK_INT >= 23 && activity.getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try{
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && activity.getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try{
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try{
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
//                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

//    /**
//     * 显示提示信息
//     *
//     * @since 2.5.0
//     */
//    private void showMissingPermissionDialog() {
//        try{
//            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
//            builder.setTitle("提示");
//            builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限");
//
////            // 拒绝, 退出应用
////            builder.setNegativeButton("取消",
////                    new DialogInterface.OnClickListener() {
////                        @Override
////                        protected void onSingleClick(DialogInterface dialog, int which) {
////                            try{
////                                finish();
////                            } catch (Throwable e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    });
////
////            builder.setPositiveButton("设置",
////                    new DialogInterface.OnClickListener() {
////                        @Override
////                        protected void onSingleClick(DialogInterface dialog, int which) {
////                            try {
////                                startAppSettings();
////                            } catch (Throwable e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    });
//
//            builder.setCancelable(false);
//
//            builder.show();
//        }catch(Throwable e){
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 启动应用的设置
//     *
//     * @since 2.5.0
//     */
//    private void startAppSettings() {
//        try{
//            Intent intent = new Intent(
//                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.parse("package:" + getPackageName()));
//            startActivity(intent);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
}
