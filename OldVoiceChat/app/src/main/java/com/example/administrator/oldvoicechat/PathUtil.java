package com.example.administrator.oldvoicechat;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

/**
 * DATE：2018/4/24
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */

public class PathUtil {


    public static boolean isSdcardExit() {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    public static String getVoiceFilePath() {
        //要求应用卸载，文件也要卸载  不在使用Environment.getExternalStorageDirectory()

        File dir = BaseApplication.getInstances().getExternalFilesDir("voice");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = generateFileName();
        File file = new File(dir, fileName);
        return file.getAbsolutePath();
    }

    /**
     * @return 随机生成的录音文件名称
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public static long getFileSize(String path) {
        File mFile = new File(path);
        if (!mFile.exists()) {
            return -1;
        }
        return mFile.length();
    }
}
