package com.sramar.myapplication.database;

import android.database.sqlite.SQLiteDatabase;

import com.sramar.myapplication.baseApplication.BaseApplication;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private static DatabaseManager dbOpenManager;
    private SQLiteDatabase database;
    private AtomicInteger atomicInteger;
    //用AtomicInteger来解决数据表异步操作的问题
    private DatabaseHelper dbHelper;

    //私有化构造器
    private DatabaseManager() {
        if (dbOpenManager != null){
            return;
        }
        initData();
    }

    //初始化基本数据
    private void initData() {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(BaseApplication.getContext());
        }
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger();
        }
    }

    //单例模式获取操作类对象(懒汉式)
    public static DatabaseManager getInstance() {
        if (dbOpenManager == null) {
            synchronized (DatabaseManager.class) {
                if (dbOpenManager == null) {
                    dbOpenManager = new DatabaseManager();
                }
            }
        }
        return dbOpenManager;
    }

    //打开数据库. 返回数据库操作对象
    public synchronized SQLiteDatabase openDatabase() {
        initData();
        //查看当前 AtomicInteger 中的 value 值
        if (atomicInteger.incrementAndGet() == 1) {
            try { //获取一个可读可写的数据库操作对象
                database = dbHelper.getWritableDatabase();
                dbHelper.getWritableDatabase();
            } catch (Exception e) {
                atomicInteger.set(0);
                e.printStackTrace();
            }
        }
        return database;
    }

    //关闭数据库
    public synchronized void closeDatabase() {
        //查看当前 AtomicInteger 中的 value 值
        if (atomicInteger.decrementAndGet() <= 0) {//避免关闭多次后数据库产生异常
            atomicInteger.set(0);
            try {
                if (database != null) {
                    database.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            database = null;
        }
    }

}