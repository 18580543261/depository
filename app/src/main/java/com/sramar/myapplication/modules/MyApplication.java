package com.sramar.myapplication.modules;

import com.sramar.myapplication.database.bean.A;
import com.sramar.mylibrary.appManager.AppManager;
import com.sramar.mylibrary.appManager.BaseApplication;
import com.sramar.mylibrary.appManager.Constants;
import com.sramar.mylibrary.appManager.DatabaseManager;

public class MyApplication extends BaseApplication {

    @Override
    protected Constants registConstants() {
        return super.registConstants();
    }

    @Override
    protected AppManager registAppManager() {
        return super.registAppManager();
    }

    @Override
    protected Class registDatabaseHelper() {
        super.registDatabaseHelper();
        return MyHelperI.class;
    }
}
