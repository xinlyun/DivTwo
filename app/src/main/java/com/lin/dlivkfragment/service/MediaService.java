package com.lin.dlivkfragment.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.appwidget.MusicWidget;
import com.lin.dlivkfragment.model.MusicInfo;

import java.util.List;

/**
 * 将播放控制模块封装进后台服务中，提供aidl接口
 * Created by zzq on 15-7-27.
 */
public class MediaService extends Service implements IConstants {
    private final String ACTION_UPDATE_ALL = "com.xiaopeng.widget.UPDATE_ALL";
    private static final String START_BROADCAST_NAME = "com.xiaopeng.musicplayer.start.broadcast";
    private static final String PAUSE_BROADCAST_NAME = "com.xiaopeng.musicplayer.pause.broadcast";
    private static final String NEXT_BROADCAST_NAME = "com.xiaopeng.musicplayer.next.broadcast";
    private static final String PRE_BROADCAST_NAME = "com.xiaopeng.musicplayer.pre.broadcast";
    private static final String CREATE_BROADCAST_NAME = "com.xiaopeng.musicplayer.create.broadcast";
    public RemoteViews views; //RemoteView对象
    public ComponentName thisWidget; //组件名
    public AppWidgetManager manager; // AppWidget管理器


    private static final int PAUSE_FLAG = 0X1;
    private static final int NEXT_FALG = 0X2;
    private static final int PRE_FLAG = 0X3;
    private static final int START_FLAG = 0X4;

    private final IBinder mBinder = new ServerStub();

    private MusicControl mMc;
    private ControlBroadcast mControlBrocast;
    private ServiceManager serviceManager;



    @Override
    public void onCreate(){
        Log.d("MediaService", "onCreate");
        super.onCreate();



        serviceManager = ServiceManager.init(this);
//        serviceManager.refreshData();


        mMc = new MusicControl(this);



        mControlBrocast = new ControlBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_BROADCAST_NAME);
        filter.addAction(PAUSE_BROADCAST_NAME);
        filter.addAction(NEXT_BROADCAST_NAME);
        filter.addAction(PRE_BROADCAST_NAME);
        filter.addAction(CREATE_BROADCAST_NAME);
        registerReceiver(mControlBrocast, filter);

//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("flag",1);
//        startActivity(intent);
//        initwidget();
    }

    private void initwidget(){
        thisWidget = new ComponentName(this, MusicWidget.class);

        manager = AppWidgetManager.getInstance(this);
        views = new RemoteViews(getPackageName(), R.layout.music_widget);
        Intent startService = new Intent("com.xiaopeng.widget.start1");

        PendingIntent pstartService = PendingIntent.getBroadcast(this,0,startService,0);

        Intent start = new Intent("com.xiaopeng.musicplayer.start.broadcast");
        start.putExtra("FLAG",START_FLAG);
        PendingIntent pstart = PendingIntent.getBroadcast(this,0,start,0);

        Intent pause = new Intent("com.xiaopeng.widget.stop1");
        pause.putExtra("FLAG",PAUSE_FLAG);
        PendingIntent ppause = PendingIntent.getBroadcast(this,0,pause,0);

        Intent next = new Intent("com.xiaopeng.musicplayer.next.broadcast");
        next.putExtra("FLAG", NEXT_FALG);
        PendingIntent pnext = PendingIntent.getBroadcast(this,0,next,0);

        Intent prep = new Intent("com.xiaopeng.musicplayer.pre.broadcast");
        prep.putExtra("FLAG", PRE_FLAG);
        PendingIntent pprep = PendingIntent.getBroadcast(this,0,prep,0);

        views.setOnClickPendingIntent(R.id.widget_ib_play,pstartService);
        views.setOnClickPendingIntent(R.id.widget_ib_pause,ppause);
        views.setOnClickPendingIntent(R.id.widget_ib_next,pnext);
        views.setOnClickPendingIntent(R.id.widget_ib_prev,pprep);

        manager.updateAppWidget(thisWidget, views);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MediaService","onBind");
        return mBinder;
    }

    private class ControlBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
