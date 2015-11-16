package com.lin.dlivkfragment.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.lin.dlivkfragment.interfaces.IConstants;


/**
 * 存储一些配置信息
 * Created by zzq on 15-7-27.
 */
public class SPStorage implements IConstants {
    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;

    public SPStorage(Context context){
        mSp = context.getSharedPreferences(SP_NAME,
                Context.MODE_MULTI_PROCESS);
        mEditor = mSp.edit();
    }

    public void savePath(String path){
        mEditor.putString(SP_BG_PATH, path);
        mEditor.commit();
    }

    public String getPath(){
        return mSp.getString(SP_BG_PATH, null);
    }

    public void saveFilterSize(boolean size){
        mEditor.putBoolean(SP_FILTER_SIZE, size);
        mEditor.commit();
    }

    public boolean getFilterSize(){
        return mSp.getBoolean(SP_FILTER_SIZE, false);
    }

    public void saveFilterTime(boolean time){
        mEditor.putBoolean(SP_FILTER_TIME, time);
        mEditor.commit();
    }

    public boolean getFilterTime(){
        return mSp.getBoolean(SP_FILTER_TIME, false);
    }
}
