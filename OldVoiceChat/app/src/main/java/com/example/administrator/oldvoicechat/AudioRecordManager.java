package com.example.administrator.oldvoicechat;

import android.media.MediaRecorder;

import java.io.File;
import java.util.UUID;

/**
 * DATE：2018/4/23
 * USER： liuzj
 * DESC：录音管理类 单例
 * email：liuzj@hi-board.com
 */

public class AudioRecordManager {

    private String mDir;  //录音文件所存放的文件夹路径

    //单例模式
    private static AudioRecordManager mInstance;
    //是否准备好默认为false
    private boolean isPrepare = false;
    private String mCurrentFilePath;
    private MediaRecorder mMediaRecorder;

    //私有构造方法
    private AudioRecordManager(String dir) {
        mDir = dir;
    }

    //对外公布获取实例的方法
    public static AudioRecordManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioRecordManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecordManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 用于回调录音状态
     */
    public interface AudioStateListener {
        void onPrepareAndStart();
    }

    public AudioStateListener mAudioStateListener;

    public void setOnAudioStateListener(AudioStateListener mAudioStateListener) {
        this.mAudioStateListener = mAudioStateListener;
    }

    /**
     * （1）设置音频来源（一般为麦克风）。
     * （2）设置音频输出格式。
     * （3）设置音频编码方式。
     * （4）设置输出音频的文件名。
     * （5）调用MediaRecorder类的perpare方法。
     * （6）调用MediaRecorder类的start方法开始录音。
     * 准备，并开始录音
     */
    public void startRecord() {

        try {
            //录音器
            mMediaRecorder = new MediaRecorder();
            //1设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //2设置音频输出格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            //3设置音频编码方式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //4指定输出音频文件，设置文件输出路径即我们把录音文件存在该位置
            File dir = new File(mDir);
            if (!dir.exists()) {
                //文件不存在则创建
                dir.mkdirs(); //mkdirs可以创建多级目录  mkdir只能创建一级目录
            }

            //创建随机的文件名
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //5.调用MediaRecorder类的perpare方法。
            mMediaRecorder.prepare();
            //6开始录音
            mMediaRecorder.start();

            // 准备完成
            isPrepare = true;
            if (mAudioStateListener != null) {
                mAudioStateListener.onPrepareAndStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @return 随机生成的录音文件名称
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    /**
     * @return 返回当前的录音文件路径
     */
    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 销毁释放资源
     */
    public void onDestroy() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder = null;
    }

    /**
     * 取消录音
     */
    public void cancel() {
        onDestroy();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public void getVoiceLevel() {

    }


}