//            if(mMc==null) {
//                mMc = new MusicControl(MediaService.this);
//                MediaService.this.onCreate();
//            }
            int flag = intent.getIntExtra("FLAG", -1);
            String action = intent.getAction();
            Log.d("MediaService","onReceive"+action);
            switch (action){
                case PAUSE_BROADCAST_NAME:
                    try {
                        ((ServerStub)mBinder).pause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    mMc.pause();
                    break;
                case NEXT_BROADCAST_NAME:
                    try {
                        ((ServerStub)mBinder).next();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    mMc.next();
                    break;
                case PRE_BROADCAST_NAME:
                    try {
                        ((ServerStub)mBinder).prev();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    mMc.prev();
                    break;
                case START_BROADCAST_NAME:
                    try {
                        if(serviceManager.getCurMusic()==null)
                            ((ServerStub)mBinder).play(0);
                        else  ((ServerStub)mBinder).rePlay();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CREATE_BROADCAST_NAME:
                    MusicInfo m = mMc.getmCurMusic();

                    Intent intentx = new Intent(ACTION_UPDATE_ALL);
                    intentx.putExtra("style",0);
                    intentx.putExtra("name",m.musicName);
                    intentx.putExtra("album",m.albumId);
                    MediaService.this.sendBroadcast(intentx);
                    break;
            }
        }
    }

    private class ServerStub extends IMediaService.Stub{

        @Override
        public boolean pause() throws RemoteException {
//            Intent intent = new Intent(ACTION_UPDATE_ALL);
//            intent.putExtra("style",1);
//            MediaService.this.sendBroadcast(intent);


            return mMc.pause();
        }

        @Override
        public boolean prev() throws RemoteException{
            boolean b=mMc.prev();
//            Log.d("MediaService","prev:"+b);
//            MusicInfo m = mMc.getmCurMusic();
//            Intent intent = new Intent(ACTION_UPDATE_ALL);
////            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//            intent.putExtra("style",0);
//            intent.putExtra("name",m.musicName);
//            intent.putExtra("album",m.albumId);
//            MediaService.this.sendBroadcast(intent);

            return b;
        }

        @Override
        public boolean next() throws RemoteException{
            boolean b=mMc.next();

//            MusicInfo m = mMc.getmCurMusic();
//            Log.d("MediaService","next:"+b+" name:"+m.musicName+" album:"+m.albumId);
//            Intent intent = new Intent(ACTION_UPDATE_ALL);
////            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//            intent.putExtra("style",0);
//            intent.putExtra("name",m.musicName);
//            intent.putExtra("album",m.albumId);
//            MediaService.this.sendBroadcast(intent);

            return b;
        }

        @Override
        public boolean play(int pos) throws RemoteException{
//            Intent intent2 = new Intent();
//            intent2.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//            MediaService.this.sendBroadcast(intent2);

            boolean b=mMc.play(pos);
//            if(!b){
//                mMc.refreshMusicList(serviceManager.getMusicList());
//            }
//            Log.d("MediaService", "play:" + b);
//            MusicInfo m = mMc.getmCurMusic();
//
//            Intent intent = new Intent(ACTION_UPDATE_ALL);
//            intent.putExtra("style",0);
//            intent.putExtra("name",m.musicName);
//            intent.putExtra("album",m.albumId);
//            MediaService.this.sendBroadcast(intent);
            return b;
        }

        @Override
        public int duration() throws RemoteException{
            return mMc.duration();
        }

        @Override
        public int position() throws RemoteException{
            return mMc.position();
        }

        @Override
        public boolean seekTo(int progress) throws RemoteException{
            return mMc.seekTo(progress);
        }

        @Override
        public void refreshMusicList(List<MusicInfo> musicList) throws RemoteException{
            mMc.refreshMusicList(musicList);
        }

        @Override
        public void getMusicList(List<MusicInfo> musicList) throws RemoteException{
            List<MusicInfo> music = mMc.getMusiclist();
            for(MusicInfo m : music){
                musicList.add(m);
            }
        }

        @Override
        public void refreshCurMusic(MusicInfo info) throws RemoteException{
            mMc.refreshCurMusic(info);
        }

        @Override
        public int getPlayState() throws RemoteException{
            return mMc.getPlayState();
        }

        @Override
        public int getPlayMode() throws RemoteException{
            return mMc.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException{
            mMc.setPlayMode(mode);
        }

        @Override
        public void sendPlayStateBrocast() throws RemoteException{
            //mMc.sendBroadCast();
        }

        @Override
        public void exit() throws RemoteException{
            stopSelf();
            mMc.exit();
        }

        @Override
        public boolean rePlay() throws RemoteException{
            MusicInfo m = mMc.getmCurMusic();
            Intent intent = new Intent(ACTION_UPDATE_ALL);
            intent.putExtra("style",0);
            intent.putExtra("name",m.musicName);
            intent.putExtra("album",m.albumId);
            MediaService.this.sendBroadcast(intent);
            return mMc.replay();
        }

        @Override
        public int getCurMusicId() throws RemoteException{
            return mMc.getCurMusicId();
        }

        @Override
        public boolean playById(int id) throws RemoteException{
            return mMc.playById(id);
        }

        @Override
        public MusicInfo getCurMusic() throws RemoteException{
            return mMc.getmCurMusic();
        }

        @Override
        public int getNextId() throws RemoteException{
            return mMc.getNextId();
        }
        @Override
        public int getPreId() throws  RemoteException{
            return mMc.getPreId();
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("MediaService", "onDestroy");
        if(serviceManager!=null)serviceManager.destory();
        if(mControlBrocast != null){
            unregisterReceiver(mControlBrocast);
        }
    }
}
