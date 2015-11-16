package com.lin.dlivkfragment.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;


import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.model.MusicInfo;
import com.lin.dlivkfragment.util.MusicUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 所有播放操作的实际控制类
 * Created by zzq on 15-7-27.
 */
public class MusicControl implements IConstants, MediaPlayer.OnCompletionListener{
    private final String ACTION_UPDATE_ALL = "com.xiaopeng.widget.UPDATE_ALL";
    private String TAG = MusicControl.class.getSimpleName();
    private MediaPlayer mMediaplayer;
    private int mPlayMode;
    private List<MusicInfo> mMusiclist = new ArrayList<MusicInfo>();
    private int mPlayState;
    private int mCurPlayIndex;
    private Context mContext;
    private Random mRandom;
    private int mCurMusicId;
    private MusicInfo mCurMusic;
    private AudioManager mAudioManager;
    public MusicControl(Context context){
        mMediaplayer = new MediaPlayer();
        mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaplayer.setOnCompletionListener(this);
        mPlayMode = MPM_LIST_LOOP_PLAY;
        mPlayState = MPS_NOFILE;
        mCurPlayIndex = -1;
        mCurMusicId = -1;
        this.mContext = context;
        mRandom = new Random();
        mRandom.setSeed(System.currentTimeMillis());

        //--linzx
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
            {
                // Pause playback
                Log.d("MusicControl","Music pause1");
//                pause();
                mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaplayer.setVolume(0.285f, 0.285f);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d("MusicControl","Music stop1");
                pause();
                // Stop playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
                Log.d("MusicControl","Music Duck");
                mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaplayer.setVolume(0.285f, 0.285f);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback or Raise it back to normal
                Log.d("MusicControl", "Music replay");
//
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                         //execute the task
                        mMediaplayer.setVolume(1f, 1f);
                    }
                }, 1000);
            }
        }
    };


    public boolean play(int pos){
        if(mCurPlayIndex == pos){
            if(!mMediaplayer.isPlaying()){
                mMediaplayer.start();
                mPlayState = MPS_PLAYING;
                sendBroadCast();



                MusicInfo m = getmCurMusic();
                intent.putExtra("style",0);
                intent.putExtra("name", m.musicName);
                intent.putExtra("album",m.albumId);
                mContext.sendBroadcast(intent);



            }else{
                pause();
            }
            return true;
        }
        return prepare(pos) && replay();
    }

    public boolean playById(int id){
        int position = MusicUtils.seekPosInListById(mMusiclist, id);
        mCurPlayIndex = position;
        if(mCurMusicId == id){
            if(!mMediaplayer.isPlaying()){
                mMediaplayer.start();
                mPlayState = MPS_PLAYING;
                sendBroadCast();
            }else{
                pause();
            }
            return true;
        }

        return prepare(position) && replay();
    }

    public boolean replay(){
        if(mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE){
            return false;
        }

        mMediaplayer.start();
        mPlayState = MPS_PLAYING;
        sendBroadCast();

        MusicInfo m = getmCurMusic();
        Intent intent = new Intent(ACTION_UPDATE_ALL);
        intent.putExtra("style",0);
        intent.putExtra("name", m.musicName);
        intent.putExtra("album", m.albumId);
        mContext.sendBroadcast(intent);


        return true;
    }

    public boolean pause(){
        if(mPlayState != MPS_PLAYING){
            return false;
        }
        mMediaplayer.pause();
        mPlayState = MPS_PAUSE;
        sendBroadCast();

        intent.putExtra("style",1);
        mContext.sendBroadcast(intent);


        return true;
    }

    public boolean prev(){
        if(mPlayState == MPS_NOFILE){
            return false;
        }
        mCurPlayIndex--;
        mCurPlayIndex = reviseIndex(mCurPlayIndex);

        boolean b= prepare(mCurPlayIndex) && replay();
        if(b){
            MusicInfo m = getmCurMusic();
//            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            intent.putExtra("style",0);
            intent.putExtra("name",m.musicName);
            intent.putExtra("album",m.albumId);
            mContext.sendBroadcast(intent);
        }



        return b;
    }
    Intent intent = new Intent(ACTION_UPDATE_ALL);
    public boolean next(){
        if(mPlayState == MPS_NOFILE){
            return false;
        }
        mCurPlayIndex++;
        mCurPlayIndex = reviseIndex(mCurPlayIndex);

        boolean b= prepare(mCurPlayIndex) && replay();

        if(b){
            MusicInfo m = getmCurMusic();

//            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            intent.putExtra("style",0);
            intent.putExtra("name",m.musicName);
            intent.putExtra("album",m.albumId);
            mContext.sendBroadcast(intent);
        }
        return b;
    }

    private int reviseIndex(int index){
        if(index < 0){
            index = mMusiclist.size() - 1;
        }
        if(index >= mMusiclist.size()){
            index = 0;
        }
        return index;
    }

    public int position(){
        if(mPlayState == MPS_PLAYING || mPlayState == MPS_PAUSE){
            return mMediaplayer.getCurrentPosition();
        }
        return 0;
    }

    public int duration(){
        if(mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE){
            return 0;
        }
        return mMediaplayer.getDuration();
    }

    public boolean seekTo(int progress){
        if(mPlayState == MPS_INVALID || mPlayState == MPS_NOFILE){
            return false;
        }
        int pro = reviseSeekValue(progress);
        int time = mMediaplayer.getDuration();
        int curTime = (int)((float)pro / 100 * time);
        mMediaplayer.seekTo(curTime);
        return true;
    }

    private int reviseSeekValue(int progress){
        if(progress < 0){
            progress = 0;
        } else if(progress > 100){
            progress = 100;
        }
        return progress;
    }

    public void refreshMusicList(List<MusicInfo> musicList){
        Log.d("MusicControl","refreshMusicList:size "+musicList.size());
        mMusiclist.clear();
        mMusiclist.addAll(musicList);
        if(mMusiclist.size() == 0){
            mPlayState = MPS_NOFILE;
            mCurPlayIndex = -1;
        }
        else mCurPlayIndex = -1;
    }

    //当添加“我的最爱”时，只更新当前歌曲信息
    public void refreshCurMusic(MusicInfo info){
        if(mMusiclist.size() <= mCurPlayIndex) {
            return;
        }
        mMusiclist.set(mCurPlayIndex, info);
    }

    private boolean prepare(int pos){
        Log.d("MusicControl","prepare:"+mMusiclist.size()+" State:"+mPlayState);
        if(pos >= mMusiclist.size()) return false;
        mCurPlayIndex = pos;
        mMediaplayer.reset();
        String path = mMusiclist.get(pos).data;
        try{
            mMediaplayer.setDataSource(path);
            mMediaplayer.prepare();
            mPlayState = MPS_PREPARE;
        }catch(Exception e){
            Log.d(TAG, "Exception nononono", e);
            mPlayState = MPS_INVALID;
//            if(pos < mMusiclist.size() - 1){
//                pos++;
//                playById(mMusiclist.get(pos).songId);
//            }
            sendBroadCast();
            return false;
        }
        sendBroadCast();
        return true;

    }

    public void sendBroadCast(){
        if(mCurPlayIndex < 0) return;

        Intent intent = new Intent(BROADCAST_NAME);
        intent.putExtra(PLAY_STATE_NAME, mPlayState);
        intent.putExtra(PLAY_MUSIC_INDEX, mCurPlayIndex);
        intent.putExtra("music_num", mMusiclist.size());
        if(mPlayState != MPS_NOFILE && mMusiclist.size() > 0){
            Bundle bundle = new Bundle();
            mCurMusic = mMusiclist.get(mCurPlayIndex);
            mCurMusicId = mCurMusic.songId;
            bundle.putParcelable(MusicInfo.KEY_MUSIC, mCurMusic);
            intent.putExtra(MusicInfo.KEY_MUSIC, bundle);
        }
        mContext.sendBroadcast(intent);
    }


    public int getPreId(){
        MusicInfo musicInfo = mMusiclist.get(mCurPlayIndex-1);
        return musicInfo.albumId;
    }
    public int getNextId(){
        MusicInfo musicInfo = mMusiclist.get(mCurPlayIndex+1);
        return musicInfo.albumId;
    }

    public int getCurMusicId(){
        return mCurMusicId;
    }

    public MusicInfo getmCurMusic(){
        return mCurMusic;
    }

    public int getPlayState() {
        return mPlayState;
    }

    public int getPlayMode(){
        return mPlayMode;
    }

    public void setPlayMode(int mode){
        mPlayMode = mode;
    }

    public List<MusicInfo> getMusiclist(){
        return mMusiclist;
    }

    @Override
    public void onCompletion(MediaPlayer mp){
        switch (mPlayMode){
            case MPM_LIST_LOOP_PLAY:
                next();
                break;
            case MPM_RANDOM_PLAY:
                int index = getRandomIndex();
                if(index != -1){
                    mCurPlayIndex = index;
                }else{
                    mCurPlayIndex = 0;
                }
                if (prepare(mCurPlayIndex)) {
                    replay();
                }
                break;
            case MPM_SINGLE_LOOP_PLAY:
                play(mCurPlayIndex);
                break;
            default:
                break;
        }
    }

    private int getRandomIndex(){
        int size = mMusiclist.size();
        if(size == 0){
            return -1;
        }
        return Math.abs(mRandom.nextInt() % size);
    }

    public void exit(){
        mMediaplayer.stop();
        mMediaplayer.release();
        mCurPlayIndex = -1;
        mMusiclist.clear();
    }
}
