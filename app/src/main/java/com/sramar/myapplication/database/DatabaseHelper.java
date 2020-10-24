package com.sramar.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_name";
    private static final int DATABASE_VERSION = 1;

    private static Context myContext = null;

    public DatabaseHelper(Context context, String name,
                          CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext= context;
    }

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("UseDatabase", "创建数据库");
        CreateTable.initTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int newVersion) {
        switch (old){
            case 1:
            case 2:


        }
    }

    private static class CreateTable{
        //相册
        public static void initTable(SQLiteDatabase database){
            String beanPackage = ABeans.class.getPackage().getName()+".bean";
            List<String> allSubClass = ABeans.getAllSubClass(myContext, beanPackage );
            for (String s:allSubClass){

                try {
                    Class c = Class.forName(s);
                    if ((ABeans.class.isAssignableFrom(c)) && !c.getName().contains(ABeans.class.getName())){

                        ABeans i = (ABeans) c.newInstance();
                        database.execSQL(i.getSqlStringCreateTable());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static class UpdateTable{

    }

}