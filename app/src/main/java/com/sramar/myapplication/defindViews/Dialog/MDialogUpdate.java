package com.sramar.myapplication.defindViews.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.sramar.myapplication.BuildConfig;
import com.sramar.myapplication.R;
import com.sramar.myapplication.utils.fileUtils.FileDeleteUtil;
import com.sramar.myapplication.utils.fileUtils.FileRenameUtil;
import com.sramar.myapplication.utils.listener.OnSingleClickListener;
import com.sramar.myapplication.utils.netRequest.DownloadRunnable;
import com.sramar.myapplication.utils.netRequest.TaskInfo;

import java.io.File;
import java.util.Date;

//<provider
//            android:name="android.support.v4.content.FileProvider"
//                    android:authorities="${applicationId}.FileProvider"
//                    android:exported="false"
//                    android:grantUriPermissions="true">
//<meta-data
//        android:name="android.support.FILE_PROVIDER_PATHS"
//        android:resource="@xml/rc_file_path" />
//</provider>

//<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//<uses-permission android:name="android.permission.READ_PHONE_STATE" />
//<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
//<uses-permission android:name="android.permission.INTERNET" />

public class MDialogUpdate extends Dialog {
    Activity activity;
    String updateFileDir ;
    String apkName = "";

    boolean isCompel;
    String updateVersion;
    String updateLog;

    OnCancelListener cancelListener;
    OnSingleClickListener updateListener;
    CompoundButton.OnCheckedChangeListener tipListener;

    SharedPreferences sp;
    SharedPreferences.Editor edit;

    Button btn_update,btn_install;

    private MDialogUpdate(@NonNull Activity activity) {
        super(activity);

        sp =activity.getSharedPreferences("app_version", activity.MODE_PRIVATE);
        edit = sp.edit();

        this.activity = activity;
        updateFileDir = activity.getExternalFilesDir(null) + "/newVersion/";
        apkName = "circle_"+sp.getString("version","1.0.0")+"_"+sp.getString("updateTime",new Date().getTime()+"")+".apk";
        File file = new File(updateFileDir);
        if (!file.exists()){
            file.mkdirs();
        }
    }

    public MDialogUpdate(@NonNull Activity activity,String version, String url, String updateLog, String updateBy, String updateTime,boolean is_Compel_update,OnCancelListener singleClickListener){
        this(activity);
        this.saveVersion(version, url, updateLog, updateBy, updateTime, is_Compel_update);
        this.cancelListener = singleClickListener;
    }

    @Override
    public void show() {
        if (isUpdated()){
            return;
        }

        setCompel(sp.getBoolean("isCompel",false));
        setUpdateVersion("发现新版本\nV"+sp.getString("version","1.0.0"));
        setUpdateLog(sp.getString("updateLog","1.修复问题"));
        if (!sp.getBoolean("isNextTip",true)){
            if (new Date().getTime() < sp.getLong("nextTipTime",new Date().getTime())){
                Log.e("momo","MDialogUpdate: setContent: 不再显示");
                setControl();
                cancel();
                return;
            }
        }
        super.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //指定布局
        setContentView(R.layout.m_dialog_upadte);


        setWidth();
        setBackgroundTransparent();
        setControl();
        setContent();

    }

    private void setWidth(){
        //获取窗口
        Window window = this.getWindow();

        WindowManager w = activity.getWindowManager();
        //获取屏幕宽、高
        Display d = w.getDefaultDisplay();
        //获取当前对话框参数值
        WindowManager.LayoutParams p = window.getAttributes();
        //设置对话框宽度为屏幕0.8
        //p.height = (int)(d.getHeight()*0.8);
        p.width = (int)(d.getWidth()*0.7);
        window.setAttributes(p);
    }

