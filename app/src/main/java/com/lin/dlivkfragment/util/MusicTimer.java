package com.lin.dlivkfragment.util;

import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器，用于同步UI和播放进度
 * Created by zzq on 15-7-27.
 */
public class MusicTimer {
    public final static int REFRESH_PROGRESS_EVENT = 0x100;

    //计时器发送消息的时间间隔，大于500可能导致主界面播放时间更新不及时
    private static final int INTERVAL_TIME = 500;
    private Handler[] mHandler;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private int what;
    private boolean mTimerStart = false;

    public MusicTimer(Handler... handler){
        this.mHandler = handler;
        this.what = REFRESH_PROGRESS_EVENT;

        mTimer = new Timer();
    }

    public void startTimer(){
        if(mHandler == null || mTimerStart){
            return;
        }
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, INTERVAL_TIME, INTERVAL_TIME);
        mTimerStart = true;
    }

    public void stopTimer(){
        if(!mTimerStart){
            return;
        }
        mTimerStart = false;
        if(mTimerTask != null){
            mTimerTask.cancel();
            mTimerTask = null;
        }


    }

    class MyTimerTask extends TimerTask{
        @Override
        public void run(){
            if(mHandler != null){
                for(Handler handler : mHandler){
                    Message msg = handler.obtainMessage(what);
                    msg.sendToTarget();
                }
            }
        }
    }
}
