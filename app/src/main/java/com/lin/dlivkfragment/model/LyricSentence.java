package com.lin.dlivkfragment.model;

/**
 * 歌词信息，记录了一句歌词的开始时间、持续时间、内容等信息
 * Created by zzq on 15-8-3.
 */
public class LyricSentence {

    private long startTime = 0;

    private long duringTime = 0;

    private String contentText = "";

    public LyricSentence(long time, String text){
        startTime = time;
        contentText = text;
    }

    public long getStartTime(){
        return startTime;
    }

    public void setStartTime(long startTime){
        this.startTime = startTime;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText){
        this.contentText = contentText;
    }

    public long getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(long duringTime){
        this.duringTime = duringTime;
    }

    public boolean isInTime(long time){
        return time >= startTime && time <= startTime + duringTime;
    }


}