    private void setBackgroundTransparent(){
        //获取窗口
        Window window = this.getWindow();
        //背景透明
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void setContent(){
        LinearLayout ly_cancel = findViewById(R.id.cancel);
        LinearLayout ly_compel_true = findViewById(R.id.update_compel_true);
        LinearLayout ly_compel_false = findViewById(R.id.update_compel_false);
        TextView tv_upload_version = findViewById(R.id.update_version);
        TextView tv_upload_log = findViewById(R.id.update_log);
        CheckBox cb_update_tip = findViewById(R.id.update_compel_false_tip);
        btn_update = findViewById(R.id.update_button);
        btn_install = findViewById(R.id.install_button);
        btn_install.setVisibility(View.GONE);
        final ProgressBar update_progress_bar = findViewById(R.id.update_progress_bar);
        update_progress_bar.setVisibility(View.GONE);

        ly_compel_true.setVisibility(isCompel?View.VISIBLE:View.GONE);
        ly_compel_false.setVisibility(isCompel?View.GONE:View.VISIBLE);
        ly_cancel.setVisibility(isCompel?View.GONE:View.VISIBLE);
        tv_upload_log.setText(updateLog);
        tv_upload_version.setText(updateVersion);

        checkVersion();

        if (updateListener == null){
            updateListener = new OnSingleClickListener() {
                @Override
                protected void onSingleClick(View view) {
                    btn_update.setVisibility(View.GONE);
                    update_progress_bar.setVisibility(View.VISIBLE);
                    final TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setNameTmp("apk.tmp");
                    taskInfo.setName(apkName);
                    taskInfo.setDir(updateFileDir);
                    taskInfo.setUrl(sp.getString("url", "http://baid.con"));
                    taskInfo.setProgressListener(new TaskInfo.OnProgressListener() {
                        @Override
                        public void onProgress(final long total, final long completed) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    update_progress_bar.setProgress((int)(100*completed/total));
                                    if (total == completed){
                                        btn_install.setVisibility(View.VISIBLE);
                                        update_progress_bar.setVisibility(View.GONE);
                                        FileRenameUtil.renameFile(taskInfo.getPathTmp(),taskInfo.getPath());
                                        edit.putString("apkPath",taskInfo.getPath());
                                        edit.commit();
                                    }
                                }
                            });
                        }
                    });
                    DownloadRunnable runnable = new DownloadRunnable(taskInfo);
                    new Thread(runnable).start();
                }
            };
        }

        btn_update.setOnClickListener(updateListener);
        btn_install.setOnClickListener(new OnSingleClickListener() {
            @Override
            protected void onSingleClick(View view) {
                String apkPath = sp.getString("apkPath",updateFileDir+"demo.apk");
                installApk(activity,apkPath);
            }
        });
        ly_cancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            protected void onSingleClick(View view) {
                dismiss();
            }
        });
        cb_update_tip.setOnCheckedChangeListener(tipListener);
    }

    private void setControl(){
        if (cancelListener == null){
            cancelListener = new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Log.e("momo","MDialogUpdate: onCancel: ");
                }
            };
        }
        if (tipListener == null){
            tipListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e("momo","MDialogUpdate: onCheckedChanged: 没有设置复选框事件，isChecked："+isChecked);
                    setIsNextTip(!isChecked);
                }
            };
        }
        setCanceledOnTouchOutside(false);
        if (isCompel){
            setCancelable(false);
            this.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        cancel();
                        activity.finish();
                        return true;
                    }
                    return false;
                }
            });

        }else {
            setOnCancelListener(cancelListener);
        }

    }

    private void setCompel(boolean isCompel){
        this.isCompel = isCompel;
    }

    private void setUpdateVersion(String version){
        this.updateVersion = version;
    }

    private void setUpdateLog(String content){
        this.updateLog = content;
    }

    private void setCancelListener(OnCancelListener singleClickListener){
        this.cancelListener = singleClickListener;

    }

    private void setUpdateListener(OnSingleClickListener singleClickListener){
        this.updateListener = singleClickListener;
    }


    private boolean shouldSaveVersion(String newVersion,String newTime){
        String version = sp.getString("version", BuildConfig.VERSION_NAME);
        String updateTime = sp.getString("updateTime", "");

        if (newVersion == null)
            return false;

        if (version.equals(newVersion)){
            return !newTime.equals(updateTime);
        }else {
            return true;
        }
    }
    public void saveVersion(String version, String url, String updateLog, String updateBy, String updateTime,boolean is_Compel_update){
        if (!shouldSaveVersion(version,updateTime))
            return;
        edit.putString("version", version);
        edit.putString("url", url);
        edit.putString("updateLog", updateLog);
        edit.putString("updateBy", updateBy);
        edit.putString("updateTime", updateTime);
        edit.putBoolean("isCompel", is_Compel_update);
        edit.commit();

        apkName = "circle_"+sp.getString("version","1.0.0")+"_"+sp.getString("updateTime",new Date().getTime()+"")+".apk";
    }
    private void setIsNextTip(boolean isNextTip){

        edit.putBoolean("isNextTip", isNextTip);
        edit.putLong("nextTipTime", new Date().getTime() + 7*24*60*60*1000);
        edit.commit();
    }
    public void checkVersion(){
        if (btn_update == null || btn_install == null)
            return;
        if (isApkRepared()){
            btn_install.setVisibility(View.VISIBLE);
            btn_update.setVisibility(View.GONE);
        }else {
            btn_install.setVisibility(View.GONE);
            btn_update.setVisibility(View.VISIBLE);
        }
    }
    public boolean isApkRepared() {
        String apkPath = updateFileDir+apkName;
        Log.e("momo","MDialogUpdate: isApkRepared: "+apkPath);
        File file = new File(apkPath);

        return file.exists();
    }
    private boolean isUpdated(){
        String newVersion = sp.getString("version",BuildConfig.VERSION_NAME);
        String oldVersion = BuildConfig.VERSION_NAME;
        String[] urln = newVersion.split("\\.");
        String[] urlo = oldVersion.split("\\.");
        if (urln.length > urlo.length)return false;
        for (int i = 0; i < urln.length; i++) {
            if (Integer.valueOf(urln[i]) > Integer.valueOf(urlo[i])) {
                return false;
            }
        }
        FileDeleteUtil.DeleteFolder(updateFileDir);
        return true;
    }

    /**
     * 安装APK文件
     */
    public void installApk(Context context, String fileName) {
        File apkfile = new File(fileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider", apkfile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

}