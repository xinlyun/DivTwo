package com.lin.dlivkfragment.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.lin.dlivkfragment.interfaces.IConstants;


/**
 * 音乐信息，记录了曲目的详细信息
 * Created by zzq on 15-7-23.
 */
public class MusicInfo implements Parcelable, IConstants {

    public int _id = -1;
    public int songId = -1;
    public int albumId = -1;
    public int duration = 0;
    public int favorite = 0;
    public String musicName = "";
    public String artist = "";
    public String data = "";
    public String folder = "";
    public String musicNameKey = "";
    public String artistkey = "";

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, _id);
        bundle.putInt(KEY_SONG_ID, songId);
        bundle.putInt(KEY_ALBUM_ID, albumId);
        bundle.putInt(KEY_DURATION, duration);
        bundle.putInt(KEY_FAVORITE, favorite);
        bundle.putString(KEY_MUSIC_NAME, musicName);
        bundle.putString(KEY_ARTIST, artist);
        bundle.putString(KEY_DATA, data);
        bundle.putString(KEY_FOLDER, folder);
        bundle.putString(KEY_MUSICNAME_KEY, musicNameKey);
        bundle.putString(KEY_ARTIST_KEY, artistkey);

        dest.writeBundle(bundle);
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>(){
        @Override
        public MusicInfo createFromParcel(Parcel source){
            MusicInfo music = new MusicInfo();
            Bundle bundle = source.readBundle();
            music._id = bundle.getInt(KEY_ID);
            music.songId = bundle.getInt(KEY_SONG_ID);
            music.albumId = bundle.getInt(KEY_ALBUM_ID);
            music.duration = bundle.getInt(KEY_DURATION);
            music.musicName = bundle.getString(KEY_MUSIC_NAME);
            music.artist = bundle.getString(KEY_ARTIST);
            music.data = bundle.getString(KEY_DATA);
            music.folder = bundle.getString(KEY_FOLDER);
            music.musicNameKey = bundle.getString(KEY_MUSICNAME_KEY);
            music.favorite = bundle.getInt(KEY_FAVORITE);
            return music;
        }

        @Override
        public MusicInfo[] newArray(int size){
            return new MusicInfo[size];
        }
    };

    public int getFavorite(){
        return favorite;
    }

    public void setFavorite(int favorite){
        this.favorite = favorite;
    }

}
