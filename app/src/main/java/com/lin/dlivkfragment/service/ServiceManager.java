package com.lin.dlivkfragment.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;


import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.MusicApp;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.interfaces.IOnServiceConnectComplete;
import com.lin.dlivkfragment.model.MusicInfo;
import com.lin.dlivkfragment.util.MusicUtils;

import java.util.List;

/**
 * 封装aidl接口，面向上层应用提供服务管理
 * Created by zzq on 15-7-28.
 */
public class ServiceManager implements IConstants {

    public IMediaService mService;

    private Context mContext;

    private ServiceConnection mConn;

    private IOnServiceConnectComplete mIOnServiceConnectComplete;

    private MusicUtils musicUtils;

    private String curListName;

    private static final int MSG_HAS_DATA = 1;
    private static ServiceManager serviceManager;
     ServiceManager(Context context){
        this.mContext = context;
        musicUtils = new MusicUtils();
        initConn();
        setCurListName(mContext.getString(R.string.all_list_name));
    }

    public static ServiceManager init(Context context){
        if(serviceManager==null){
            serviceManager = new ServiceManager(context);
        }
        return serviceManager;
    }
    public void destory(){
        serviceManager = null;
    }
    public String getCurListName() {
        return curListName;
    }

    public void setCurListName(String curListName) {
        this.curListName = curListName;
    }
    List<MusicInfo> musicList;
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case MSG_HAS_DATA:
                    if(musicUtils.musicHasData()){
                        musicList = musicUtils.getMusicInfoList();
                        Log.d("ServiceManager","refreshData");
                        refreshMusicList(musicList, mContext.getString(R.string.all_list_name));
                    }

                default:
                    break;
            }
        }
    };

    public void refreshData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message message = new Message();
//                if (musicUtils.musicHasData()) {
//                    message.what = MSG_HAS_DATA;
//                } else {
//                    musicUtils.queryMusic(MusicApp.getContext());
                    musicUtils.queryMusic(mContext);
                Log.d("ServiceManager", "refreshData:xxxxxxx");
                    message.what = MSG_HAS_DATA;
//                }
                handler.sendMessage(message);
            }
        }).start();
    }

    public void reQueryData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                musicUtils.clearData();
                Message message = new Message();
                musicUtils.queryMusic(MusicApp.getContext());
                Log.d("ServiceManager","reQueryData:xxxxxxx");
                message.what = MSG_HAS_DATA;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initConn(){
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IMediaService.Stub.asInterface(service);
                if(mService != null){
                    mIOnServiceConnectComplete.onServiceConnectComplete(mService);
                    refreshData();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    public void refreshMusicList(List<MusicInfo> musicList, String listName){
        if(musicList != null && mService != null){
            try{
                mService.refreshMusicList(musicList);
                curListName = listName;
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
    }




    public void refreshCurMusic(MusicInfo info){
        if(info == null || mService == null) return;

        try{
            mService.refreshCurMusic(info);
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

//    public List<MusicInfo> getMusicList(){
//        List<MusicInfo> musicList = new ArrayList<MusicInfo>();
//        try{
//            if(mService != null){
//                mService.getMusicList(musicList);
//            }
//        }catch(RemoteException e){
//            e.printStackTrace();
//        }
//        return musicList;
//    }
    public List<MusicInfo> getMusicList(){

        return musicList;
    }


    public boolean play(int pos){
        if(mService != null){
            try{
                return mService.play(pos);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean playById(int id){
        if(mService != null){
            try{
                return mService.playById(id);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean rePlay(){
        if(mService != null){
            try{
                return mService.rePlay();
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean pause(){
        if(mService != null){
            try{
                return mService.pause();
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean prev() {
        if(mService != null) {
            try {
                return mService.prev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean next() {
        if(mService != null) {
            try {
                return mService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean seekTo(int progress) {
        if(mService != null) {
            try {
                return mService.seekTo(progress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }




    public int position() {
        if(mService != null) {
            try {
                return mService.position();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public int duration() {
        if(mService != null) {
            try {
                return mService.duration();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int getPlayState() {
        if(mService != null) {
            try {
                int mode = mService.getPlayState();
                return mService.getPlayState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public void setPlayMode(int mode) {
        if(mService != null) {
            try {
                mService.setPlayMode(mode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int getPlayMode() {
        if(mService != null) {
            try {
                return mService.getPlayMode();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int getCurMusicId() {
        if(mService != null) {
            try {
                return mService.getCurMusicId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public MusicInfo getCurMusic() {
        if(mService != null) {
            try {
                return mService.getCurMusic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void sendBroadcast() {
        if(mService != null) {
            try {
                mService.sendPlayStateBrocast();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        if(mService != null) {
            try {
                mService.exit();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        disConnectService();
    }

    public void connectService(){
        Intent intent = new Intent(SERVICE_NAME);
        intent.setPackage("com.lin.dlivkfragment");
        mContext.bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    public void disConnectService(){
        mContext.unbindService(mConn);
//        mContext.stopService(new Intent(SERVICE_NAME));
    }

    public void setOnServiceConnectComplete(IOnServiceConnectComplete IServiceConnect){
        mIOnServiceConnectComplete = IServiceConnect;
    }
    public int getPreId(){
        if(mService != null) {
            try {
                return mService.getPreId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    public int getNextId(){
        if(mService != null) {
            try {
                return mService.getNextId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


}
