package com.lin.dlivkfragment.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.MusicApp;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.activity.AddToListActivty;
import com.lin.dlivkfragment.activity.MusicListActivity;
import com.lin.dlivkfragment.interfaces.IOnServiceConnectComplete;
import com.lin.dlivkfragment.model.MusicInfo;
import com.lin.dlivkfragment.service.IMediaService;
import com.lin.dlivkfragment.service.ServiceManager;
import com.lin.dlivkfragment.util.FastBlur;
import com.lin.dlivkfragment.util.LyricLoadHelper;
import com.lin.dlivkfragment.util.MusicTimer;
import com.lin.dlivkfragment.util.MusicUtils;
import com.lin.dlivkfragment.view.LrcViewManager;
import com.lin.dlivkfragment.view.VerticalSeekBar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xinlyun on 15-11-9.
 */
public class MusicPlayer extends Fragment implements View.OnClickListener,
        IOnServiceConnectComplete, IConstants, SeekBar.OnSeekBarChangeListener,
        View.OnLongClickListener{

    private MusicPlayBroadcast musicPlayBroadcast;
    private MusicVolBroadcast volBroadcast;
    private ServiceManager serviceManager;
    private MusicTimer mMusicTimer;

    private LyricLoadHelper mLyricLoadHelper;

    private ImageButton ibBack;
    private ImageButton ibSetting;
    private ImageButton ibPrev;
    private ImageButton ibNext;
    private ImageButton ibPlay;
    private ImageButton ibPause;
    private ImageButton ibLrc;
    private ImageButton ibMode;
    private ImageButton ibLove;
    private ImageButton ibAdd2List;
    private ImageButton ibVol;

    private ImageView ibBackage;

    private int curMode;

    private ThrandBitMap thrandBitMap;

    private static final int modeDrawable[] = {
            R.drawable.selector_bt_mode_list,
            R.drawable.selector_bt_mode_single,
            R.drawable.selector_bt_mode_random
    };

    private static final int PIC_LOVE = 0;
    private static final int PIC_LOVE_PRESSED = 1;

    private static final int loveDrawable[] = {
            R.drawable.selector_bt_love,
            R.drawable.selector_bt_love_selected
    };

    private CircleImageView ivAlbum0,ivAlbum1,ivAlbum2;
    private Bitmap mDefaultAlbumIcon;

    private TextView tvSong;
    private TextView tvSinger;
    private TextView tvNowTime;
    private TextView tvTotalTime;

    private TextView tvLrcPrev;
    private TextView tvLrcNow;
    private TextView tvLrcNext;

    private TextView tvVolText;
    private ImageView ivVolLabel;
    private FrameLayout flVol;

    private LrcViewManager lrcView;
    private SeekBar sbProcess;
    private VerticalSeekBar sbVol;

    MusicUtils musicUtils = new MusicUtils();

    private Boolean isLove = false,isPlaying = false;

    private AudioManager audioManager;
    private int maxVolume, currVolume;

    private int mProgress;
    private boolean mPlayAuto,mChangeStatu;
    private final String ACTION_UPDATE_ALL = "com.xiaopeng.widget.UPDATE_ALL";
    //private static MusicInfo currMusicInfo;

    private View view1, view2, view3;
    private ViewPager viewPager;  //对应的viewPager
    private List<View> viewList;//view数组

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshSeekProgress(serviceManager.position(), serviceManager.duration());
            lrcView.updateIndex(serviceManager.position());
        }
    };
    int i;
    View MainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        i = getActivity().getIntent().getIntExtra("flag",0);
        View view = inflater.inflate(R.layout.activity_main2,container,false);
        MainView = view;
        mLyricLoadHelper = new LyricLoadHelper();

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        initUI();
        initDB();
        initService();
        mMusicTimer = new MusicTimer(mHandler);

        initSDCard();
        initBroadcast();

        refreshVolBar();

        return view;
    }
    private View findViewById(int id){
        return MainView.findViewById(id);
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d("hehe", "MainActivity onResume");
//        if(i==0)
        refreshUI();

        if(!ivAlbum1.isRouting()) {
            ivAlbum0.beginRoute();
            ivAlbum1.beginRoute();
            ivAlbum2.beginRoute();
        }
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(ACTION_UPDATE_ALL);
//        intent.putExtra("style",3);
////        intent.putExtra("name",m.musicName);
////        intent.putExtra("album",m.albumId);
//        this.sendBroadcast(intent);
//
//
////        serviceManager.destory();
        getActivity().unregisterReceiver(sdCardReceiver);
        getActivity().unregisterReceiver(musicPlayBroadcast);
        getActivity().unregisterReceiver(volBroadcast);


        super.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onLongClick(View view){
        switch (view.getId()){
            case R.id.ib_vol:
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                refreshVolBar();
                if(sbVol.getVisibility() != View.VISIBLE){
                    changeVolBarVisible();
                }

                break;
            default:
                break;
        }
        return true;
    }

    private void finishApp(){


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        RemoteViews rv = new RemoteViews(getActivity().getPackageName(), R.layout.music_widget);
        rv.setViewVisibility(R.id.widget_ib_pause, View.GONE);
        rv.setViewVisibility(R.id.widget_ib_play, View.VISIBLE);
        rv.setTextViewText(R.id.widget_textview, "小鹏汽车");
        rv.setImageViewResource(R.id.widget_backage, R.raw.bg);

        SharedPreferences s = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        int id = s.getInt("id",0);
        appWidgetManager.updateAppWidget(id, rv);

        mMusicTimer.stopTimer();
        mMusicTimer = null;
        MusicApp.finishApp();
    }
    private void playMusic(){
        if(serviceManager.getCurMusic() == null){
            serviceManager.play(0);
            isPlaying=true;
        }
        else{
            serviceManager.rePlay();
            isPlaying=true;
        }

        ivAlbum0.beginRoute();
        ivAlbum1.beginRoute();
        ivAlbum2.beginRoute();
    }
    private void pauseMusic(){
        if(serviceManager.getCurMusic() == null) {
            return;
        }
        serviceManager.pause();
        isPlaying = false;
        ivAlbum0.stopRoute();
        ivAlbum1.stopRoute();
        ivAlbum2.stopRoute();
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_back:
//                Intent intent = new Intent(ACTION_UPDATE_ALL);
//                intent.putExtra("style",3);
////        intent.putExtra("name",m.musicName);
////        intent.putExtra("album",m.albumId);
//                MainActivity.this.sendBroadcast(intent);

//                finishApp();
//                finish();
                break;
            case R.id.ib_setting:

                break;
            case R.id.ib_prev:
                if(serviceManager.getCurMusic() == null) {
                    return;
                }
                serviceManager.prev();
                ivAlbum0.beginRoute();
                ivAlbum1.beginRoute();
                ivAlbum2.beginRoute();
                break;
            case R.id.ib_next:
                if(serviceManager.getCurMusic() == null) {
                    return;
                }
                serviceManager.next();
                ivAlbum0.beginRoute();
                ivAlbum1.beginRoute();
                ivAlbum2.beginRoute();
                break;
            case R.id.ib_play:
                playMusic();
                break;
            case R.id.ib_pause:
                pauseMusic();
                break;
            case R.id.ib_list:
                MusicListActivity.actionStart(getActivity(), this);
                break;
            case R.id.ib_mode:
                changeMode();
                break;
            case R.id.ib_love:
                changeLoveState();
                break;
            case R.id.ib_add2list:
                addToList();
                break;
            case R.id.ib_vol:
                changeVolBarVisible();
                break;
            default:
                break;
        }
    }

    public void refreshData(){
        serviceManager.refreshData();
    }

    private void addToList(){
        if(serviceManager.getCurMusic() == null){
            return;
        }

        AddToListActivty.actionStart(getActivity(), serviceManager.getCurMusic().songId, this);
    }

    public void changeLoveState(){
        if(serviceManager.getCurMusic() == null){
            return;
        }

        MusicInfo info = serviceManager.getCurMusic();

        if(!isLove){
            info.favorite = 1;
            musicUtils.setFavoriteState(info.songId, 1);
            ibLove.setBackgroundResource(loveDrawable[PIC_LOVE_PRESSED]);
        }else{
            info.favorite = 0;
            musicUtils.setFavoriteState(info.songId, 0);
            ibLove.setBackgroundResource(loveDrawable[PIC_LOVE]);
        }
        isLove = !isLove;
        serviceManager.refreshCurMusic(info);
    }

    private void changeVolBarVisible(){

        if(sbVol.getVisibility() == View.VISIBLE){
            sbVol.setVisibility(View.INVISIBLE);
            tvVolText.setVisibility(View.INVISIBLE);
            ivVolLabel.setVisibility(View.INVISIBLE);
        }else{
            refreshVolBar();
            sbVol.setVisibility(View.VISIBLE);
            tvVolText.setVisibility(View.VISIBLE);
            ivVolLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProcess) {
            if (!mPlayAuto) {
                mProgress = progress;
            }
        }

    }

    private void refreshVolBar(){
        int progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        if(progress == sbVol.getProgress()) return;

        sbVol.setProgress(progress);

        float textHeight = sbVol.getBottom() - sbVol.getTop() - 36;

        int pos = new Float(textHeight - textHeight * progress / maxVolume).intValue();
        RelativeLayout.LayoutParams olp=(RelativeLayout.LayoutParams) flVol.getLayoutParams();
        olp.setMargins(0, pos, 0, 0);
        flVol.setLayoutParams(olp);
//        flVol.setTop(pos);
        tvVolText.setText("" + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProcess) {
            mPlayAuto = false;
            mMusicTimer.stopTimer();
            serviceManager.pause();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProcess) {
            mPlayAuto = true;
            serviceManager.seekTo(mProgress);
            refreshSeekProgress(serviceManager.position(),
                    serviceManager.duration());
            serviceManager.rePlay();
            mMusicTimer.startTimer();
        }
    }

    private void initUI(){
        ibBack      = (ImageButton) findViewById(R.id.ib_back);
        ibSetting   = (ImageButton) findViewById(R.id.ib_setting);
        ibPrev      = (ImageButton) findViewById(R.id.ib_prev);
        ibNext      = (ImageButton) findViewById(R.id.ib_next);
        ibPlay      = (ImageButton) findViewById(R.id.ib_play);
        ibPause     = (ImageButton) findViewById(R.id.ib_pause);
        ibLrc       = (ImageButton) findViewById(R.id.ib_list);
        ibMode      = (ImageButton) findViewById(R.id.ib_mode);
        ibLove      = (ImageButton) findViewById(R.id.ib_love);
        ibAdd2List  = (ImageButton) findViewById(R.id.ib_add2list);
        ibVol       = (ImageButton) findViewById(R.id.ib_vol);

        initTv();

        ivVolLabel  = (ImageView)findViewById(R.id.iv_vol_label);
        flVol       = (FrameLayout) findViewById(R.id.vol_text_layout);
        ibBackage   = (ImageView) findViewById(R.id.main_backage);


        if(tvLrcPrev != null && tvLrcNow != null && tvLrcNext != null && mLyricLoadHelper != null){
            lrcView     = new LrcViewManager(tvLrcPrev, tvLrcNow, tvLrcNext, mLyricLoadHelper);
        }
        sbVol = (VerticalSeekBar) findViewById(R.id.sb_vol);
        sbVol.setMax(maxVolume);
        sbVol.setProgress(currVolume);
        sbVol.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(VerticalSeekBar VerticalSeekBar, int progress,
                                          boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
//                sbVol.setProgress(progress);
                refreshVolBar();

            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {

            }
        });
        tvVolText.setText("" + currVolume);
        changeVolBarVisible();

        sbProcess   = (SeekBar) findViewById(R.id.sb_process);
        sbProcess.setOnSeekBarChangeListener(this);

        viewPager = (ViewPager) findViewById(R.id.iv_pageview);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view1 = inflater.inflate(R.layout.iv_album0,null);
        view2 = inflater.inflate(R.layout.iv_album0,null);
        view3 = inflater.inflate(R.layout.iv_album0,null);
        ivAlbum0 = (CircleImageView) view1.findViewById(R.id.iv_album0);
        ivAlbum1 = (CircleImageView) view2.findViewById(R.id.iv_album0);
        ivAlbum2 = (CircleImageView) view3.findViewById(R.id.iv_album0);
        ivAlbum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying)pauseMusic();
                else playMusic();
            }
        });
        viewList = new ArrayList<>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
