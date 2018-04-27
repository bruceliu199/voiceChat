package com.example.administrator.oldvoicechat;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * DATE：2018/4/20
 * USER： liuzj
 * DESC：
 * email：liuzj@hi-board.com
 */
@Entity
public class VoiceMsg {

    @Id
    private Long id;
    private long time;//时间长度
    private String filePath;//文件路径
    private float voiceTime;//
    private int deriction;//0 send  1 receive
    private String name;
    @Generated(hash = 749010677)
    public VoiceMsg(Long id, long time, String filePath, float voiceTime,
            int deriction, String name) {
        this.id = id;
        this.time = time;
        this.filePath = filePath;
        this.voiceTime = voiceTime;
        this.deriction = deriction;
        this.name = name;
    }
    @Generated(hash = 809488364)
    public VoiceMsg() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public float getVoiceTime() {
        return this.voiceTime;
    }
    public void setVoiceTime(float voiceTime) {
        this.voiceTime = voiceTime;
    }
    public int getDeriction() {
        return this.deriction;
    }
    public void setDeriction(int deriction) {
        this.deriction = deriction;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
