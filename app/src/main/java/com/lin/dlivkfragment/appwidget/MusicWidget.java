package com.lin.dlivkfragment.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;


import com.lin.dlivkfragment.MainActivity;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.service.MediaService;
import com.lin.dlivkfragment.util.MusicUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Implementation of App Widget functionality.
 */
public class MusicWidget extends AppWidgetProvider {
    private static final String MUSIC = "com.xiaopeng.widget.UPDATE_ALL";
    private static final String TAG = "MusicWidget";
    private static final int PAUSE_FLAG = 0X1;
    private static final int NEXT_FALG = 0X2;
    private static final int PRE_FLAG = 0X3;
    private static final int START_FLAG = 0X4;
    private boolean DEBUG = false;
    private static Set idsSet = new HashSet();

    AppWidgetManager appWidgetManager;
    int[] appid;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d("MusicWidget","onUpdate");
        super.onUpdate(context,appWidgetManager,appid);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.music_widget);

        Intent startService = new Intent("com.xiaopeng.widget.start1");
        PendingIntent pstartService = PendingIntent.getBroadcast(context,0,startService,0);



        Intent start = new Intent("com.xiaopeng.musicplayer.start.broadcast");
        start.putExtra("FLAG",START_FLAG);
        PendingIntent pstart = PendingIntent.getBroadcast(context,0,start,0);

        Intent pause = new Intent("com.xiaopeng.widget.stop1");
        pause.putExtra("FLAG",PAUSE_FLAG);
        PendingIntent ppause = PendingIntent.getBroadcast(context,0,pause,0);

        Intent next = new Intent("com.xiaopeng.musicplayer.next.broadcast");
        next.putExtra("FLAG", NEXT_FALG);
        PendingIntent pnext = PendingIntent.getBroadcast(context,0,next,0);

        Intent prep = new Intent("com.xiaopeng.musicplayer.pre.broadcast");
        prep.putExtra("FLAG", PRE_FLAG);
        PendingIntent pprep = PendingIntent.getBroadcast(context,0,prep,0);

        Intent startactivity = new Intent(context, MainActivity.class);
        PendingIntent pStartactivity = PendingIntent.getActivity(context, 0, startactivity, 0);




        rv.setOnClickPendingIntent(R.id.widget_ib_play,pstartService);
        rv.setOnClickPendingIntent(R.id.widget_ib_pause,ppause);
        rv.setOnClickPendingIntent(R.id.widget_ib_next,pnext);
        rv.setOnClickPendingIntent(R.id.widget_ib_prev,pprep);
        rv.setOnClickPendingIntent(R.id.widget_backage,pStartactivity);
