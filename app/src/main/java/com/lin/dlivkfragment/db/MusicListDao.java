package com.lin.dlivkfragment.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.model.ListInfo;
import com.lin.dlivkfragment.model.MusicInfo;
import com.lin.dlivkfragment.model.MusicListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 音乐-列表信息的数据访问类
 * Created by zzq on 15-8-19.
 */
public class MusicListDao implements IConstants {

    private Context mContext;

    public MusicListDao(Context context){
        this.mContext = context;
    }

    public void insertToListById(int musicId, int listId){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        ContentValues cv = new ContentValues();
        cv.put(KEY_SONG_ID, musicId);
        cv.put(KEY_LISTID, listId);
        db.insert(TABLE_MUSICLIST, null, cv);
    }

    public void deleteFromListById(int musicId, int listId){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = " delete from " + TABLE_MUSICLIST + " where " + KEY_LISTID + " = " + listId
                + " and " + KEY_SONG_ID + " = " + musicId;
        db.execSQL(sql);
    }

    public void deleteFromListById(int listId){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = " delete from " + TABLE_MUSICLIST + " where " + KEY_LISTID + " = " + listId;
        db.execSQL(sql);
    }

    public void deleteMusicInfo(MusicInfo music){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "delete from " + TABLE_MUSICLIST + " where " + KEY_SONG_ID + " = " + music.songId;
        db.execSQL(sql);
    }

    public void deleteMusicInfo(){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "delete from " + TABLE_MUSICLIST;
        db.execSQL(sql);
    }

    private List<MusicListInfo> parseCursor(Cursor cursor){
        List<MusicListInfo> list = new ArrayList<MusicListInfo>();
        while(cursor.moveToNext()){
            MusicListInfo music = new MusicListInfo();
            music._id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            music.songId = cursor.getInt(cursor.getColumnIndex(KEY_SONG_ID));
            music.listId = cursor.getInt(cursor.getColumnIndex(KEY_LISTID));

            list.add(music);
        }
        cursor.close();
        return list;
    }

    public List<MusicListInfo> getMusicListInfo() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_MUSICLIST;

        return parseCursor(db.rawQuery(sql, null));
    }

    public List<MusicListInfo> getMusicListInfo(int listId){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_MUSICLIST
                + " where " + KEY_LISTID + " = " + listId;
        return parseCursor(db.rawQuery(sql, null));
    }

    public List<MusicListInfo> getMusicListInfo(ListInfo list){
        return getMusicListInfo(list._id);
    }



    public boolean hasData() {
        return getDataCount() > 0;
    }

    public int getDataCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_MUSICLIST;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
}
