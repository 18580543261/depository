安卓框架1.0

使用说明：
    一：Application板块：
    1.继承BaseApplication
    2.manifest中，添加<meta-data android:name="app_name" android:value="com.sramar.myapplication.modules.MyApplication"/>，value值为自定义的Application类（继承自BaseApplication）

    二：数据库板块：
    1.自写数据库帮助类MHelper（自定义）继承自IDatabaseHelper，并设置单参数构造函数调用super的四参数构造，其中setMarkerPackageName的参数，是一个随意的java类，名Marker（或任意）
    2.在Marker包下新增bean的包名
    3.在xxx.xxx.xxx.bean包中，新建字段类Example.java(自定义)，继承IBean(重点)
    4.自写application继承BaseApplication，并在registDatabaseHelper返回数据库帮助类MHelper.class
        字段类Example注解说明：
        1.类标签：@ABeanFieldDesc(eField = EBeanField.ALL)，表示用到全局字段
        2.字段标签： @ABeanTable(EBeanTable.NAME)，表示代表的是数据表名
        3.字段标签： @ABeanTable(EBeanTable.VERSION)，表示代表的是数据表版本
        4.字段标签： @ABeanFieldDesc(desc = EBeanFieldDesc.KEY)，表示代表的是主键，可设置联合主键
        5.字段标签：@ABeanFieldDesc(eField = EBeanField.INTEGER,bit = 2,desc = EBeanFieldDesc.DEFAULT,value = "1")，这是全参数
        6.无标签默认：public final static String Default1 = "default1";默认设置为varchar（100）

        注解参数说明：“、”表示单个使用；“|”表示是数组可组合，单个使用可不写大括号{}
            @ABeanTable（）：单参数
                EBeanTable.NAME、 EBeanTable.VERSION
            @ABeanFieldDesc（）：四参数
                eField = EBeanField.ALL、 EBeanField.STRING、 EBeanField.INTEGER
                bit = 位数1.....999
                desc = EBeanFieldDesc.DEFAULT | EBeanFieldDesc.NORMAL | EBeanFieldDesc.KEY | EBeanFieldDesc.NOTNULL | EBeanFieldDesc.AUTOINCREASEMENT
                value = "配合DEFAULT使用"
            无标签：
                将public final static String修饰的字段设置为varchar(100)

    三：常量池模块

    四：AppManager模块

    五：网络模块