package com.example.administrator.oldvoicechat;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * DATE：2018/4/24
 * USER： liuzj
 * DESC：音频播放
 * email：liuzj@hi-board.com
 */

public class MediaPlayerHelper {

    private static MediaPlayer mMediaPlayer;
    //是否暂停
    private static boolean isPause;

    /**
     * 播放
     */
    public static void play(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (!(new File(filePath).exists())) {
            return;
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 暂停
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static boolean isPlaying() {
        if (mMediaPlayer!=null) {
            return mMediaPlayer.isPlaying();
        }else {
            return false;
        }
    }


}
