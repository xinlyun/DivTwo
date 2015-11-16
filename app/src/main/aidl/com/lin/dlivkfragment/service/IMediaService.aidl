// IMediaService.aidl
package com.lin.dlivkfragment.service;

import com.lin.dlivkfragment.model.MusicInfo;
import android.graphics.Bitmap;

// Declare any non-default types here with import statements

interface IMediaService {
    boolean play(int pos);
    boolean playById(int id);
    boolean rePlay();
    boolean pause();
    boolean prev();
    boolean next();
    int duration();
    int position();
    boolean seekTo(int progress);
    void refreshMusicList(in List<MusicInfo> musicList);
    void refreshCurMusic(in MusicInfo info);
    void getMusicList(out List<MusicInfo> musicList);

    int getPlayState();
    int getPlayMode();
    void setPlayMode(int mode);
    void sendPlayStateBrocast();
    void exit();
    int getCurMusicId();
    MusicInfo getCurMusic();
    int getNextId();
    int getPreId();
}
