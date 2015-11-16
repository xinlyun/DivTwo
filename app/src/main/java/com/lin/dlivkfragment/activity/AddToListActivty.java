package com.lin.dlivkfragment.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;


import com.lin.dlivkfragment.fragment.MusicPlayer;
import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.adapter.PlayListAdapter;
import com.lin.dlivkfragment.model.ListInfo;
import com.lin.dlivkfragment.util.ActivityManager;
import com.lin.dlivkfragment.util.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加到列表界面
 * Created by zzq on 15-8-15.
 */

public class AddToListActivty extends Activity
implements AdapterView.OnItemClickListener, IConstants {

    private List<ListInfo> listNames = new ArrayList<>();
    private MusicUtils musicUtils = new MusicUtils();

    private ListView lvList;
    private static MusicPlayer mainActivity;
    private static int musicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list_activty);

        lvList = (ListView) findViewById(R.id.lv_add_to);
        lvList.setOnItemClickListener(this);
        refreshList();
        ActivityManager.addActivity(this);

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        window.setAttributes(lp);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3){
        //根据index，将当前歌曲插入到列表中
        String listName = listNames.get(index).listName;
        if(listName.equals(TABLE_MUSIC)) {
            finish();
            return;
        }else if(listName.equals(getString(R.string.favorite_list_name))){
            mainActivity.changeLoveState();

        }else{
            musicUtils.insertToList(listName, musicId);
            mainActivity.refreshData();
        }
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    private void refreshList(){
        if(musicUtils == null) {
            musicUtils = new MusicUtils();
        }
        listNames = musicUtils.getListInfo();
        PlayListAdapter playListAdapter = new PlayListAdapter(
                this, R.layout.musiclist_item, listNames);
        lvList.setAdapter(playListAdapter);
    }

    public static void actionStart(Context context, int id, MusicPlayer activity){
        Intent intent = new Intent(context, AddToListActivty.class);
        context.startActivity(intent);
        musicId = id;
        mainActivity = activity;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view
                .getLayoutParams();

        lp.gravity = Gravity.CENTER;
        lp.width = getResources().getDimensionPixelSize(
                R.dimen.list_width);
        lp.height = getResources().getDimensionPixelSize(
                R.dimen.list_height);
        getWindowManager().updateViewLayout(view, lp);
    }
}