//        ivAlbum = (CircleImageView) findViewById(R.id.iv_album0);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);



        mDefaultAlbumIcon = BitmapFactory.decodeResource(
                getActivity().getResources(), R.drawable.default_album);

        ibBack.setOnClickListener(this);
        ibSetting.setOnClickListener(this);
        ibPrev.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibPlay.setOnClickListener(this);
        ibPause.setOnClickListener(this);
        ibLrc.setOnClickListener(this);
        ibMode.setOnClickListener(this);
        ibLove.setOnClickListener(this);
        ibAdd2List.setOnClickListener(this);
        ibVol.setOnClickListener(this);

        ibVol.setOnLongClickListener(this);

        ibPause.setVisibility(View.INVISIBLE);
        ibPlay.setVisibility(View.VISIBLE);

        refreshVolBar();


        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("viewPager", "onPageSelectedyyyyyyyyy" + position);
                switch (position) {
                    case 0:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(400);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                myHandler.sendEmptyMessage(0);
                            }
                        }).start();

                        break;
                    case 1:

                        break;
                    case 2:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(400);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                myHandler.sendEmptyMessage(2);
                            }
                        }).start();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("onPageScrollStateChanged","state: "+state);
                switch (state){
                    case 1:
                        ivAlbum1.stopRoute();
                        ivAlbum0.stopRoute();
                        ivAlbum2.stopRoute();
                        break;
                    case 0:
                        if(!ivAlbum1.isRouting()) {
                            ivAlbum0.beginRoute();
                            ivAlbum1.beginRoute();
                            ivAlbum2.beginRoute();
                        }
                        break;
                    default:

                        break;
                }
            }
        });
    }
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (serviceManager.getCurMusic() == null) {
                        return;
                    }
                    mChangeStatu = false;
                    serviceManager.prev();
                    ivAlbum0.beginRoute();
                    ivAlbum1.beginRoute();
                    ivAlbum2.beginRoute();