//        rv.setOnClickFillInIntent(R.id.widget_ib_play,startService);
//        rv.setOnClickFillInIntent(R.id.widget_ib_pause,pause);
//        rv.setOnClickFillInIntent(R.id.widget_ib_next,next);
//        rv.setOnClickFillInIntent(R.id.widget_ib_prev,prep);

            Log.d("MusicWidget","Updata");
            try {
                rv.setImageViewBitmap(R.id.widget_backage, bitmap);
            }catch (NullPointerException e){
                rv.setImageViewResource(R.id.widget_backage,R.raw.bg);
            }
        if(name!=null)
            rv.setTextViewText(R.id.widget_textview, name);


        appWidgetManager.updateAppWidget(appWidgetIds, rv);


        this.appWidgetManager  = appWidgetManager;
        this.appid = appWidgetIds;
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
            SharedPreferences.Editor editor = context.getSharedPreferences("data",Context.MODE_PRIVATE).edit();
            editor.putInt("id",appWidgetId);
            editor.commit();
        }
        prtSet();



    }
    // 调试用：遍历set
    private void prtSet() {
        if (DEBUG) {
            int index = 0;
            int size = idsSet.size();
            Iterator it = idsSet.iterator();
            Log.d(TAG, "total:"+size);
            while (it.hasNext()) {
                Log.d(TAG, index + " -- " + ((Integer)it.next()).intValue());
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d("MusicWidget","onEnabled");

        Intent intent = new Intent();
        intent.setAction("com.xiaopeng.musicplayer.create.broadcast");
        context.sendBroadcast(intent);

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        prtSet();
        super.onDeleted(context, appWidgetIds);
    }




    private Bitmap bitmap;
    private String name;
    private static final String ACTION_PACKAGE_DATA_CLEARED = "com.mediatek.intent.action.SETTINGS_PACKAGE_DATA_CLEARED";
    private SharedPreferences s;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (ACTION_PACKAGE_DATA_CLEARED.equals(intent.getAction())) {
            return;
        }
        if(appWidgetManager == null){
            appWidgetManager = AppWidgetManager.getInstance(context);
        }

        if(idsSet.size() == 0 ){
            if(s==null)
                s = context.getSharedPreferences("data",context.MODE_PRIVATE);
            int id = s.getInt("id",0);
            if(id != 0 )
                idsSet.add(Integer.valueOf(id));
        }

        Log.d(TAG,context.getPackageCodePath());
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.music_widget);
        int style = intent.getIntExtra("style", 10);
        Log.d("MusicWidget","onReceive"+style);
        switch (style){
            case 0:
                int album = intent.getIntExtra("album", -1);
                name = intent.getStringExtra("name");
                Log.d("MusicWidget","onReceive"+" name:"+name);
                bitmap=null;
                if(name!=null) {
                    remoteView.setTextViewText(R.id.widget_textview, name);
                }
                remoteView.setViewVisibility(R.id.widget_ib_pause, View.VISIBLE);
                remoteView.setViewVisibility(R.id.widget_ib_play, View.GONE);
                if(album!=-1) {
                    try {
                        bitmap = MusicUtils.getCachedArtwork(context, album, null);
                        remoteView.setImageViewBitmap(R.id.widget_backage, bitmap);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                        remoteView.setImageViewResource(R.id.widget_backage,R.raw.bg);
                    }
                }

                break;
            case 1:
                remoteView.setViewVisibility(R.id.widget_ib_pause, View.GONE);
                remoteView.setViewVisibility(R.id.widget_ib_play,View.VISIBLE);
                break;
            case 2:
                Intent i = new Intent(context, MediaService.class);
                context.startService(i);
                break;
            case 3:
                remoteView.setViewVisibility(R.id.widget_ib_pause, View.GONE);
                remoteView.setViewVisibility(R.id.widget_ib_play,View.VISIBLE);
                remoteView.setTextViewText(R.id.widget_textview, "小鹏汽车");
                remoteView.setImageViewResource(R.id.widget_backage, R.raw.bg);
                break;


        }
        Intent startService = new Intent("com.xiaopeng.widget.start1");
        PendingIntent pstartService = PendingIntent.getBroadcast(context,0,startService,0);



        Intent start = new Intent("com.xiaopeng.musicplayer.start.broadcast");
        start.putExtra("FLAG",START_FLAG);
        PendingIntent pstart = PendingIntent.getBroadcast(context,0,start,0);

        Intent pause = new Intent("com.xiaopeng.widget.stop1");
        pause.putExtra("FLAG",PAUSE_FLAG);
        PendingIntent ppause = PendingIntent.getBroadcast(context,0,pause,0);

        Intent next = new Intent("com.xiaopeng.musicplayer.next.broadcast");
        next.putExtra("FLAG", NEXT_FALG);
        PendingIntent pnext = PendingIntent.getBroadcast(context,0,next,0);

        Intent prep = new Intent("com.xiaopeng.musicplayer.pre.broadcast");
        prep.putExtra("FLAG", PRE_FLAG);
        PendingIntent pprep = PendingIntent.getBroadcast(context,0,prep,0);

        Intent startactivity = new Intent(context, MainActivity.class);
        PendingIntent pStartactivity = PendingIntent.getActivity(context, 0, startactivity, 0);




        remoteView.setOnClickPendingIntent(R.id.widget_ib_play,pstartService);
        remoteView.setOnClickPendingIntent(R.id.widget_ib_pause,ppause);
        remoteView.setOnClickPendingIntent(R.id.widget_ib_next,pnext);
        remoteView.setOnClickPendingIntent(R.id.widget_ib_prev,pprep);
        remoteView.setOnClickPendingIntent(R.id.widget_backage, pStartactivity);



        updateAllAppWidgets(context, AppWidgetManager.getInstance(context), remoteView, idsSet);


//        Bundle extras = intent.getExtras();
//        if (extras != null && extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS) != null) {
//            appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS).clone();
//            for(int idx:appWidgetIds){
//                i.add(idx);
//            }
//        }
//        if (i != null && i.size() > 0) {
////                this.onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
//            for (int idx:i) {
////                    appID = ((Integer)it.next()).intValue();
//                appWidgetManager.updateAppWidget(idx, remoteView);
//
//            }
//        }


        super.onReceive(context, intent);
    }
    int[] appWidgetIds;
    List<Integer> i = new ArrayList<>();
    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager,RemoteViews remoteViews, Set set) {

        Log.d(TAG, "updateAllAppWidgets(): size="+set.size());
        // widget 的id
        int appID;
//        // 迭代器，用于遍历所有保存的widget的id
        Iterator it = set.iterator();
        Log.d("MusicWidget","set:"+set.size());
        while (it.hasNext()) {
            appID = ((Integer)it.next()).intValue();
            appWidgetManager.updateAppWidget(appID, remoteViews);
        }
    }
}

