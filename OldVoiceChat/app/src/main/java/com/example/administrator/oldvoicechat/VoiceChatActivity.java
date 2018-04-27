package com.example.administrator.oldvoicechat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

/**
 * DATE：2018/4/20
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */
public class VoiceChatActivity extends AppCompatActivity implements View.OnTouchListener {

    private Button btnSend;
    private Button btnTalk;
    private SwipeRefreshLayout srlFresh;
    private RecyclerView recyclerView;
    private VoiceMsgAdapter mAdapter;

    // 按钮正常状态（默认状态）
    private static final int STATE_NORMAL = 1;
    //正在录音状态
    private static final int STATE_RECORDING = 2;
    //录音取消状态
    private static final int STATE_CANCEL = 3;
    //记录当前状态
    private int mCurrentState = STATE_NORMAL;
    private VoiceRecordView voiceRecordView;
    //判断在Button上滑动距离，以判断 是否取消
    private static final int DISTANCE_Y_CANCEL = 50;
    private int page;
    private static final int DURATION = 15;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnTalk = (Button) findViewById(R.id.btn_talk);
        srlFresh = (SwipeRefreshLayout) findViewById(R.id.srl_fresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        voiceRecordView = (VoiceRecordView) findViewById(R.id.voice_recorder);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
            }
        }
        voiceRecordView.setDuration(DURATION);
        srlFresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.color_1)
                , ContextCompat.getColor(this, R.color.color_2)
                , ContextCompat.getColor(this, R.color.color_3));

        srlFresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ++page;
                loadData(page);
                //下拉
                srlFresh.setRefreshing(false);
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new VoiceMsgAdapter(this, null, DURATION);
        recyclerView.setAdapter(mAdapter);
        toBottom();
        btnTalk.setOnTouchListener(this);
        btnSend.setEnabled(false);
        page = 1;
        loadData(page);
        toBottom();
    }

    private void loadData(int page) {
        List<VoiceMsg> wxTwentyMsg = VoiceDbUtil.getInstance().getWXTwentyMsg(page);
        if (wxTwentyMsg != null) {
            if (wxTwentyMsg.size() > 0) {
                mAdapter.loadMore(wxTwentyMsg);
            } else {
                if (page>1) {
                    Toast.makeText(this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private void toBottom() {
        if (mAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }

    private static final int REQUEST_CODE = 482;

    /*
    * 语音按键的touch事件
    *
    * */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        toBottom();
        //获取TouchEvent状态
        int action = event.getAction();
        // 获得x轴坐标
        int x = (int) event.getX();
        // 获得y轴坐标
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: //按下
                changeState(STATE_RECORDING); //手指按下开始记录
                break;

            case MotionEvent.ACTION_MOVE: //移动
                if (voiceRecordView.isRecording()) {
                    if (y < 0) {
                        changeState(STATE_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP: //抬起
                changeState(STATE_NORMAL);
                break;

            default:
                changeState(STATE_NORMAL);
                break;
        }

        return voiceRecordView.onPressVoiceButton(v, event, new VoiceRecordView.VoiceRecordListener() {
            //录音结束回调
            @Override
            public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                VoiceMsg voiceMsg = new VoiceMsg(null, System.currentTimeMillis(), voiceFilePath, voiceTimeLength, 0, null);
                VoiceDbUtil.getInstance().insert(voiceMsg);
                mAdapter.addData(voiceMsg);
                toBottom();
            }
        });
    }

    private boolean wantToCancle(View v, int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > v.getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -DISTANCE_Y_CANCEL || y > v.getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    /**
     * 根据触摸状态改变button的显示
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL: //普通状态
                    btnSend.setBackgroundResource(R.drawable.data_bg_talk_nor1);
                    btnTalk.setBackgroundResource(R.drawable.data_btn_talk_nor1);
                    break;

                case STATE_RECORDING: //录音状态
                    btnSend.setBackgroundResource(R.drawable.data_bg_talk_up);
                    btnTalk.setBackgroundResource(R.drawable.data_btn_talk_up);
                    break;

                case STATE_CANCEL: //取消状态
                    btnSend.setBackgroundResource(R.drawable.data_bg_talk_nor1);
                    btnTalk.setBackgroundResource(R.drawable.data_btn_talk_nor1);
                    break;
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerHelper.release();
    }




}