//                    viewPager.setCurrentItem(1);
//                    viewPager.setCurrentItem(1,false);
                    break;
                case 2:
                    if (serviceManager.getCurMusic() == null) {
                        return;
                    }
                    mChangeStatu = true;
                    serviceManager.next();
                    isPlaying=true;
                    ivAlbum0.beginRoute();
                    ivAlbum1.beginRoute();
                    ivAlbum2.beginRoute();
//                    viewPager.setCurrentItem(1,false);
                    break;
                case 3:
                    Bitmap b1 = (Bitmap) msg.obj;
                    ivAlbum2.setImageBitmap(b1);
                    break;
                case 4:
                    Bitmap b2 = (Bitmap) msg.obj;
                    ivAlbum0.setImageBitmap(b2);
                    break;


                default:

                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void initTv(){
        tvSong      = (TextView) findViewById(R.id.tv_song);
        tvSinger    = (TextView) findViewById(R.id.tv_singer);
        tvNowTime   = (TextView) findViewById(R.id.tv_now_time);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);

        tvLrcPrev   = (TextView) findViewById(R.id.lrc_prev);
        tvLrcNow    = (TextView) findViewById(R.id.lrc_now);
        tvLrcNext   = (TextView) findViewById(R.id.lrc_next);

        tvVolText   = (TextView) findViewById(R.id.vol_text);

