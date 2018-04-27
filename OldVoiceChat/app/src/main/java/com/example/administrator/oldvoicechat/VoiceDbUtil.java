package com.example.administrator.oldvoicechat;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.example.administrator.greendao.VoiceMsgDao;

import java.util.ArrayList;
import java.util.List;

/**
 * DATE：2018/4/25
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */

public class VoiceDbUtil {

    private static class VoiceDbUtilHolder {
        private static VoiceDbUtil instance = new VoiceDbUtil();
    }

    public static VoiceDbUtil getInstance() {
        return VoiceDbUtilHolder.instance;
    }

    private VoiceDbUtil() {

    }

    private VoiceMsgDao getVoiceMsgDao() {
        return BaseApplication.getInstances().getDaoSession().getVoiceMsgDao();
    }

    private SQLiteDatabase getSqlDb() {
        return BaseApplication.getInstances().getDb();
    }

    //增
    public boolean insert(VoiceMsg msg) {
        boolean flag = false;
        try {
            getVoiceMsgDao().insert(msg);
            flag = true;
        } catch (Exception e) {
            Log.e("kk", "insert: -----------" + e.getMessage());
        }

        return flag;
    }

    /*
    * 插入多条数据对象
    * 可能会存在耗时 操作 所以new 一个线程
    * */
    public Boolean insertMultUser(final List<VoiceMsg> msgs) {
        boolean flag = false;
        try {
            BaseApplication.getInstances().getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (VoiceMsg msg : msgs) {
                        insert(msg);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return flag;
    }

    //删
    public boolean delete(VoiceMsg msg) {
        boolean flag = false;
        try {
            getVoiceMsgDao().delete(msg);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;

    }
    //改

    public boolean update(VoiceMsg msg) {
        boolean flag = false;
        try {
            getVoiceMsgDao().update(msg);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    //查

    public List<VoiceMsg> getAll() {
        return getVoiceMsgDao().loadAll();
    }


    /**
     * 分页加载  20条
     *
     * @param offset 页
     * @return
     */
    public List<VoiceMsg> getTwentyMsg(int offset) {
        if (offset < 1) {
            throw new IllegalArgumentException("offset need input the number over zero");
        }
        List<VoiceMsg> listMsg = getVoiceMsgDao().queryBuilder()
                .offset((offset - 1) * 20).limit(20).list();
        return listMsg;
    }

    /**
     * 类似于微信的从后面加载数据
     *
     * @param offset
     * @return
     */
    public List<VoiceMsg> getWXTwentyMsg(int offset) {
        List<VoiceMsg> msgs = new ArrayList<>();
        List<VoiceMsg> voiceMsgs = getVoiceMsgDao().loadAll();
        int size = voiceMsgs.size();
        if (size > offset * 20) {
            msgs = getVoiceMsgDao().queryBuilder().offset(size - offset * 20).limit(20).list();
        } else if (size > (offset - 1) * 20) {
            msgs = getVoiceMsgDao().queryBuilder().offset(0).limit(size - (offset - 1) * 20).list();
        }

        return msgs;
    }


}
