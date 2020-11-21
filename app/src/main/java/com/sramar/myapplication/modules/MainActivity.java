package com.sramar.myapplication.modules;

import android.os.Bundle;
import android.util.Log;

import com.sramar.myapplication.R;
import com.sramar.mylibrary.appManager.BaseActivity;
import com.sramar.mylibrary.appManager.BaseApplication;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("momo","MainActivity: onCreate: "+ BaseApplication.getInstance().getConstants().getServerHttpAddress());

    }


}
