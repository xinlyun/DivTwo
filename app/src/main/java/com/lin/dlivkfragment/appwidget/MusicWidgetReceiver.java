package com.lin.dlivkfragment.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lin.dlivkfragment.service.MediaService;


/**
 * Created by xinlyun on 15-10-7.
 */
public class MusicWidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 ;
//        = new Intent(context, MediaService.class);
        intent1 = new Intent(context.getApplicationContext(),MediaService.class);
        intent1.setAction("com.xiaopeng.musicplayer.service.MediaService");
        intent1.setPackage(context.getPackageName());
        intent1.putExtra("style",0);
        context.startService(intent1);


        Intent intent2 = new Intent("com.xiaopeng.widget.UPDATE_ALL");
        intent2.putExtra("style",2);
        context.sendBroadcast(intent2);

        Intent start = new Intent("com.xiaopeng.musicplayer.start.broadcast");
        context.sendBroadcast(start);
    }
}
