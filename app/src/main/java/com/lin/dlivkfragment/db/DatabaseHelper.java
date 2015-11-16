package com.lin.dlivkfragment.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.MusicApp;
import com.lin.dlivkfragment.R;


/**
 * 数据库helper类，包含建表等基本公共操作
 * Created by zzq on 15-7-23.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements IConstants {
    private static SQLiteDatabase mDb;
    private static DatabaseHelper mHelper;

    private static final String CREATE_TABLE_MUSIC = "create table "
            + TABLE_MUSIC
            + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " songid integer, albumid integer, duration integer, musicname varchar(10), "
            + " artist char, data char, folder char, musicnamekey char, artistkey char, "
            + " favorite integer)";

    private static final String CREATE_TABLE_MUSICLIST = "create table "
            + TABLE_MUSICLIST
            + " (_id integer primary key autoincrement,"
            + " songid integer, "
            + " listid integer)";

    private static final String CREATE_TABLE_LIST = "create table "
            + TABLE_LIST
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LISTNAME + " char)";

    public static SQLiteDatabase getInstance(Context context){
        if(mDb == null){
            mDb = getHelper(context).getWritableDatabase();
        }
        return mDb;
    }

    public static DatabaseHelper getHelper(Context context){
        if(mHelper == null){
            mHelper = new DatabaseHelper(context);
        }
        return mHelper;
    }

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_MUSIC);
        db.execSQL(CREATE_TABLE_MUSICLIST);
        db.execSQL(CREATE_TABLE_LIST);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LISTNAME, MusicApp.getContext().getString(R.string.favorite_list_name));
        db.insert(TABLE_LIST, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public void deleteTables(Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MUSIC, null, null);
        db.delete(TABLE_LIST, null, null);
        db.delete(TABLE_MUSICLIST, null, null);
    }


}
