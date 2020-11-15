package com.sramar.myapplication.modules;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sramar.myapplication.R;
import com.sramar.myapplication.baseApplication.BaseActivity;
import com.sramar.myapplication.defindViews.Dialog.MDialogUpdate;
import com.sramar.myapplication.utils.listener.OnSingleClickListener;

import java.util.Date;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MDialogUpdate dialogUpdate = new MDialogUpdate(activity,
                "1.0.3", "http://acj6.0098118.com/pc6_soure/2020-10-6/d071f46de1fbbfdj7wl1EuteNuKarU.apk", "1.修复漏洞2", "momo", "111", false,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
        dialogUpdate.show();
    }


}
