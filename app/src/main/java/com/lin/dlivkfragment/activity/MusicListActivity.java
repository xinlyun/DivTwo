package com.lin.dlivkfragment.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.lin.dlivkfragment.fragment.MusicPlayer;
import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.MusicApp;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.adapter.PlayListAdapter;
import com.lin.dlivkfragment.adapter.PlayListDetailAdapter;
import com.lin.dlivkfragment.model.ListInfo;
import com.lin.dlivkfragment.model.MusicInfo;
import com.lin.dlivkfragment.service.ServiceManager;
import com.lin.dlivkfragment.util.ActivityManager;
import com.lin.dlivkfragment.util.MusicUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放列表界面
 * Created by zzq on 15-7-29.
 */
public class MusicListActivity extends Activity implements View.OnClickListener,
        AdapterView.OnItemClickListener, IConstants {

    private static MusicPlayer mMainActivity;
    private static ServiceManager serviceManager;

    private int curContent;
    private static final int CONTENT_LIST = 0;
    private static final int CONTENT_LIST_DETAIL = 1;

    private int selectedList;

    private ImageButton ibAdd;
    private ImageButton ibDelete;
    private ImageButton ibBack;
    private ImageButton ibSelectAll;

    private ListView lvList;
    private ListView lvListDetail;

    private TextView tvMusicLists;

    private List<MusicInfo> mSelectedList = new ArrayList<MusicInfo>();
    private List<ListInfo> listNames = new ArrayList<>();
    private static MusicUtils musicUtils = new MusicUtils();
    private MusicPlayBroadcast musicPlayBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        initView();
        initConnection();
        refreshListNames();
        refreshListView();
        curContent = CONTENT_LIST;

        musicPlayBroadcast = new MusicPlayBroadcast();
        IntentFilter filterMusic = new IntentFilter(BROADCAST_NAME);
        filterMusic.addAction(BROADCAST_NAME);
        registerReceiver(musicPlayBroadcast, filterMusic);
        ActivityManager.addActivity(this);

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        window.setAttributes(lp);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(musicPlayBroadcast);
        ActivityManager.removeActivity(this);
    }

    public void refreshList(){
        refreshListNames();
        refreshListView();
    }

    private void updatePlayState(){
        refreshListView();
        refreshMusicList(listNames.get(selectedList).listName);
    }

    private void refreshListNames(){
        listNames = new ArrayList<ListInfo>();
        ListInfo music = new ListInfo();
        music._id = 0x7f7f7f;
        music.listName = getString(R.string.all_list_name);
        listNames.add(0, music);
        List<ListInfo> tempList = musicUtils.getListInfo();
        for(ListInfo info : tempList){
            listNames.add(info);
        }
    }

    private void initView(){
        ibAdd = (ImageButton) findViewById(R.id.bt_add_list);
        ibAdd.setOnClickListener(this);

        ibDelete = (ImageButton) findViewById(R.id.bt_delete_selected);
        ibDelete.setVisibility(View.INVISIBLE);
        ibDelete.setOnClickListener(this);

        ibBack = (ImageButton) findViewById(R.id.ib_list_back);
        ibBack.setOnClickListener(this);
        ibBack.setVisibility(View.INVISIBLE);

        ibSelectAll = (ImageButton) findViewById(R.id.ib_select_all);
        ibSelectAll.setOnClickListener(this);
        ibSelectAll.setVisibility(View.INVISIBLE);

        lvList = (ListView) findViewById(R.id.music_list);
        lvListDetail = (ListView) findViewById(R.id.music_detail_list);
        lvList.setVisibility(View.VISIBLE);
        lvListDetail.setVisibility(View.INVISIBLE);
        lvList.setOnItemClickListener(this);
        lvListDetail.setOnItemClickListener(this);

        lvList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(position == 0 || position == 1){
                    return false;
                }

                Dialog dialog = new AlertDialog.Builder(MusicListActivity.this)
                        .setTitle("确认删除")
                        .setIcon(R.drawable.iv_delete)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteListItem(position);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.8f;
                window.setAttributes(lp);
                dialog.show();

                return true;
            }
        });

        lvListDetail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        tvMusicLists = (TextView) findViewById(R.id.tv_music_lists);

    }

    private void deleteListItem(int position){
        if(!(1 < position && position < lvList.getAdapter().getCount())){
            return;
        }

        musicUtils.deleteList(listNames.get(position)._id);
        refreshList();
    }

    private void initConnection() {
        if(serviceManager == null) serviceManager = MusicApp.mServiceManager;

    }

    private void deleteSelected(){
        if(lvListDetail == null) return;
        int count = lvListDetail.getAdapter().getCount();
        ListInfo listInfo = listNames.get(selectedList);
        List<MusicInfo> musics = musicUtils.getMusicInfoList(listInfo);
        for(int i = 0; i < count; i++){
            View view = getViewByPosition(i, lvListDetail);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb_delete);
            if(cb.isChecked()){
                musicUtils.deleteFromList(listInfo.listName, musics.get(i).songId);
            }
        }
        refreshMusicList(listInfo.listName);
    }

    private void selectAll(){
        if(lvListDetail == null) return;
        int count = lvListDetail.getAdapter().getCount();
        for(int i = 0; i < count; i++){
            View view = getViewByPosition(i, lvListDetail);
            CheckBox cb = (CheckBox) view.findViewById(R.id.cb_delete);
            cb.setChecked(true);

        }
        PlayListDetailAdapter.selectAll(count);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_add_list:
                AddListActivity.actionStart(this, this);
                break;
            case R.id.bt_delete_selected:
                deleteSelected();
                break;
            case R.id.ib_list_back:
                changeListVisiable();
                break;
            case R.id.ib_select_all:
                selectAll();
                break;
            default:

                break;
        }
    }

    private class MusicPlayBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            if(intent.getAction().equals(BROADCAST_NAME)){
                updatePlayState();
            }
        }
    }

    private void changeListVisiable(){
        if(lvList.getVisibility() != View.VISIBLE){
            //将要显示的是lvList
            curContent = CONTENT_LIST;
            lvList.setVisibility(View.VISIBLE);
            lvListDetail.setVisibility(View.INVISIBLE);
            tvMusicLists.setText(getString(R.string.music_lists));
            changeButtonVisiable();
        }else{
            //将要显示的是lvListDetail
            curContent = CONTENT_LIST_DETAIL;
            lvList.setVisibility(View.INVISIBLE);
            lvListDetail.setVisibility(View.VISIBLE);
            changeButtonVisiable();
        }
    }

    private void changeButtonVisiable(){
        if(curContent == CONTENT_LIST){
            ibBack.setVisibility(View.INVISIBLE);
            ibDelete.setVisibility(View.INVISIBLE);
            ibSelectAll.setVisibility(View.INVISIBLE);
            ibAdd.setVisibility(View.VISIBLE);
        }else if(curContent == CONTENT_LIST_DETAIL){
            ibBack.setVisibility(View.VISIBLE);
            ibSelectAll.setVisibility(View.VISIBLE);
            ibAdd.setVisibility(View.INVISIBLE);
            if(selectedList != 0){
                ibDelete.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshListView() {
        PlayListAdapter playListAdapter = new PlayListAdapter(MusicListActivity.this,
                    R.layout.musiclist_item, listNames,
                    MusicUtils.seekListPos(listNames, serviceManager.getCurListName()));
        lvList.setAdapter(playListAdapter);
    }

    private void refreshMusicList(String listName){
        tvMusicLists.setText(listName);
        mSelectedList = musicUtils.getMusicInfoList(listName);
        if(mSelectedList == null){
            mSelectedList = new ArrayList<>();
        }

        PlayListDetailAdapter playListDetailAdapter;
        if(listName.equals(serviceManager.getCurListName())
                && serviceManager.getPlayState() != MPS_INVALID){
            playListDetailAdapter = new PlayListDetailAdapter(
                    MusicListActivity.this, R.layout.play_list_detail_item,
                    mSelectedList, MusicUtils.seekPosInListById(
                    serviceManager.getMusicList(), serviceManager.getCurMusicId()));
        }else{
            playListDetailAdapter = new PlayListDetailAdapter(
                    MusicListActivity.this, R.layout.play_list_detail_item,
                    mSelectedList);
        }

        lvListDetail.setAdapter(playListDetailAdapter);
    }

    private View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int index, long arg3){
        if(curContent == CONTENT_LIST){
            selectedList = index;

            refreshMusicList(listNames.get(index).listName);
            changeListVisiable();

        }else if (curContent == CONTENT_LIST_DETAIL){

            if(mSelectedList == null || mSelectedList.size() <= index) return;

            //将当前播放列表设置为选择的列表
            String listName = listNames.get(selectedList).listName;
            serviceManager.refreshMusicList(mSelectedList, listName);
            //开始播放当前选择的音乐
            serviceManager.play(index);
            mMainActivity.refreshUI();
        }
    }

    public static void actionStart(Context context, MusicPlayer activity){
        Intent intent = new Intent(context, MusicListActivity.class);
        context.startActivity(intent);
        mMainActivity = activity;
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
