package com.sramar.mylibrary.utils.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import com.sramar.mylibrary.baseApplication.BaseApplication;


public class NotificationUtil {

    private static NotificationUtil instance;
    private NotificationUtil(){}
    public static synchronized NotificationUtil getInstance() {
        if (instance == null) {
            instance = new NotificationUtil();
        }
        return instance;
    }

    Context context;
    //android 8.0后台定位权限
    public String NOTIFICATION_LOCATION_CHANNEL_NAME = "BackgroundLocation";
    public String NOTIFICATION_LOCATION_CHANNEL_ID = "2001";
    public int CHANNEL_ID = 2001;
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;

    public NotificationUtil init(Context context){
        this.context = context;
        NOTIFICATION_LOCATION_CHANNEL_ID = context.getPackageName();
        return instance;
    }
    public Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_LOCATION_CHANNEL_ID,
                        NOTIFICATION_LOCATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(BaseApplication.getContext().getApplicationContext(), NOTIFICATION_LOCATION_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(BaseApplication.getContext().getApplicationContext());
        }
        //构造Intent对象,自定义
//        Intent intent = new Intent(context, MainActivity.class);
//        //获得PendingIntent对象
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        builder.setSmallIcon(R.mipmap.logo)
//                .setContentTitle("小圈圈")
//                .setContentText("正在运行")
//                .setSound(null)
//                .setContentIntent(pendingIntent)
//                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }
}
