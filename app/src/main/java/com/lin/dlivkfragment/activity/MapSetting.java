package com.lin.dlivkfragment.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.util.Utils;
import com.lin.myfloatactionbtn.SystemBarTintManager;
import com.lin.myfloatactionbtn.swipeback.SwipeBackActivity;
import com.lin.myfloatactionbtn.swipeback.SwipeBackLayout;

/**
 * Created by xinlyun on 15-10-13.
 */
public class MapSetting extends SwipeBackActivity implements  View.OnClickListener,CompoundButton.OnCheckedChangeListener{

    private TextView map_enter1,map_enter2,map_enter3,map_enter4,map_enter5,map_enter4_false;
    private ImageView btn_return;
    private Switch map_check1,map_check2,map_check3,map_check4,map_check5,map_check6,map_check7;
    private SharedPreferences s ;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_mapsetting);

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


        initShared();
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        initView();
        initListen();
    }

    private void initShared(){
        s = getSharedPreferences("mapsetting",MODE_PRIVATE);
        editor = s.edit();
    }

    private void initView(){
        btn_return = (ImageView) findViewById(R.id.btn_return_bar);

        map_enter1 = (TextView) findViewById(R.id.map_setting_in1);
        map_enter2 = (TextView) findViewById(R.id.map_setting_in2);
        map_enter3 = (TextView) findViewById(R.id.map_setting_in3);
        map_enter4 = (Button) findViewById(R.id.map_setting_switch4_true);
        map_enter4_false = (TextView) findViewById(R.id.map_setting_switch4_false);
        map_enter5 = (TextView) findViewById(R.id.map_setting_in4);

        map_check1 = (Switch) findViewById(R.id.map_setting_switch1);
        map_check2 = (Switch) findViewById(R.id.map_setting_switch2);
        map_check3 = (Switch) findViewById(R.id.map_setting_switch3);
        map_check4 = (Switch) findViewById(R.id.map_setting_switch5);
        map_check5 = (Switch) findViewById(R.id.map_setting_switch6);
        map_check6 = (Switch) findViewById(R.id.map_setting_switch7);
        map_check7 = (Switch) findViewById(R.id.map_setting_switch8);

        initSwitch();

    }



    private void initListen(){
        btn_return.setOnClickListener(this);

        map_enter1.setOnClickListener(this);
        map_enter2.setOnClickListener(this);
        map_enter3.setOnClickListener(this);
        map_enter4.setOnClickListener(this);
        map_enter4_false.setOnClickListener(this);
        map_enter5.setOnClickListener(this);

        map_check1.setOnCheckedChangeListener(this);
        map_check2.setOnCheckedChangeListener(this);
        map_check3.setOnCheckedChangeListener(this);
        map_check4.setOnCheckedChangeListener(this);
        map_check5.setOnCheckedChangeListener(this);
        map_check6.setOnCheckedChangeListener(this);
        map_check7.setOnCheckedChangeListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_setting_in1:
                Utils.startActivitySafely(this, v, new Intent(this, OfflineMapActivity.class));
//                startActivity(new Intent(this, OfflineMapActivity.class));
                finish();
                break;
            case R.id.map_setting_in2:

                break;
            case R.id.map_setting_in3:
                Log.d("MapSetting","map_setting_in3");
//                SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_TTS);
                Intent var2 = new Intent();
                var2.setPackage("com.iflytek.vflynote");
                String var3 = "com.iflytek.vflynote";

                    var3 = "com.iflytek.vflynote.activity.speaker.SpeakerSetting";

//                    var3 = "com.iflytek.vflynote.settings.asr";

//                    var3 = "com.iflytek.vflynote.settings.main";


                var2.setAction(var3);
                var2.addFlags(268435456);
                Utils.startActivitySafely(this,v,var2);
//                this.startActivity(var2);
                finish();
                break;
            case R.id.map_setting_switch4_true:
                map_enter4.setTextColor(getResources().getColor(R.color.setting_text_true));
                map_enter4_false.setTextColor(getResources().getColor(R.color.setting_text_false));
                map_enter4.setBackgroundColor(getResources().getColor(R.color.setting_text_bg_true));
                map_enter4_false.setBackgroundColor(getResources().getColor(R.color.setting_text_bg_false));
                break;
            case R.id.map_setting_switch4_false:
                map_enter4_false.setTextColor(getResources().getColor(R.color.setting_text_true));
                map_enter4.setTextColor(getResources().getColor(R.color.setting_text_false));
                map_enter4_false.setBackgroundColor(getResources().getColor(R.color.setting_text_bg_true));
                map_enter4.setBackgroundColor(getResources().getColor(R.color.setting_text_bg_false));
                break;
            case R.id.map_setting_in4:

                break;
            case R.id.btn_return_bar:
                finish();
                break;
        }
    }

    private void initSwitch(){
        map_check1.setChecked(s.getBoolean("xiangtu",true));
        map_check2.setChecked(s.getBoolean("yuyin",true));
        map_check3.setChecked(s.getBoolean("lukuang",true));
        map_check4.setChecked(s.getBoolean("dianzigou",true));
        map_check5.setChecked(s.getBoolean("huanyin",true));
        map_check6.setChecked(s.getBoolean("lianwang",true));
        map_check7.setChecked(s.getBoolean("didian",true));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.map_setting_switch1:
                editor.putBoolean("xiangtu",isChecked);
                editor.commit();
                break;
            case R.id.map_setting_switch2:
                editor.putBoolean("yuyin",isChecked);
                editor.commit();
                break;
            case R.id.map_setting_switch3:
                editor.putBoolean("lukuang",isChecked);
                editor.commit();
                break;
            case R.id.map_setting_switch5:
                editor.putBoolean("dianzigou",isChecked);
                editor.commit();
                break;
            case R.id.map_setting_switch6:
                editor.putBoolean("huanyin",isChecked);
                editor.commit();
                break;
            case R.id.map_setting_switch7:
                editor.putBoolean("lianwang",isChecked);
                editor.commit();
                break;
            case R.id.map_setting_switch8:
                editor.putBoolean("didian",isChecked);
                editor.commit();
                break;
        }
    }


}
