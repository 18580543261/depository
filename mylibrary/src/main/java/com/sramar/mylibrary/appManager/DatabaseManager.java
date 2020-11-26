package com.sramar.mylibrary.appManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sramar.mylibrary.database.IBeans;
import com.sramar.mylibrary.database.CreateTable;
import com.sramar.mylibrary.database.IDatabaseHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {
    private static DatabaseManager dbOpenManager;
    private SQLiteDatabase database;
    private AtomicInteger atomicInteger;
    //用AtomicInteger来解决数据表异步操作的问题
    private IDatabaseHelper dbHelper;
    private HashMap<String, IDatabaseHelper> dbHelpers = new HashMap<>();
    private HashMap<String, CreateTable> dbTables = new HashMap<>();

    //私有化构造器
    private DatabaseManager() {
        //防止反射实例化
        if (dbOpenManager != null){
            return;
        }
        init();
    }

    //初始化数据库
    private void init() {
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger();
        }
    }
    //选择数据库分支
    public DatabaseManager changeData(Class dataHelperClass){
        String key = dataHelperClass.getName();
        Log.e("momo","DatabaseManager: changeData: "+key);
        if (!dbHelpers.containsKey(key)|| dbHelpers.get(key) == null){
            try {
                Constructor constructor = dataHelperClass.getDeclaredConstructor(Context.class);
                dbHelpers.put(key, (IDatabaseHelper) constructor.newInstance(BaseApplication.getContext()));
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        dbHelper = dbHelpers.get(key);
        return this;
    }

    //单例模式获取操作类对象(懒汉式)
    protected static DatabaseManager getInstance() {
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
        //查看当前 AtomicInteger 中的 value 值
        if (atomicInteger.incrementAndGet() == 1) {
            try { //获取一个可读可写的数据库操作对象
                database = dbHelper.getWritableDatabase();
            } catch (Exception e) {
                atomicInteger.set(0);
                e.printStackTrace();
            }
        }

        String dbName = dbHelper.getDatabaseName();
        CreateTable createTable;
        if (!dbTables.containsKey(dbName) || dbTables.get(dbName)== null){
            createTable = new CreateTable(dbName,dbHelper.getMarkerPackageName());
            dbTables.put(dbName,createTable);
        }else {
            createTable = dbTables.get(dbName);
        }
        createTable.createTables(database);


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


    public static LinkedList<Map<String,String>> query(Class clazz, ContentValues contentValues){
        SQLiteDatabase db = dbOpenManager.openDatabase();
        LinkedList<Map<String,String>> linkedList = new LinkedList<>();
        try {
            IBeans aBean = (IBeans) clazz.newInstance();
            if (!tabbleIsExist(db,aBean.tableName)){
                throw new Exception(aBean.tableName+"表格不存在");
            }
            if (contentValues == null){//全搜索,无条件
                dbOpenManager.closeDatabase();
                return query(clazz);
            }else {
                Cursor cursor = db.query(aBean.tableName,null,null,null,null,null,null);
                linkedList = cursor2List(cursor);
                cursor.close();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linkedList;
    }
    public static LinkedList<Map<String,String>> query(Class clazz,String querySql){
        SQLiteDatabase db = dbOpenManager.openDatabase();
        LinkedList<Map<String,String>> linkedList = new LinkedList<>();
        try {
            IBeans aBean = (IBeans) clazz.newInstance();
            if (!tabbleIsExist(db,aBean.tableName)){
                throw new Exception(aBean.tableName+"表格不存在");
            }
            Cursor cursor = db.rawQuery(querySql,null);
            linkedList = cursor2List(cursor);
            cursor.close();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return linkedList;
    }
    public static LinkedList<Map<String,String>> query(Class clazz){
        SQLiteDatabase db = dbOpenManager.openDatabase();
        LinkedList<Map<String,String>> linkedList = new LinkedList<>();
        try {
            IBeans aBean = (IBeans) clazz.newInstance();
            if (!tabbleIsExist(db,aBean.tableName)){
                throw new Exception(aBean.tableName+"表格不存在");
            }
            Cursor cursor = db.query(aBean.tableName,null,null,null,null,null,null);
            linkedList = cursor2List(cursor);
            cursor.close();

        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbOpenManager.closeDatabase();
        return linkedList;
    }
    private static LinkedList<Map<String,String>> cursor2List(Cursor cursor){
        LinkedList<Map<String,String>> linkedList = new LinkedList<>();
            while (cursor.moveToNext()){
                String[] colNames = cursor.getColumnNames();
                HashMap<String,String> map = new HashMap<>();
                for (String name :colNames) {
                    map.put(name,cursor.getString(cursor.getColumnIndex(name)));
                }
                linkedList.add(map);
            }
        return linkedList;
    }
    public static void insert(Class clazz, ContentValues contentValues){
        SQLiteDatabase db = dbOpenManager.openDatabase();
        try {

            IBeans aBean = (IBeans) clazz.newInstance();
            if (!tabbleIsExist(db,aBean.tableName)){
                db.execSQL(aBean.createSql);
            }
            db.insert(aBean.tableName,null,contentValues);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        dbOpenManager.closeDatabase();
    }
    public static void delete(Class clazz, ContentValues contentValues){
        SQLiteDatabase db = dbOpenManager.openDatabase();

        dbOpenManager.closeDatabase();
    }
    public static void update(Class clazz, ContentValues contentValues){
        SQLiteDatabase db = dbOpenManager.openDatabase();

        dbOpenManager.closeDatabase();
    }

    public static boolean tabbleIsExist(SQLiteDatabase db, String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
            cursor.close();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

}