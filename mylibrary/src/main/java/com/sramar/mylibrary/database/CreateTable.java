package com.sramar.mylibrary.database;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.sramar.mylibrary.appManager.BaseApplication;
import com.sramar.mylibrary.appManager.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateTable{
    String dbName;
    Class packageMark;
    ArrayList<String> totalTables = new ArrayList<>();
    ArrayList<String> createdTables = new ArrayList<>();

    public CreateTable(String dbName,Class packageMark){
        this.dbName = dbName;
        this.packageMark = packageMark;
        getSubClass(packageMark);
    }

    public void createTables(SQLiteDatabase database){
        if (totalTables.size() <= createdTables.size()){

        }else {
            initTable(database);
        }
    }

    //相册
    public void initTable(SQLiteDatabase database){
        for (String s:totalTables){
            ABeans i = null;
            try {
                i = (ABeans) Class.forName(s).newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (DatabaseManager.tabbleIsExist(database,i.getTableName())){
                createdTables.add(s);
            }else {
                database.execSQL(i.getSqlStringCreateTable());
            }
        }
    }

    public void getSubClass(Class packageMark){
        String beanPackage = packageMark.getPackage().getName()+".bean";
        List<String> allSubClass = ABeans.getAllSubClass(BaseApplication.getContext(), beanPackage );
        for (String s:allSubClass){
            try {
                Class c = Class.forName(s);
                if ((ABeans.class.isAssignableFrom(c)) && !c.getName().contains(ABeans.class.getName())){
                    totalTables.add(s);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}