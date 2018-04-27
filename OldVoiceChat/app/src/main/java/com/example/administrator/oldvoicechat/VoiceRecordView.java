package com.example.administrator.oldvoicechat;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * DATE：2018/4/24
 * USER： liuzj
 * DESC：自定义一个view这里面可以做录音的事情。好处是可以把页面的逻辑分离减少耦合
 * email：liuzj@hi-board.com
 */

public class VoiceRecordView extends FrameLayout {
    private Context context;
    private ImageView ivVoiceRecorder;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private int duration = 60;//默认最长录音时长60秒
    private VoiceRecordMannager voiceRecorder;
    private int[] pics;
    private VoiceCountTimer timer;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what > 6) {
                ivVoiceRecorder.setImageResource(pics[6]);
            } else {
                ivVoiceRecorder.setImageResource(pics[msg.what]);
            }
        }
    };

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public VoiceRecordView(Context context) {
        this(context, null);
    }

    public VoiceRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.widget_voice_recorder, this);
        ivVoiceRecorder = (ImageView) findViewById(R.id.iv_voice_recorder);
        voiceRecorder = new VoiceRecordMannager(mHandler);

        pics = new int[]{R.drawable.voice_bg_upglide_1,
                R.drawable.voice_bg_upglide_2,
                R.drawable.voice_bg_upglide_3,
                R.drawable.voice_bg_upglide_4,
                R.drawable.voice_bg_upglide_5,
                R.drawable.voice_bg_upglide_6,
                R.drawable.voice_bg_upglide_7,
        };
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "pat");

    }

    //把按住说话按钮的touch事件拿过来
    public boolean onPressVoiceButton(View v, MotionEvent event, VoiceRecordListener listener) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // TODO: 2018/4/24  这里如果有语音在播放就要停掉
                //开始录音
                startRecording();
                timer = new VoiceCountTimer(1000 * duration, 1000, listener, event);
                timer.start();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    //取消发送
                    voiceRecorder.setCancel(true);
                    ivVoiceRecorder.setImageResource(R.drawable.data_ico_cancel);

                } else {
                    voiceRecorder.setCancel(false);
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (timer != null) {
                    timer.onFinish();
                    timer.cancel();
                    timer = null;
                    return true;
                }

                if (event.getY() < 0) {
                    cancelRecord();
                } else {
                    try {
                        int length = stopRecord();
                        if (length > 0) {
                            if (listener != null) {
                                listener.onVoiceRecordComplete(getVoicePath(), length);
                            }
                        } else if (length == 401) {
                            Toast.makeText(context, "无录音权限", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "录音时间太短", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;

            default:
                cancelRecord();
                return false;
        }
    }


    /**
     * @return 返回录音文件的路径
     */
    public String getVoicePath() {
        return voiceRecorder.getCurrentVoicePath();
    }

    public boolean isRecording() {
        return voiceRecorder.isRecording();
    }

    /**
     * 停止录音
     *
     * @return
     */
    private int stopRecord() {
        this.setVisibility(View.INVISIBLE);
        if (mWakeLock.isHeld())
            mWakeLock.release();
        return voiceRecorder.stopRecord();
    }

    /**
     * 取消录音
     */
    private void cancelRecord() {
        if (mWakeLock.isHeld())
            mWakeLock.release();
        try {
            if (voiceRecorder.isRecording()) {
                voiceRecorder.cancelRecord();
                this.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        if (!PathUtil.isSdcardExit()) {
            Toast.makeText(context, "录音需要SD卡支持", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mWakeLock.acquire();
            this.setVisibility(View.VISIBLE);
            voiceRecorder.startRecord();
        } catch (Exception e) {
            e.printStackTrace();
            if (mWakeLock.isHeld())
                mWakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.cancelRecord();
            this.setVisibility(View.INVISIBLE);
            Toast.makeText(context, "录音失败，请重试！", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    class VoiceCountTimer extends CountDownTimer {

        private MotionEvent event;
        private VoiceRecordListener vListener;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public VoiceCountTimer(long millisInFuture, long countDownInterval, VoiceRecordListener vListener, MotionEvent event) {
            super(millisInFuture, countDownInterval);
            this.vListener = vListener;
            this.event = event;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //这里可以做个时间提示
        }

        @Override
        public void onFinish() {
            if (event.getY() < 0) {
                // discard the recorded audio.
                cancelRecord();
            } else {
                // stop recording and send voice file
                try {
                    int length = stopRecord();
                    if (length > 0) {
                        if (vListener != null) {
                            vListener.onVoiceRecordComplete(getVoicePath(), length);
                        }
                    } else if (length == 401) {
                        Toast.makeText(context, "无录音权限", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "录音时间太短", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "发送失败，请检测服务器是否连接", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public interface VoiceRecordListener {
        /**
         * @param voiceFilePath   录音文件路径
         * @param voiceTimeLength 录音时间长度
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }


}