//        StringHelper.setFont(tvSong);
//        StringHelper.setFont(tvSinger);
//        StringHelper.setFont(tvNowTime);
//        StringHelper.setFont(tvTotalTime);
//        StringHelper.setFont(tvVolText);
//        StringHelper.setFont(tvLrcPrev);
//        StringHelper.setFont(tvLrcNow);
//        StringHelper.setFont(tvLrcNext);
    }

    private void initSDCard(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        getActivity().registerReceiver(sdCardReceiver, intentFilter);
    }

    private void initDB(){
        if(musicUtils == null){
            musicUtils = new MusicUtils();
        }
    }

    private void initService(){
        MusicApp.mServiceManager.connectService();
        MusicApp.mServiceManager.setOnServiceConnectComplete(this);
        serviceManager = MusicApp.mServiceManager;
    }

    private void initBroadcast(){
        musicPlayBroadcast = new MusicPlayBroadcast();
        IntentFilter filterMusic = new IntentFilter(BROADCAST_NAME);
        filterMusic.addAction(BROADCAST_NAME);
        getActivity().registerReceiver(musicPlayBroadcast, filterMusic);

        volBroadcast = new MusicVolBroadcast() ;
        IntentFilter filterVol = new IntentFilter() ;
        filterVol.addAction("android.media.VOLUME_CHANGED_ACTION") ;
        getActivity().registerReceiver(volBroadcast, filterVol) ;
    }

    private final BroadcastReceiver sdCardReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(action.equals("android.intent.action.MEDIA_REMOVED")
//                    || action.equals("android.intent.action.MEDIA_UNMOUNTED")
                    || action.equals("android.intent.action.MEDIA_BAD_REMOVAL")
                    || action.equals("android.intent.action.MEDIA_SHARED"))
            {
                serviceManager.pause();
//                serviceManager.reQueryData();
                Toast.makeText(getActivity(), R.string.sdcard_removed,
                        Toast.LENGTH_SHORT).show();
            } else if(action.equals("android.intent.action.MEDIA_MOUNTED")){

                Dialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("检测到SD卡插入，是否检索其中的音乐？")
                        .setIcon(R.drawable.ic_refresh_black)
                        .setPositiveButton("检测", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ProgressDialog mProgress = new ProgressDialog(getActivity());
                                mProgress.setMessage("正在扫描");
                                mProgress.setCancelable(false);
                                mProgress.setCanceledOnTouchOutside(false);
                                mProgress.show();
                                serviceManager.reQueryData();
                                mProgress.dismiss();
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
            }
        }
    };

    @Override
    public void onServiceConnectComplete(IMediaService service){
        curMode = serviceManager.getPlayMode();
        ibMode.setBackgroundResource(modeDrawable[curMode]);

    }

    private void changeMode(){
        curMode++;
        if(curMode > MPM_RANDOM_PLAY){
            curMode = MPM_LIST_LOOP_PLAY;
        }
        serviceManager.setPlayMode(curMode);
        ibMode.setBackgroundResource(modeDrawable[curMode]);
    }

    public void refreshUI(){
        if(serviceManager.getPlayState() == MPS_INVALID){
            refreshUI(0, 0, null);
        }else{
            refreshUI(serviceManager.position(), serviceManager.duration(), serviceManager.getCurMusic());
        }
    }

    public void refreshUI(int curTime, int totalTime, MusicInfo music){
        if(music == null) {
            resetUI();
            return;
        }

        refreshSeekProgress(curTime, totalTime);
        refreshText(music, totalTime);
        refreshAlbumPic(music.albumId);
        refreshLrcView(music, curTime);
        refreshVolBar();
        refreshButtons();
    }

    private void refreshText(MusicInfo music, int totalTime){
        tvSong.setText(music.musicName);
        tvSinger.setText(music.artist);

        totalTime /= 1000;
        int totalMinute = totalTime / 60;
        int totalSecond = totalTime % 60;
        String totalTimeString = String.format("%02d:%02d", totalMinute, totalSecond);
        tvTotalTime.setText(totalTimeString);
    }

    private void refreshButtons(){
        curMode = serviceManager.getPlayMode();
        ibMode.setBackgroundResource(modeDrawable[curMode]);

        if(serviceManager.getPlayState() == MPS_PLAYING){
            mMusicTimer.startTimer();
            ibPlay.setVisibility(View.INVISIBLE);
            ibPause.setVisibility(View.VISIBLE);
        }else{
            mMusicTimer.stopTimer();
            ibPause.setVisibility(View.INVISIBLE);
            ibPlay.setVisibility(View.VISIBLE);
        }

        if(serviceManager.getCurMusic().favorite == 1){
            isLove = true;
            ibLove.setBackgroundResource(loveDrawable[PIC_LOVE_PRESSED]);
        }else{
            isLove = false;
            ibLove.setBackgroundResource(loveDrawable[PIC_LOVE]);
        }
    }

    private void resetUI(){
        tvSong.setText(R.string.song_name);
        tvSinger.setText(R.string.singer_name);
        tvNowTime.setText(R.string.default_time);
        tvTotalTime.setText(R.string.default_time);
        sbProcess.setProgress(0);
        lrcView.clear();
        if(thrandBitMap == null || !thrandBitMap.isRunning()) {
            thrandBitMap = new ThrandBitMap(BitmapHandler, mDefaultAlbumIcon, getActivity());
            thrandBitMap.start();
        }

//        ibBackage.setImageBitmap(FastBlur.blurBitmap(mDefaultAlbumIcon.copy(mDefaultAlbumIcon.getConfig(), true), this));
//        ibBackage.setImageBitmap(FastBlur.doBlur(mDefaultAlbumIcon,10,true));
        ivAlbum1.setImageBitmap(mDefaultAlbumIcon);


        refreshVolBar();

    }

    private void refreshLrcView(MusicInfo music, int curTime){
        String lyricFilePath = MusicApp.lrcPath + "/" +
                music.artist + " - " +
                music.musicName
                + ".lrc";
        mLyricLoadHelper.loadLyric(lyricFilePath);
        lrcView.clear();
        if(mLyricLoadHelper.ismHasLyric()){
            lrcView.setLyric(mLyricLoadHelper.getmLyricSentences());
            lrcView.updateIndex(curTime);
        }
    }
    private void refreshAlbumPic(int albumId){
        Bitmap bitmap = MusicUtils.getCachedArtwork(getActivity(), albumId, mDefaultAlbumIcon);
        ivAlbum1.setImageBitmap(bitmap);
        viewPager.setCurrentItem(1,false);

        if(serviceManager.getNextId()!=-1) {
//            Bitmap bitmap1 = MusicUtils.getCachedArtwork(MainActivity.this, serviceManager.getNextId(), mDefaultAlbumIcon);
//            ivAlbum2.setImageBitmap(bitmap1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = myHandler.obtainMessage();
                    message.what = 3;
                    message.obj = MusicUtils.getCachedArtwork(getActivity(), serviceManager.getNextId(), mDefaultAlbumIcon);
                    myHandler.sendMessage(message);
                }
            }).start();
        }
        if(serviceManager.getPreId()!=-1) {
//            Bitmap bitmap2 = MusicUtils.getCachedArtwork(MainActivity.this, serviceManager.getPreId(), mDefaultAlbumIcon);
//            ivAlbum0.setImageBitmap(bitmap2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = myHandler.obtainMessage();
                    message.what = 4;
                    message.obj = MusicUtils.getCachedArtwork(getActivity(), serviceManager.getPreId(), mDefaultAlbumIcon);
                    myHandler.sendMessage(message);
                }
            }).start();
        }


