package com.example.administrator.oldvoicechat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DATE：2018/4/20
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */

public class VoiceMsgAdapter extends RecyclerView.Adapter<VoiceMsgAdapter.ViewHolder> {

    private static final int TYPE_SEND = 0x01;
    private static final int TYPE_RECEIVE = 0x02;
    private final int duration;

    private List<VoiceMsg> msgs;
    private Context ctx;
    private final LayoutInflater mInflater;
    private int mMaxWidth;
    private int mMinWidth;
    private View currentAnimView = null;
    private AnimationDrawable animation;


    public VoiceMsgAdapter(Context ctx, List<VoiceMsg> msgs, int duration) {
        this.ctx = ctx;
        this.duration = duration;
        if (msgs == null) this.msgs = new ArrayList<VoiceMsg>();
        else this.msgs = msgs;
        mInflater = LayoutInflater.from(ctx);
        //获取屏幕的宽度
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之56
        mMaxWidth = (int) (outMetrics.widthPixels * 0.56f);
        //最小宽度为屏幕宽度的百分之16
        mMinWidth = (int) (outMetrics.widthPixels * 0.16f);
    }

    /**
     * 第一次加载
     *
     * @param datas
     */
    public void setData(List<VoiceMsg> datas) {
        msgs.clear();
        msgs.addAll(datas);
        notifyDataSetChanged();
    }

    public void loadMore(List<VoiceMsg> datas) {
        msgs.addAll(0, datas);
        notifyItemRangeInserted(0, datas.size());
    }

    /**
     * 添加一条
     *
     * @param data
     */
    public void addData(VoiceMsg data) {
        msgs.add(data);
        if (msgs.size() > 0) notifyItemInserted(msgs.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SEND:
                View view1 = mInflater.inflate(R.layout.chat_recycle_item_send_right, parent, false);
                return new ViewHolder(view1);

            case TYPE_RECEIVE:
                View view2 = mInflater.inflate(R.layout.chat_recycle_item_receive_left, parent, false);
                return new ViewHolder(view2);

            default:
                View view0 = mInflater.inflate(R.layout.chat_recycle_item_send_right, parent, false);
                return new ViewHolder(view0);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int itemViewType = getItemViewType(position);
        final VoiceMsg msg = msgs.get(position);
        Log.e("ll", "onBindViewHolder(VoiceMsgAdapter.java:102---------)" + msg.getId());

        if (position == 0) {
            holder.time.setText(VoiceDateUtils.getTimestampString(new Date(msg.getTime())));
            holder.time.setVisibility(View.VISIBLE);
        } else {
            // 两条消息时间离得如果稍长，显示时间
            if (VoiceDateUtils.isCloseEnough(msg.getTime(), msgs.get(position - 1).getTime())) {
                holder.time.setVisibility(View.GONE);
            } else {
                holder.time.setText(VoiceDateUtils.getTimestampString(new Date(
                        msg.getTime())));
                holder.time.setVisibility(View.VISIBLE);
            }
        }
        ViewGroup.LayoutParams lp = holder.voiceBg.getLayoutParams();
        lp.width = (int) (mMinWidth + ((float) (mMaxWidth - mMinWidth) / duration) * msg.getVoiceTime());
        holder.voiceTime.setText(Math.round(msg.getVoiceTime()) + "\"");
        holder.voiceBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAnimView != null && currentAnimView == holder.voiceAnim && MediaPlayerHelper.isPlaying()) {
                    MediaPlayerHelper.release();
                    if (itemViewType == TYPE_SEND) {
                        currentAnimView.setBackgroundResource(R.drawable.data_ico_left_voice_three1);
                    } else {
                        currentAnimView.setBackgroundResource(R.drawable.data_ico_right_voice_three1);
                    }
                    currentAnimView = null;
                    return;
                }
                if (animation != null && animation.isRunning()) {
                    animation.stop();
                    animation = null;
                }

                if (currentAnimView != null) {
                    if (itemViewType == TYPE_SEND) {
                        currentAnimView.setBackgroundResource(R.drawable.data_ico_left_voice_three1);
                    } else {
                        currentAnimView.setBackgroundResource(R.drawable.data_ico_right_voice_three1);
                    }
                    currentAnimView = null;
                }

                currentAnimView = holder.voiceAnim;
                if (itemViewType == TYPE_SEND) {
                    currentAnimView.setBackgroundResource(R.drawable.voice_play_send_anim);
                    MediaPlayerHelper.play(msg.getFilePath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            MediaPlayerHelper.release();
                            currentAnimView.setBackgroundResource(R.drawable.data_ico_left_voice_three1);
                        }
                    });
                } else {
                    currentAnimView.setBackgroundResource(R.drawable.voice_play_receive_anim);
                    MediaPlayerHelper.play(msg.getFilePath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            MediaPlayerHelper.release();
                            currentAnimView.setBackgroundResource(R.drawable.data_ico_right_voice_three1);
                        }
                    });
                }

                animation = (AnimationDrawable) currentAnimView.getBackground();
                animation.start();

            }
        });

        if (itemViewType == TYPE_RECEIVE) {
            holder.tvName.setText(msg.getName());
        }


    }

    @Override
    public int getItemViewType(int position) {
        VoiceMsg msg = msgs.get(position);
        int deriction = msg.getDeriction();
        if (deriction == 0) {
            return TYPE_SEND;
        } else if (deriction == 1) {
            return TYPE_RECEIVE;
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return msgs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        //共有
        TextView time;
        ImageView ivIcon;
        ImageView voiceBg;
        TextView voiceTime;
        View voiceAnim;

        //接收特有
        TextView tvName;
        View unread;

        public ViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.timestamp); //时间
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon); //头像
            voiceBg = (ImageView) itemView.findViewById(R.id.iv_voice_bg); //消息长度条
            voiceTime = (TextView) itemView.findViewById(R.id.tv_voice_length); //消息时长
            voiceAnim = (View) itemView.findViewById(R.id.voice_anim); //消息播放动画

            tvName = itemView.findViewById(R.id.tv_name); //名字
            unread = itemView.findViewById(R.id.unread_flag); //未读标记

        }
    }


}
