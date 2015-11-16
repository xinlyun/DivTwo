package com.lin.dlivkfragment.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.view.RouteOverLay;
import com.lin.dlivkfragment.MainActivity;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.interfaces.IWindowControl;
import com.lin.dlivkfragment.util.TTSController;
import com.lin.dlivkfragment.util.Utils;

/**
 * Created by xinlyun on 15-11-14.
 */
public class NaviCustomActivity extends Fragment implements
        AMapNaviViewListener,AMapNaviListener,IWindowControl{
    private FragmentTransaction transaction ;
    private FragmentManager fragmentManager;
    private void startFragment(Class<?> fragment){
        fragmentManager = getActivity().getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        try {
            Fragment fragment1 = (Fragment) fragment.newInstance();
            transaction.remove(this);
            transaction.replace(R.id.fragment1,fragment1);
            transaction.commit();

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //---
    private AMap aMap;
    private LinearLayout mAmapAMapNaviView_layout;
    private AMapNaviView mAmapAMapNaviView;
    // 导航可以设置的参数
    private boolean mDayNightFlag = Utils.DAY_MODE;// 默认为白天模式
    private boolean mDeviationFlag = Utils.YES_MODE;// 默认进行偏航重算
    private boolean mJamFlag = Utils.YES_MODE;// 默认进行拥堵重算
    private boolean mTrafficFlag = Utils.OPEN_MODE;// 默认进行交通播报
    private boolean mCameraFlag = Utils.OPEN_MODE;// 默认进行摄像头播报
    private boolean mScreenFlag = Utils.YES_MODE;// 默认是屏幕常亮
    private String TAG= "NaviCustom";
    // 导航界面风格
    private int mThemeStle;
    // 导航监听
    private AMapNaviListener mAmapNaviListener;
    private AMapNavi mAMapNavi;
    TextView tv ;
    private TextView hourtext,mintext,distancetext,hourtextshow;

    private ImageView btnExit;
    private ImageView mNaviBtn;
    private float zoom = 19.0f;
    private AMapNaviViewOptions viewOptions;
    private SharedPreferences s ;
    private ImageView mNavi_show_image;
    private TextView mNavi_show_length,mNavi_show_time,mNavi_show_road;
    private View MainView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_navicustom, container, false);
        MainView = view;
        s = getActivity().getSharedPreferences("mapsetting", Activity.MODE_PRIVATE);
        ((MainActivity)getActivity()).setWindowControl(this);
        if(s.getBoolean("yuyin",true))
            //语音播报开始
            TTSController.getInstance(getActivity()).startSpeaking();
//        Bundle bundle =getActivity().getIntent().getBundleExtra()
//        if(bundle!=null){
//            int style = bundle.getInt("style",0);
//            if(style == 1){
//                // 设置模拟速度
//                AMapNavi.getInstance(getActivity()).setEmulatorNaviSpeed(100);
//                // 开启模拟导航
//                AMapNavi.getInstance(getActivity()).startNavi(AMapNavi.EmulatorNaviMode);
//            }
//        }else
        // 实时导航方式进行导航
        AMapNavi.getInstance(getActivity()).startNavi(AMapNavi.GPSNaviMode);


        mAMapNavi = AMapNavi.getInstance(getActivity());
//		hourtext = (TextView) findViewById(R.id.navi_text_hour);
//		mintext = (TextView) findViewById(R.id.navi_text_min);
//		distancetext = (TextView) findViewById(R.id.navi_text_distance);
        hourtext = (TextView) findViewById(R.id.navihour);
        mintext = (TextView) findViewById(R.id.navimin);
        distancetext = (TextView) findViewById(R.id.navilength);
        hourtextshow = (TextView) findViewById(R.id.navihour_show);
        mNavi_show_image = (ImageView) findViewById(R.id.navi_show_navi);
        mNavi_show_length = (TextView) findViewById(R.id.navi_show_length);
        mNavi_show_time = (TextView) findViewById(R.id.navi_show_time);
        mNavi_show_road = (TextView) findViewById(R.id.navi_show_road);
        initView();
        mAMapNavi.resumeNavi();
        return view;
    }
    private View findViewById(int id){
        return MainView.findViewById(id);
    }
    private void initView() {

        btnExit = (ImageView) findViewById(R.id.navi_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMapNavi.getInstance(getActivity()).destroy();
//                new NaviStart3Activity((Activity)getActivity(),NaviCustomActivity.this).onStart(0);
//                FloatManager.getActivity(getActivity()).startWindows(NaviCustomActivity.this,NaviStart3Activity.class);
                startFragment(NaviStart3Activity.class);
            }
        });
        mNaviBtn = (ImageView) findViewById(R.id.navi_image_navi);
        mNaviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//				viewOptions.setCompassEnabled(false);