//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.bg);

//        ibBackage.setImageBitmap(FastBlur.blurBitmap(bitmap.copy(bitmap.getConfig(), true), this));
        if(thrandBitMap == null || !thrandBitMap.isRunning()) {

            thrandBitMap = new ThrandBitMap(BitmapHandler, bitmap, getActivity());
            thrandBitMap.start();

        }
    }

    private Handler BitmapHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ibBackage.setImageBitmap((Bitmap)msg.obj);
            super.handleMessage(msg);
        }
    };

    private void refreshSeekProgress(int curTime, int totalTime) {

        curTime /= 1000;
        totalTime /= 1000;
        int curminute = curTime / 60;
        int cursecond = curTime % 60;
        int totalSecond = totalTime % 60;
        cursecond = cursecond > totalSecond ? totalSecond : cursecond;

        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        tvNowTime.setText(curTimeString);

        int rate = 0;
        if (totalTime != 0) {
            rate = (int) ((float) curTime / totalTime * 100);
        }
        sbProcess.setProgress(rate);
    }

    private class MusicVolBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果音量发生变化则更改seekbar的位置
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                refreshVolBar();
            }
        }
    }

    private class MusicPlayBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if(intent.getAction().equals(BROADCAST_NAME)){
                MusicInfo music = serviceManager.getCurMusic();
                int playState = intent.getIntExtra(PLAY_STATE_NAME, MPS_NOFILE);
                if(music == null) return;

                switch(playState){
                    case MPS_INVALID:
                        refreshUI(0, 0, null);
                        musicUtils.deleteMusicInfo(music);
                        refreshData();
                        break;
                    case MPS_PAUSE:
                        refreshUI(serviceManager.position(), serviceManager.duration(), music);
                        break;
                    case MPS_PLAYING:
                        refreshUI(serviceManager.position(), serviceManager.duration(), music);
                        break;
                    case MPS_PREPARE:
                        refreshUI(0, serviceManager.duration(), music);
                        break;
                }
            }
        }
    }

    class ThrandBitMap extends Thread{
        Handler handler ;
        Bitmap b;
        Context context;
        boolean flag;
        ThrandBitMap(Handler handler,Bitmap b,Context context){
            this.flag = false;
            this.handler = handler;
//               FastBlur.blurBitmap(bitmap.copy(bitmap.getConfig(), true), this)
            this.b = b;
            this.context = context;
        }

        @Override
        public void run() {
            flag = true;
            Bitmap bx = FastBlur.blurBitmap(b.copy(b.getConfig(), true), context);
            Message m = handler.obtainMessage();
            m.obj = bx;
            handler.sendMessage(m);
            this.flag = false;
            super.run();
        }

        public boolean isRunning() {
            return flag;
        }
    }


}
