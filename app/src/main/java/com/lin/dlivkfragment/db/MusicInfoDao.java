package com.lin.dlivkfragment.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.model.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐信息的数据访问类
 * Created by zzq on 15-7-23.
 */
public class MusicInfoDao implements IConstants {
    private Context mContext;

    public MusicInfoDao(Context context){
        this.mContext = context;
    }

    public void saveMusicInfo(List<MusicInfo> list){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        for(MusicInfo music : list){
            ContentValues cv = new ContentValues();
            cv.put("songid", music.songId);
            cv.put("albumid", music.albumId);
            cv.put("duration", music.duration);
            cv.put("musicname", music.musicName);
            cv.put("artist", music.artist);
            cv.put("data", music.data);
            cv.put("folder", music.folder);
            cv.put("musicnamekey", music.musicNameKey);
            cv.put("artistkey", music.artistkey);
            cv.put("favorite", music.favorite);
            db.insert(TABLE_MUSIC, null, cv);
        }
    }

    private List<MusicInfo> parseCursor(Cursor cursor){
        List<MusicInfo> list = new ArrayList<MusicInfo>();
        while(cursor.moveToNext()){
            MusicInfo music = new MusicInfo();
            music._id = cursor.getInt(cursor.getColumnIndex("_id"));
            music.songId = cursor.getInt(cursor.getColumnIndex("songid"));
            music.albumId = cursor.getInt(cursor.getColumnIndex("albumid"));
            music.duration = cursor.getInt(cursor.getColumnIndex("duration"));
            music.musicName = cursor.getString(cursor.getColumnIndex("musicname"));
            music.artist = cursor.getString(cursor.getColumnIndex("artist"));
            music.data = cursor.getString(cursor.getColumnIndex("data"));
            music.folder = cursor.getString(cursor.getColumnIndex("folder"));
            music.musicNameKey = cursor.getString(cursor.getColumnIndex("musicnamekey"));
            music.artistkey = cursor.getString(cursor.getColumnIndex("artistkey"));
            music.favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
            list.add(music);
        }
        cursor.close();
        return list;
    }

    public List<MusicInfo> getMusicInfo() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_MUSIC;

        return parseCursor(db.rawQuery(sql, null));
    }

    public MusicInfo getMusicInfoById(int musicId){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_MUSIC +
                " where songid = " + musicId;
        List<MusicInfo> list = parseCursor(db.rawQuery(sql, null));
        if(list.size() == 0) return null;
        else return list.get(0);
    }

    public void deleteMusicInfo(MusicInfo music){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "delete from " + TABLE_MUSIC +
                " where _id = " + music._id;
        db.execSQL(sql);
    }

    public void deleteMusicInfo(){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "delete from " + TABLE_MUSIC;
        db.execSQL(sql);
    }

    public void setFavoriteStateById(int id, int favorite) {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "update " + TABLE_MUSIC + " set favorite = " + favorite + " where songid = " + id;
        db.execSQL(sql);
    }

    public boolean hasData() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_MUSIC;
        Cursor cursor = db.rawQuery(sql, null);
        boolean has = false;
        if(cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if(count > 0) {
                has = true;
            }
        }
        cursor.close();
        return has;
    }

    public int getDataCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_MUSIC;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
}
