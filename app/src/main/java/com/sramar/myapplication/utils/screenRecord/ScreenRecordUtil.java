package com.sramar.myapplication.utils.screenRecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.Log;

import com.sramar.myapplication.baseApplication.BaseApplication;
import com.sramar.myapplication.baseApplication.Constances;
import com.sramar.myapplication.utils.ScreenUtil;

import java.io.IOException;

public class ScreenRecordUtil {
    MediaProjectionManager mediaProjectionManager;
    MediaRecorder mMediaRecorder;
    VirtualDisplay virtualDisplay;
    String mRecordFilePath;
    int requestCode;

    private static ScreenRecordUtil instance;
    private ScreenRecordUtil  (){}
    public static synchronized ScreenRecordUtil  getInstance() {
        if (instance == null) {
            instance = new ScreenRecordUtil ();
        }
        return instance;
    }

    public void permission(Activity activity,int requestCode){
        mediaProjectionManager = (MediaProjectionManager) activity.
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager != null){
            Intent intent = mediaProjectionManager.createScreenCaptureIntent();
            PackageManager packageManager = activity.getPackageManager();
            if (packageManager.resolveActivity(intent,PackageManager.MATCH_DEFAULT_ONLY) != null){
                //存在录屏授权的Activity
                activity.startActivityForResult(intent,requestCode);
            }else {

            }
        }

    }

    public void init(int resultCode,Intent data) {

        mRecordFilePath = Constances.screenRecords + System.currentTimeMillis() + ".mp4";
        int mRecordWidth = ScreenUtil.getDisplaySize().x;
        int mRecordHeight = ScreenUtil.getDisplaySize().y;
        int dpi = (int) ScreenUtil.getScreenDpi();
        Log.e("momo","width: "+mRecordWidth+", height: "+mRecordHeight+", dpi: "+dpi);
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            //设置音频来源
//            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置视频来源
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            //输出的录屏文件格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //录屏文件足迹
            mMediaRecorder.setOutputFile( mRecordFilePath );
            //音视频编码器
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //视频尺寸
            mMediaRecorder.setVideoSize(mRecordWidth, mRecordHeight);
            //比特率
            mMediaRecorder.setVideoEncodingBitRate((int) (mRecordWidth * mRecordHeight * 3.6));
            //视频帧率
            mMediaRecorder.setVideoFrameRate(20);

            mMediaRecorder.prepare();
            MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
            virtualDisplay = mediaProjection.createVirtualDisplay(
                    "MainScreen",
                    mRecordWidth,
                    mRecordHeight,
                    dpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mMediaRecorder.getSurface(),
                    null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mMediaRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void stop(OnScreenRecordListener listener){
        if (mMediaRecorder == null){
            listener.onSuccess(-1,"空");
            return;
        }

        try {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);

            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            if(virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }



        }catch (Exception e){
            e.printStackTrace();
        }
        listener.onSuccess(200,mRecordFilePath);
    }

    public interface OnScreenRecordListener{
        void onSuccess(int code, String path);
    }
}
