package com.sramar.myapplication.modules;

import android.content.DialogInterface;
import android.os.Bundle;

import com.sramar.myapplication.R;
import com.sramar.mylibrary.appManager.BaseActivity;
import com.sramar.mylibrary.defindViews.Dialog.MDialogUpdate;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MDialogUpdate dialogUpdate = new MDialogUpdate(activity,
                "1.0.4", "http://acj6.0098118.com/pc6_soure/2020-10-6/d071f46de1fbbfdj7wl1EuteNuKarU.apk", "1.修复漏洞2", "momo", "155", false,
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
        dialogUpdate.show();
    }


}
