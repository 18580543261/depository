package com.sramar.myapplication.database;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dalvik.system.DexFile;

public abstract class ABeans {

    public static final String[] ALL_KEY_PHONE = {"key_phone"};

    protected ArrayList<String> keyFields;
    protected ArrayList<String> mFieldsName;
    protected ArrayList<String> mFields;

    protected String createSql;



    protected String tableName = "";
    protected int version = 1;

    public ABeans(){
        initTableNameAndVersion();
        initFields();
    }

    //获取该表的字段
    private void initFields(){

        keyFields = new ArrayList<>();
        mFields = new ArrayList<>();
        mFieldsName = new ArrayList<>();

        Class<?> clazz = this.getClass();
        //获取Bean是否具有全局字段
        if(clazz.isAnnotationPresent(ABeanFieldDesc.class)){
            //获取Info注解
            ABeanFieldDesc annotation = clazz.getAnnotation(ABeanFieldDesc.class);

            EBeanField eBeanFields = annotation.eField();
                switch (eBeanFields){
                    case ALL:
                        for(String s:ALL_KEY_PHONE){
                            mFields.add(s+" varchar(100) ,");
                            mFieldsName.add(s);
                        }
                        break;

                }
        }

        //获取Bean的各个字段
        Field[] fields = clazz.getDeclaredFields();
        if(fields != null && fields.length > 0){
            try {
                for(Field field : fields){
                    String fieldType = field.getType().getName();
                    String fieldName = field.getName();
                    Object fieldValue = field.get(null);
                    int fieldModifier = field.getModifiers();
                    if(field.isAnnotationPresent(ABeanFieldDesc.class)){
                        ABeanFieldDesc annotation = field.getAnnotation(ABeanFieldDesc.class);
                        EBeanFieldDesc[] eBeanFieldsDesc =annotation.desc();
                        EBeanField eBeanField = annotation.eField();
                        int bit = annotation.bit();
                        String value = annotation.value();

                        String type = "";
                        for(EBeanFieldDesc e:eBeanFieldsDesc){
                            if (e == EBeanFieldDesc.AUTOINCREASEMENT) {
                                eBeanField = EBeanField.INTEGER;
                            }
                        }
                        String baseSql = "";
                        switch (eBeanField){
                            case STRING:
                                type = " varchar";
                                baseSql = (String) fieldValue+type +"("+bit+")";
                                break;
                            case INTEGER:
                                type = " INTEGER ";
                                baseSql = (String) fieldValue+type ;
                                break;
                        }
                        String appended = "";
                        for (EBeanFieldDesc e:eBeanFieldsDesc){
                            switch (e){
                                case NORMAL:
                                    break;
                                case KEY:
                                    keyFields.add((String) fieldValue);
                                    break;
                                case AUTOINCREASEMENT:
//                                    appended += " AUTOINCREMENT ";
                                    break;
                                case DEFAULT:
                                    appended += " default "+value;
                                    break;
                                case NOTNULL:
                                    appended += " not null";
                                    break;
                            }
                        }
                        mFields.add(appendSql(baseSql,appended));
                        mFieldsName.add((String) fieldValue);
                    }else if (field.isAnnotationPresent(ABeanTable.class)){


                    }else {
                        if (fieldType.equals(String.class.getName()) && fieldModifier == 25){
//                            Log.e("momo","没有ABeanField注解，将public static final String 设置为STRING字段");
                            System.err.println("没有ABeanField注解，将public static final String "+fieldName+"设置为STRING字段");
//                            stringFields.add((String) fieldValue);
                            mFields.add((String) fieldValue+" varchar(100) ,");
                            mFieldsName.add((String) fieldValue);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //获取该表的表名以及版本名
    private void initTableNameAndVersion(){
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            if(fields != null && fields.length > 0){
                for(Field field : fields){
                    if(field.isAnnotationPresent(ABeanTable.class)){
                        ABeanTable annotation = field.getAnnotation(ABeanTable.class);

                        String fieldName = field.getName();
                        String fieldType = field.getType().getName();
                        Object fieldValue = field.get(null);
                        int fieldModifier = field.getModifiers();

                        EBeanTable eBeanTable = annotation.value();
                        switch (eBeanTable){
                            case NAME:
                                tableName = (String) fieldValue;
                                break;
                            case VERSION:
                                version = (int) fieldValue;
                                break;
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getTableName(){
        return tableName;
    }
    public String getTmepTableName(){
        return tableName+"_temp";
    }

    //数据库控制相关
    public String getSqlStringCreateTable(){
        if (createSql != null){
            return createSql;
        }
        String sql = "create table if not exists "+ tableName+" (";

        for(String s:mFields){
            sql += s;
        }
        if (!keyFields.isEmpty()){
            sql += "primary key(";
            for (String s: keyFields){
                sql += s +",";
            }
            sql = sql.substring(0,sql.length() - 1);
            sql += ")";
        }else {
            sql = sql.substring(0,sql.length() - 1);
        }

        sql += ")";
        createSql = sql;
        return createSql;
    }
    public String getSqlStringRenameTable(){
        String sql = "alter table "+ tableName+" rename to "+getTmepTableName();
        return sql;
    }
    public String getSqlStringInsertTable(){
        String sql = "insert into "+tableName+" select * from "+getTmepTableName();
        return sql;
    }
    public String getSqlStringDropTmTable(){
        String sql = "drop table "+getTmepTableName();
        return sql;
    }

    //数据库操作相关
    public ContentValues put(ContentValues contentValues,String key,Object value){
        contentValues.put(key, (String) value);
        return contentValues;
    }
    public ContentValues put(Map<String,Object> map){
        ContentValues contentValues = new ContentValues();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()){
            String key = it.next();
            if (mFieldsName.contains(key)){
                contentValues.put(key, (String) map.get(key));
            }
        }
        return contentValues;
    }
    public ContentValues put(JSONObject map){
        ContentValues contentValues = new ContentValues();
        Iterator<String> it = map.keys();
        while (it.hasNext()){
            String key = it.next();
            if (mFieldsName.contains(key)){
                try {
                    contentValues.put(key, (String) map.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return contentValues;
    }


    //动态获取，根据反射，比如获取xx.xx.xx.xx.Action 这个所有的实现类。 xx.xx.xx.xx 表示包名  Action为接口名或者类名
    public static List<Class<?>> getAllActionSubClass(String classPackageAndName) {
        Field field = null;
        Vector v = null;
        Class<?> cls = null;
        List<Class<?>> allSubclass = new ArrayList<Class<?>>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> classOfClassLoader = classLoader.getClass();
        try {
            cls = Class.forName(classPackageAndName);
            while (classOfClassLoader != ClassLoader.class) {
                classOfClassLoader = classOfClassLoader.getSuperclass();
            }
            field = classOfClassLoader.getDeclaredField("classes");
            field.setAccessible(true);
            v = (Vector) field.get(classLoader);
            for (int i = 0; i < v.size(); ++i) {
                Class<?> c = (Class<?>) v.get(i);
                if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                    allSubclass.add((Class<?>) c);
                }
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return allSubclass;
    }
    public static List<String > getAllSubClass(Context context,String packageName){
        List<String >classNameList=new ArrayList<>();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());//通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            int i = 0;
            while (enumeration.hasMoreElements()) {//遍历
                String className = (String) enumeration.nextElement();
                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    classNameList.add(className);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }

    //工具
    private static String appendSql(String start,String content){
        return start+content+",";
    }


    private class MyField{
        EBeanField eBeanField;

    }





}
