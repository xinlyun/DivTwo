package com.lin.dlivkfragment.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.lin.dlivkfragment.interfaces.IConstants;


/**
 * 音乐-列表信息，记录了所有添加到列表的音乐的记录
 * Created by zzq on 15-8-19.
 */
public class MusicListInfo implements Parcelable, IConstants {
    public int _id = -1;
    public int songId = -1;
    public int listId = -1;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, _id);
        bundle.putInt(KEY_SONG_ID, songId);
        bundle.putInt(KEY_LISTID, listId);

        dest.writeBundle(bundle);
    }

    public static final Creator<MusicListInfo> CREATOR = new Creator<MusicListInfo>(){
        @Override
        public MusicListInfo createFromParcel(Parcel source){
            MusicListInfo music = new MusicListInfo();
            Bundle bundle = source.readBundle();
            music._id = bundle.getInt(KEY_ID);
            music.songId = bundle.getInt(KEY_SONG_ID);
            music.listId = bundle.getInt(KEY_LISTID);

            return music;
        }

        @Override
        public MusicListInfo[] newArray(int size){
            return new MusicListInfo[size];
        }
    };
}