//				mAmapAMapNaviView.setViewOptions(viewOptions);
                mAmapAMapNaviView.onTouch(null);
                AMapNaviPath naviPath = mAMapNavi.getNaviPath();
                RouteOverLay mRouteOverLay = new RouteOverLay(aMap, null);
                mRouteOverLay.setRouteInfo(naviPath);
                mRouteOverLay.zoomToSpan();
            }
        });
//		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.customnavimap);
        mAmapAMapNaviView_layout = (LinearLayout) findViewById(R.id.customnavimap_group);
        mAmapAMapNaviView = new AMapNaviView(getActivity());
        mAmapAMapNaviView_layout.addView(mAmapAMapNaviView);
        mAmapAMapNaviView.onCreate(((Activity)getActivity()).getIntent().getExtras());
//        mAmapAMapNaviView.onCreate(null);
        aMap = mAmapAMapNaviView.getMap();

        setAmapNaviViewOptions();
//		mAMapNavi.setAMapNaviListener(getAMapNaviListener());
        mAMapNavi.setAMapNaviListener(this);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
        mAmapAMapNaviView.onResume();

    }

    /**
     * 设置导航的参数
     */
    private void setAmapNaviViewOptions() {
        if (mAmapAMapNaviView == null) {
            return;
        }
        viewOptions = new AMapNaviViewOptions();
        viewOptions.setSettingMenuEnabled(true);// 设置导航setting可用
        viewOptions.setLayoutVisible(false);
        viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
        viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
        viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
        viewOptions.setTrafficInfoUpdateEnabled(s.getBoolean("lukuang", true));// 设置是否更新路况
        viewOptions.setCameraInfoUpdateEnabled(s.getBoolean("dianzigou", true));// 设置摄像头播报
        viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
        viewOptions.setNaviViewTopic(1);// 设置导航界面主题样式
        viewOptions.setLeaderLineEnabled(1248);
        viewOptions.setRouteListButtonShow(true);
        viewOptions.setTrafficLayerEnabled(s.getBoolean("lukuang", true));
        viewOptions.setTrafficBarEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setCompassEnabled(true);
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
//		Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.ic_launcher);
        viewOptions.setMonitorCameraEnabled(s.getBoolean("dianzigou",true));

//		viewOptions.setWayPointBitmap(bitmap);
//		viewOptions.setMonitorCamerBitmap(bitmap);

        mAmapAMapNaviView.setViewOptions(viewOptions);

    }

    private AMapNaviListener getAMapNaviListener() {
//		if (mAmapNaviListener == null) {

        mAmapNaviListener = new AMapNaviListener() {

            @Override
            public void onTrafficStatusUpdate() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartNavi(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onReCalculateRouteForYaw() {
                // 可以在频繁重算时进行设置,例如五次之后
                // i++;
                // if (i >= 5) {
                // AMapNaviViewOptions viewOptions = new
                // AMapNaviViewOptions();
                // viewOptions.setReCalculateRouteForYaw(false);
                // mAmapAMapNaviView.setViewOptions(viewOptions);
                // }
            }

            @Override
            public void onReCalculateRouteForTrafficJam() {

            }

            @Override
            public void onLocationChange(AMapNaviLocation location) {

            }

            @Override
            public void onInitNaviSuccess() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onInitNaviFailure() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGetNavigationText(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onEndEmulatorNavi() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCalculateRouteSuccess() {

            }

            @Override
            public void onCalculateRouteFailure(int arg0) {

            }

            @Override
            public void onArrivedWayPoint(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onArriveDestination() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onGpsOpenStatus(boolean arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNaviInfoUpdated(AMapNaviInfo arg0) {
                // TODO Auto-generated method stub
                System.out.println("hour:" + arg0.getPathRemainTime() + "  distance:" + arg0.getPathRemainDistance());
//				hourtext.setText("" + arg0.getPathRemainTime() / 60 / 60);
//
//				mintext.setText(""+(arg0.getPathRemainTime()/60)%60);
//				distancetext.setText(""+((float)arg0.getPathRemainDistance())/1000);
                if(arg0.getPathRemainTime()/60/60==0){
                    hourtext.setVisibility(View.GONE);
                    hourtextshow.setVisibility(View.GONE);
                }else {
                    hourtext.setText("" + arg0.getPathRemainTime() / 60 / 60);
                }
                mintext.setText(""+(arg0.getPathRemainTime()/60)%60);
                distancetext.setText(""+((float)arg0.getPathRemainDistance())/1000);

            }

            @Override
            public void onNaviInfoUpdate(NaviInfo arg0) {

                // TODO Auto-generated method stub
                tv.setText("Road:"+arg0.getCurrentRoadName()+" CurLink:"
                        +arg0.getCurLink()+" Direction"+arg0.getDirection()+
                        " CurStepDistance"+arg0.getCurStepRetainDistance());

            }
        };
//		}
        return mAmapNaviListener;
    }

    // -------处理
    /**
     * 导航界面返回按钮监听
     * */
    @Override
    public void onNaviCancel() {
//		Intent intent = new Intent(NaviCustomActivity.this,
//				MainStartActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);
//		finish();
//		new NaviStartActivity((Activity)getActivity(),this).onStart(0);
        Toast.makeText(getActivity(), "CLick", Toast.LENGTH_SHORT).show();

    }

    /**
     * 点击设置按钮的事件
     */
    @Override
    public void onNaviSetting() {
//		Bundle bundle = new Bundle();
//		bundle.putInt(Utils.THEME, mThemeStle);
//		bundle.putBoolean(Utils.DAY_NIGHT_MODE, mDayNightFlag);
//		bundle.putBoolean(Utils.DEVIATION, mDeviationFlag);
//		bundle.putBoolean(Utils.JAM, mJamFlag);
//		bundle.putBoolean(Utils.TRAFFIC, mTrafficFlag);
//		bundle.putBoolean(Utils.CAMERA, mCameraFlag);
//		bundle.putBoolean(Utils.SCREEN, mScreenFlag);
//		Intent intent = new Intent(NaviCustomActivity.this,
//				NaviSettingActivity.class);
//		intent.putExtras(bundle);
//		startActivity(intent);

    }

    @Override
    public void onNaviMapMode(int arg0) {

    }
    @Override
    public void onNaviTurnClick() {


    }

    @Override
    public void onNextRoadClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScanViewButtonClick() {
        // TODO Auto-generated method stub

    }
    private void processBundle(Bundle bundle) {

        if (bundle != null) {
            mDayNightFlag = bundle.getBoolean(Utils.DAY_NIGHT_MODE,
                    mDayNightFlag);
            mDeviationFlag = bundle.getBoolean(Utils.DEVIATION, mDeviationFlag);
            mJamFlag = bundle.getBoolean(Utils.JAM, mJamFlag);
            mTrafficFlag = bundle.getBoolean(Utils.TRAFFIC, mTrafficFlag);
            mCameraFlag = bundle.getBoolean(Utils.CAMERA, mCameraFlag);
            mScreenFlag = bundle.getBoolean(Utils.SCREEN, mScreenFlag);
            mThemeStle = bundle.getInt(Utils.THEME);

        }
    }


    @Override
    public void onLockMap(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        super.onPause();
//		mAMapNavi.pauseNavi();
        mAmapAMapNaviView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//		mAMapNavi.resumeNavi();
        mAmapAMapNaviView.onResume();
    }
    //-----------------------------------------------------

    /**
     * 导航回调函数
     *
     * @return
     */
    @Override
    public void onInitNaviFailure() {
        Log.d(TAG, "onInitNaviFailure");
    }

    @Override
    public void onInitNaviSuccess() {
        Log.d(TAG,"onInitNaviSuccess");
    }

    @Override
    public void onStartNavi(int i) {
        Log.d(TAG,"onStartNavi");
    }

    @Override
    public void onTrafficStatusUpdate() {
        Log.d(TAG,"onTrafficStatusUpdate");
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        Log.d(TAG,"onLocationChange");
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        Log.d(TAG,"onGetNavigationText");
    }

    @Override
    public void onEndEmulatorNavi() {
        Log.d(TAG,"onEndEmulatorNavi");
    }

    @Override
    public void onArriveDestination() {
        Log.d(TAG,"onArriveDestination");
    }

    @Override
    public void onCalculateRouteSuccess() {
        Log.d(TAG,"onCalculateRouteSuccess");
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        Log.d(TAG,"onCalculateRouteFailure");
    }

    @Override
    public void onReCalculateRouteForYaw() {
        Log.d(TAG,"onReCalculateRouteForYaw");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        Log.d(TAG,"onReCalculateRouteForTrafficJam");
    }

    @Override
    public void onArrivedWayPoint(int i) {
        Log.d(TAG,"onArrivedWayPoint");
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        Log.d(TAG,"onGpsOpenStatus");
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

        Log.d(TAG,"onNaviInfoUpdated");
//		hourtext.setText("" + aMapNaviInfo.getPathRemainTime() / 60 / 60);
//
//		mintext.setText(""+(aMapNaviInfo.getPathRemainTime()/60)%60);
//		distancetext.setText("" + ((float) aMapNaviInfo.getPathRemainDistance())/1000);


        if(aMapNaviInfo.getPathRemainTime()/60/60==0){
            hourtext.setVisibility(View.GONE);
            hourtextshow.setVisibility(View.GONE);
        }else {
            hourtext.setText("" + aMapNaviInfo.getPathRemainTime() / 60 / 60);
        }
        mintext.setText(""+(aMapNaviInfo.getPathRemainTime()/60)%60);
        distancetext.setText(""+((float)aMapNaviInfo.getPathRemainDistance())/1000);
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        Log.d(TAG, "onNaviInfoUpdate");
        mNavi_show_road.setText(naviInfo.getCurrentRoadName());
        mNavi_show_time.setText("剩余 "+(naviInfo.getCurStepRetainTime()/60+1)+"分");
        mNavi_show_road.setText(naviInfo.getCurrentRoadName());
        mNavi_show_length.setText(""+naviInfo.getCurStepRetainDistance()+"M");
        if(naviInfo.getIconType()<16)
            mNavi_show_image.setImageResource(icons[naviInfo.getIconType()]);
    }

    static int[] icons={
            R.drawable.sou0,R.drawable.sou0,R.drawable.sou2,R.drawable.sou3,
            R.drawable.sou4,R.drawable.sou5,R.drawable.sou6,R.drawable.sou7,
            R.drawable.sou8,R.drawable.sou9,R.drawable.sou10,R.drawable.sou11,
            R.drawable.sou12,R.drawable.sou13,R.drawable.sou14,R.drawable.sou15
    };
    boolean bmFlag=true;
    @Override
    public void dismiss() {
        if(bmFlag) {
//            findViewById(R.id.false_map1).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.false_map)).setVisibility(View.VISIBLE);

            mAmapAMapNaviView.onPause();
//            mAmapAMapNaviView.onLowMemory();
            mAmapAMapNaviView.setVisibility(View.GONE);
            bmFlag = false;
        }
    }

    @Override
    public void reshow() {

        mAmapAMapNaviView.onResume();
        mAmapAMapNaviView.setVisibility(View.VISIBLE);
//        mMapView.onResume();
        ((ImageView) findViewById(R.id.false_map)).setVisibility(View.GONE);
//            findViewById(R.id.false_map1).setVisibility(View.GONE);
        bmFlag = true;
    }
}





