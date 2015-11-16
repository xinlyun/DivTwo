package com.lin.dlivkfragment;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.lin.dlivkfragment.service.ServiceManager;
import com.lin.dlivkfragment.util.ActivityManager;

import java.io.File;

/**
 * 指定了一些路径，实例化了服务管理对象，提供了退出APP和全局获取context的静态方法
 * Created by ZQ on 2015/7/20.
 */
public class MusicApp extends Application {

    private static Context mContext;
    //General Path
    public static String rootPath = "/xiaopengmusic";
    public static String lrcPath = "/lrc";

    public static ServiceManager mServiceManager = null;

    @Override
    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
//        mServiceManager = new ServiceManager(this);
        mServiceManager = ServiceManager.init(this);
        initPath();
        Log.d("hehe", "MusicApp onCreate");
    }

    public static Context getContext(){
        return mContext;
    }

    private void initPath(){
        String root = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            root = Environment.getExternalStorageDirectory().getPath();
        }
        rootPath = root + rootPath;
        lrcPath = rootPath + lrcPath;
        Log.d("hehe", lrcPath);
        File lrcFile = new File(lrcPath);

        if(!lrcFile.exists()){
            lrcFile.mkdirs();
        }
    }

    public static void finishApp(){
        ActivityManager.finishAll();
        if(mServiceManager != null){
            mServiceManager.exit();
        }
        android.os.Process.killProcess(android.os.Process.myPid());

    }

}
