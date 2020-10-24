package com.sramar.myapplication.database.bean;

import com.sramar.myapplication.database.ABeanFieldDesc;
import com.sramar.myapplication.database.ABeanTable;
import com.sramar.myapplication.database.EBeanField;
import com.sramar.myapplication.database.EBeanFieldDesc;
import com.sramar.myapplication.database.EBeanTable;

@ABeanFieldDesc(eField = EBeanField.ALL)
public class ExampelBean {
    //表名以及版本
    @ABeanTable(EBeanTable.NAME)
    public final static String TABLE_NAME = "my_example";
    @ABeanTable(EBeanTable.VERSION)
    public final static int TABLE_VERSION = 1;

    //主键
    @ABeanFieldDesc(desc = EBeanFieldDesc.KEY)
    public final static String ID = "_id";

    //全部参数如下
    @ABeanFieldDesc(eField = EBeanField.INTEGER,bit = 2,desc = EBeanFieldDesc.DEFAULT,value = "1")
    public final static String FINISH = "is_finish";
    @ABeanFieldDesc(eField = EBeanField.INTEGER,bit = 2,desc = EBeanFieldDesc.DEFAULT,value = "0")
    public final static String CHECKED = "is_check";

    //默认将public final static String视作varcahr(100)字段
    public final static String Default1 = "default1";
    public final static String Default2 = "default2";
}
