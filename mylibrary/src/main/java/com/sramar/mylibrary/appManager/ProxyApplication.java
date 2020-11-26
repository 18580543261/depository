package com.sramar.mylibrary.appManager;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ProxyApplication extends Application {

    protected String app_name;

    protected Application delegate;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.e("momo","ProxyApplication: attachBaseContext: "+this.getClass());
        getMetaData();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("momo","ProxyApplication: onCreate: "+this.getClass());
        try {
            bindRealApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        if (TextUtils.isEmpty(app_name)){
            return super.createPackageContext(packageName, flags);
        }
        try {
            bindRealApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delegate;
    }

    @Override
    public String getPackageName() {
        //如果meta-data 设置了 application
        //让 ContentProvider 创建的时候使用的上下文
        //在ActivityThread 中的 installProvider 函数命中else
        if (!TextUtils.isEmpty(app_name)){
            return "";
        }
        return super.getPackageName();
    }

    public void bindRealApplication() throws Exception {

        //为了避免多次进行反射创建
        if (delegate != null){
            return;
        }
        //如果用户没有配置 Application 就不用管了
        if (TextUtils.isEmpty(app_name)) {
            return;
        }

        //这个就是attachBaseContext传进来的 ContextImpl
        Context baseContext = getBaseContext();

        //反射创建出真实的用户配置的 Application
        Class<?> delegateClass = Class.forName(app_name);
        delegate = (Application) delegateClass.newInstance();
        //反射获得 attach 函数
        Method attach = Application.class.getDeclaredMethod("attach", Context.class);
        //设置允许访问
        attach.setAccessible(true);
        attach.invoke(delegate, baseContext);

        /**
         *  替换 ContextImpl 的成员变量 mOuterContext
         */
        Class<?> contextImplClass = Class.forName("android.app.ContextImpl");
        //获得 mOuterContext 属性
        Field mOuterContextField = contextImplClass.getDeclaredField("mOuterContext");
        mOuterContextField.setAccessible(true);
        mOuterContextField.set(baseContext, delegate);

        /**
         * 替换 ActivityThread 的成员变量 mInitialApplication 和 mAllApplications
         */
        //通过 ContextImpl 的成员变量 mMainThread 获得 ActivityThread 对象
        Field mMainThreadField = contextImplClass.getDeclaredField("mMainThread");
        mMainThreadField.setAccessible(true);
        Object mMainThread = mMainThreadField.get(baseContext);

        //替换 mInitialApplication
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field mInitialApplicationField = activityThreadClass.getDeclaredField
                ("mInitialApplication");
        mInitialApplicationField.setAccessible(true);
        mInitialApplicationField.set(mMainThread, delegate);

        //替换 mAllApplications
        Field mAllApplicationsField = activityThreadClass.getDeclaredField
                ("mAllApplications");
        mAllApplicationsField.setAccessible(true);
        ArrayList<Application> mAllApplications = (ArrayList<Application>) mAllApplicationsField.get(mMainThread);
        mAllApplications.remove(this);
        mAllApplications.add(delegate);

        /**
         * 替换 LoadedApk 的成员变量 mApplication
         */
        //通过 ContextImpl 的成员变量 mPackageInfo 获得 LoadedApk 对象
        Field mPackageInfoField = contextImplClass.getDeclaredField("mPackageInfo");
        mPackageInfoField.setAccessible(true);
        Object mPackageInfo = mPackageInfoField.get(baseContext);

        Class<?> loadedApkClass = Class.forName("android.app.LoadedApk");
        Field mApplicationField = loadedApkClass.getDeclaredField("mApplication");
        mApplicationField.setAccessible(true);
        mApplicationField.set(mPackageInfo, delegate);

        //修改 ApplicationInfo 的 className
        Field mApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo");
        mApplicationInfoField.setAccessible(true);
        ApplicationInfo mApplicationInfo = (ApplicationInfo) mApplicationInfoField.get(mPackageInfo);
        mApplicationInfo.className = app_name;

        delegate.onCreate();
    }

    public void getMetaData() {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo
                    (getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            //是否设置 app_name 与 app_version
            if (null != metaData) {
                //是否存在name为app_name的meta-data数据
                if (metaData.containsKey("app_name")) {
                    app_name = metaData.getString("app_name");
                }else {
                    app_name = BaseApplication.class.getName();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


}
