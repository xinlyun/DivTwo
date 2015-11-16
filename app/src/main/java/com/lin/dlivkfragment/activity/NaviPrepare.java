package com.lin.dlivkfragment.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.gc.materialdesign.widgets.Dialog;
import com.lin.dlivkfragment.R;
import com.lin.myfloatactionbtn.SystemBarTintManager;
import com.lin.myfloatactionbtn.swipeback.SwipeBackActivity;
import com.lin.myfloatactionbtn.swipeback.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinlyun on 15-10-15.
 */
public class NaviPrepare extends SwipeBackActivity implements View.OnClickListener
        ,AdapterView.OnItemClickListener
{
    AlertDialog.Builder alertdialog;
    private TextView mStrategyText;// 行车策略输入
    private String[] mStrategyMethods;
    private String[] mPositionMethods;
    private ImageView mReturnBtn;
    private static final int SET_NOWPOSI=1,SET_POSI=2,SET_HOME=3,SET_COMPANY=4;
    //    naviprepare_nowposi
//            naviprepare_destination
//    naviprepare_home
//            naviprepare_company
    private TextView nowposi,destination, mHome, mCompany;
    private ImageView mHomeSet,mCompanySet,mChelueSet;
    private ListView mHistoryLv;
    private Button mBtn_gone;
    private Button mBtn_clean;
    private Bundle bundle;
    private LatLonPoint mLocation = null;
    private LatLonPoint mPosi = null,mHomePosi = null,mCompanyPosi = null;

    private  Intent intent;
    private List<NaviLatLng> startPoint, endPoint, wayPoint;
    private AMapNavi mapNavi;
    private SharedPreferences.Editor editor;
    private List<NaviLatLng> mHistoryNaviPosi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("NaviPrepare", "onCreate");
        //--以下这段代码设置基本的浸入式状态栏----
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_naviprepare);

        /**需要在主布局里设置
         * android:fitsSystemWindows="true"
         * android:clipToPadding="false"
         * 以保证toolbar和状态栏有所距离
         */
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#182d4e"));


        //------------------------------------
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);

        initResources();
        initView();
        initListener();


        Intent intent = getIntent();
        bundle = intent.getBundleExtra("myown");
        if (bundle == null)
            bundle = new Bundle();
        else {
            mLocation = bundle.getParcelable("myown");
            if (mLocation == null) {
                SharedPreferences s = getSharedPreferences("myown", MODE_PRIVATE);
                mLocation = new LatLonPoint(s.getFloat("SeachNeighX", 0), s.getFloat("SeachNeighY", 0));
                bundle.putParcelable("myown", mLocation);
            }
            int style = bundle.getInt("style", 0);
            switch (style) {
                case 0:
                    nowposi.setText("当前位置");
                    break;
                case 1:
                    nowposi.setText(bundle.getString("myname", "当前位置"));
                    break;
                case 2:
                    nowposi.setText(bundle.getString("myname", "当前位置"));
                    destination.setText(bundle.getString("posiname", "终点"));
                    destination.setTextColor(getResources().getColor(R.color.naviblue));
                    mBtn_gone.setTextColor(getResources().getColor(R.color.naviblue));
                    mPosi = bundle.getParcelable("posi");

                    break;
            }
        }

    }
    private void initView(){

        mStrategyText = (TextView) findViewById(R.id.navi_strategy_edit);
//        mStrategyText.setDropDownBackgroundResource(R.drawable.whitedownborder);
//        mStrategyText.setInputType(InputType.TYPE_NULL);
//        ArrayAdapter<String> strategyAdapter = new ArrayAdapter<String>(this,
//                R.layout.strategy_inputs, mStrategyMethods);
//        mStrategyText.setAdapter(strategyAdapter);

        mChelueSet  = (ImageView) findViewById(R.id.naviprepare_celue_set);
        nowposi     = (TextView) findViewById(R.id.naviprepare_nowposi);
        destination = (TextView) findViewById(R.id.naviprepare_destination);
        mHome       = (TextView) findViewById(R.id.naviprepare_home);
        mHomeSet    = (ImageView) findViewById(R.id.naviprepare_home_set);
        mCompany    = (TextView) findViewById(R.id.naviprepare_company);
        mCompanySet = (ImageView) findViewById(R.id.naviprepare_company_set);
        mBtn_gone   = (Button)   findViewById(R.id.naviprepare_goon);
        mHistoryLv  = (ListView) findViewById(R.id.naviprepare_history);
        mBtn_clean  = (Button) findViewById(R.id.naviprepare_cleanhistory);
        initListView(this);
        SharedPreferences s = getSharedPreferences("myown",MODE_PRIVATE);
        editor = s.edit();
        mHome.setText(s.getString("home", "家"));
        mCompany.setText(s.getString("company", "公司"));
        if(!mHome.getText().equals("家")){
            mHomePosi = new LatLonPoint(s.getFloat("homex",0),s.getFloat("homey",0));
        }
        if(!mCompany.getText().equals("公司")){
            mCompanyPosi = new LatLonPoint(s.getFloat("companyx",0),s.getFloat("companyy",0));
        }
        mReturnBtn = (ImageView) findViewById(R.id.btn_return_bar);
        ((TextView)findViewById(R.id.btn_return_bar_title)).setText("导航准备");


    }

    private void initListView(Context context){
        List<HistoryPosi> historyPosis= DBHelper.getHistory(context);
        mHistoryNaviPosi = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        for(HistoryPosi historyPosi:historyPosis){
            strings.add(historyPosi.getName());
            mHistoryNaviPosi.add(new NaviLatLng(historyPosi.getX(),historyPosi.getY()));
        }
        ArrayAdapter adapter = new ArrayAdapter(context,R.layout.mysimple_listitem,strings);
        mHistoryLv.setAdapter(adapter);
    }
    private void initListener(){
        mStrategyText   .setOnClickListener(this);
        nowposi         .setOnClickListener(this);
        destination     .setOnClickListener(this);
        mBtn_gone       .setOnClickListener(this);

        mHome           .setOnClickListener(this);
        mHomeSet        .setOnClickListener(this);
        mCompany        .setOnClickListener(this);
        mCompanySet     .setOnClickListener(this);
        mHistoryLv      .setOnItemClickListener(this);
        mBtn_clean      .setOnClickListener(this);
        mChelueSet      .setOnClickListener(this);

        mReturnBtn      .setOnClickListener(this);
    }
    /**
     * 初始化资源文件，主要是字符串
     */
    private void initResources() {
        mapNavi = AMapNavi.getInstance(getApplicationContext());
        startPoint = new ArrayList<>();
        endPoint   = new ArrayList<>();
        wayPoint   = new ArrayList<>();

        Resources res = getResources();
        mStrategyMethods = new String[] {
                res.getString(R.string.navi_strategy_speed),
                res.getString(R.string.navi_strategy_cost),
                res.getString(R.string.navi_strategy_distance),
                res.getString(R.string.navi_strategy_nohighway),
                res.getString(R.string.navi_strategy_timenojam),
                res.getString(R.string.navi_strategy_costnojam) };
        mPositionMethods = new String[] { res.getString(R.string.mypoistion),
                res.getString(R.string.mappoistion) };

        alertdialog = new AlertDialog.Builder(this).setTitle("规划选择").setIcon(android.R.drawable.ic_dialog_info).
                setSingleChoiceItems(
                        mStrategyMethods, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mStrategyText.setText(mStrategyMethods[which]);
                                dialog.dismiss();
                            }
                        }
                ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 根据选择，获取行车策略
     */
    private int getDriveMode() {
        String strategyMethod = mStrategyText.getText().toString();
        // 速度优先
        if (mStrategyMethods[0].equals(strategyMethod)|| strategyMethod.equals("路线偏好")) {
            return AMapNavi.DrivingDefault;
        }
        // 花费最少
        else if (mStrategyMethods[1].equals(strategyMethod)) {
            return AMapNavi.DrivingSaveMoney;

        }
        // 距离最短
        else if (mStrategyMethods[2].equals(strategyMethod)) {
            return AMapNavi.DrivingShortDistance;
        }
        // 不走高速
        else if (mStrategyMethods[3].equals(strategyMethod)) {
            return AMapNavi.DrivingNoExpressways;
        }
        // 时间最短且躲避拥堵
        else if (mStrategyMethods[4].equals(strategyMethod)) {
            return AMapNavi.DrivingFastestTime;
        } else if (mStrategyMethods[5].equals(strategyMethod)) {
            return AMapNavi.DrivingAvoidCongestion;
        } else {
            return AMapNavi.DrivingDefault;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.naviprepare_celue_set:
//                mStrategyText.showDropDown();
                alertdialog.show();
                break;
            case R.id.navi_strategy_edit:
                alertdialog.show();
//                mStrategyText.showDropDown();
                break;
            case R.id.naviprepare_nowposi:
                setPosi(SET_NOWPOSI);
                break;
            case R.id.naviprepare_destination:
                setPosi(SET_POSI);
                break;
            case R.id.naviprepare_goon:
                if(mLocation!=null && mPosi!=null)
                {
                    startPoint.add(new NaviLatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                    endPoint  .add(new NaviLatLng(mPosi.getLatitude(), mPosi.getLongitude()));
                    mapNavi   .calculateDriveRoute(startPoint, endPoint, wayPoint, getDriveMode());
                    this.onBackPressed();
                }
                break;
            case R.id.naviprepare_home_set:
                setPosi(SET_HOME);
                break;
            case R.id.naviprepare_company_set:
                setPosi(SET_COMPANY);
                break;
            case R.id.naviprepare_home:
                if(mHome.getText().equals("家")){
                    setPosi(SET_HOME);
                }else {
                    if(mLocation!=null&&mHomePosi!=null){
                        startPoint.add(new NaviLatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                        endPoint  .add(new NaviLatLng(mHomePosi.getLatitude(), mHomePosi.getLongitude()));
                        mapNavi   .calculateDriveRoute(startPoint, endPoint, wayPoint, getDriveMode());
                        this.onBackPressed();
                    }
                }
                break;
            case R.id.naviprepare_company:
                if(mCompany.getText().equals("公司")){
                    setPosi(SET_COMPANY);
                }else {
                    if(mLocation!=null&&mCompanyPosi!=null){
                        startPoint.add(new NaviLatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                        endPoint  .add(new NaviLatLng(mCompanyPosi.getLatitude(), mCompanyPosi.getLongitude()));
                        mapNavi   .calculateDriveRoute(startPoint, endPoint, wayPoint, getDriveMode());
                        this.onBackPressed();
                    }
                }
                break;
            case R.id.naviprepare_cleanhistory:
                DBHelper.cleanHistory(this);
                initListView(this);
                break;
            case R.id.btn_return_bar:
                finish();
                break;

        }
    }

    private void setPosi(int style){
        intent = new Intent(this,IatDemo.class);
        bundle.putInt("style",style);
        intent.putExtra("myown", bundle);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("NaviPrepare", "onResume");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("NaviPrepare", "onActivityResult");
        if(data!=null) {
            bundle = data.getBundleExtra("myown");
            mLocation = bundle.getParcelable("myown");
            if (mLocation == null) {
                SharedPreferences s = getSharedPreferences("myown", MODE_PRIVATE);
                mLocation = new LatLonPoint(s.getFloat("SeachNeighX", 0), s.getFloat("SeachNeighY", 0));
                bundle.putParcelable("myown", mLocation);
            }
            int style = bundle.getInt("style", 0);
            switch (style) {
                case 0:
                    nowposi.setText("当前位置");
                    break;
                case 1:
                    nowposi.setText(bundle.getString("myname", "当前位置"));
                    mLocation = bundle.getParcelable("myown");
                    if(!nowposi.equals("当前位置")) {
                        DBHelper.addHistory(this, new HistoryPosi(nowposi.getText().toString(),(float)mLocation.getLatitude(),(float)mLocation.getLongitude()));
                    }
                    break;
                case 2:
                    nowposi.setText(bundle.getString("myname", "当前位置"));
                    mLocation = bundle.getParcelable("myown");
                    destination.setText(bundle.getString("posiname", "终点"));
                    destination.setTextColor(getResources().getColor(R.color.naviblue));
                    mBtn_gone.setTextColor(getResources().getColor(R.color.naviblue));

                    mPosi = bundle.getParcelable("posi");
                    if(!nowposi.equals("当前位置")) {
                        DBHelper.addHistory(this, new HistoryPosi(destination.getText().toString(),(float)mPosi.getLatitude(),(float)mPosi.getLongitude()));
                    }
                    break;
                case 3:
                    mHome.setText(bundle.getString("home"));
                    mHomePosi = bundle.getParcelable("homeposi");
                    editor.putString("home",bundle.getString("home"));
                    editor.putFloat("homex", (float)mHomePosi.getLatitude());
                    editor.putFloat("homey", (float)mHomePosi.getLongitude());
                    editor.commit();
                    break;
                case 4:
                    mCompany.setText(bundle.getString("company"));
                    mCompanyPosi = bundle.getParcelable("companyposi");
                    editor.putString("company", bundle.getString("company"));
                    editor.putFloat("companyx", (float) mCompanyPosi.getLatitude());
                    editor.putFloat("companyy", (float)mCompanyPosi.getLongitude());
                    editor.commit();
                    break;
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mLocation!=null && mHistoryNaviPosi.size()>position){
            startPoint.add(new NaviLatLng(mLocation.getLatitude(),mLocation.getLongitude()));
            endPoint.add(mHistoryNaviPosi.get(position));
            mapNavi   .calculateDriveRoute(startPoint, endPoint, wayPoint, getDriveMode());
            this.onBackPressed();
        }
    }
}
