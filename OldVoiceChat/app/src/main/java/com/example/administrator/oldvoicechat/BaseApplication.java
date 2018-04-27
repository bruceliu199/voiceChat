package com.example.administrator.oldvoicechat;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.greendao.DaoMaster;
import com.example.administrator.greendao.DaoSession;

/**
 * DATE：2018/4/24
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */

public class BaseApplication extends Application {

    private static BaseApplication instances;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static BaseApplication getInstances() {
        return instances;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;

        initGreenDao();
    }

    /**
     * 设置greenDao
     */
    private void initGreenDao() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "pingantong.db", null);
        db = mHelper.getWritableDatabase();

        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }
}
