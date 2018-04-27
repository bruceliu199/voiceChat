package com.example.administrator.oldvoicechat;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * DATE：2018/4/24
 * USER： liuzj
 * DESC：录音管理类
 * email：liuzj@hi-board.com
 */

public class VoiceRecordMannager {

    private Handler handler;
    private File file;
    private MediaRecorder recorder;
    private String currentVoicePath;
    private boolean isRecording;  //是否在录音
    private boolean isCancel = false;  //到达可取消的边界时变为true
    private long startTime;

    public String getCurrentVoicePath() {
        return currentVoicePath;
    }

    public VoiceRecordMannager(Handler handler) {
        this.handler = handler;
    }

    /**
     * 开启录音
     */
    public void startRecord() {
        file = null;
        try {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //设置音频源为麦克风
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB); //设置输出格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //设置编码格式
            String voiceFilePath = PathUtil.getVoiceFilePath();
            file = new File(voiceFilePath);
            currentVoicePath = file.getAbsolutePath();
            recorder.setOutputFile(currentVoicePath);
            recorder.prepare();
            isRecording = true;
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //开启线程用于记录音量显示
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRecording) {
                        if (!isCancel) {
                            Message msg = new Message();
                            msg.what = recorder.getMaxAmplitude() / 3000;
                            handler.sendMessage(msg);
                            SystemClock.sleep(100);
                        }

                    }
                } catch (Exception e) {
                    // from the crash report website, found one NPE crash from
                    // one android 4.0.4 htc phone
                    // maybe handler is null for some reason
                }
            }
        }).start();

        startTime = new Date().getTime();


    }

    public void setCancel(boolean cancel) {
        this.isCancel = cancel;
    }

    /**
     * 取消录音
     */
    public void cancelRecord() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                //删除该文件
                if (file != null && file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRecording = false;
            currentVoicePath = null;
        }
    }


    /**
     * @return 结束录音返回录音时长
     */
    public int stopRecord() {
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;

            if (file == null || !file.exists() || !file.isFile()) {
                return 401;
            }
            if (file.length() == 0) {
                file.delete();
                return 401;
            }
            int seconds = (int) (new Date().getTime() - startTime) / 1000;  //得到录音时长  秒

            return seconds;
        }
        return 0;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (recorder != null) {
            recorder.release();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }
}
