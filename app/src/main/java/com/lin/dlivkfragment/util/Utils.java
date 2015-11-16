package com.lin.dlivkfragment.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.lin.dlivkfragment.R;


public class Utils {
    public static final String DAY_NIGHT_MODE="daynightmode";
    public static final String DEVIATION="deviationrecalculation";
    public static final String JAM="jamrecalculation";
    public static final String TRAFFIC="trafficbroadcast";
    public static final String CAMERA="camerabroadcast";
    public static final String SCREEN="screenon";
    public static final String THEME="theme";
    public static final String ISEMULATOR="isemulator";


    public static final String ACTIVITYINDEX="activityindex";

    public static final int SIMPLEHUDNAVIE=0;
    public static final int EMULATORNAVI=1;
    public static final int SIMPLEGPSNAVI=2;
    public static final int SIMPLEROUTENAVI=3;


    public static final boolean DAY_MODE=false;
    public static final boolean NIGHT_MODE=true;
    public static final boolean YES_MODE=true;
    public static final boolean NO_MODE=false;
    public static final boolean OPEN_MODE=true;
    public static final boolean CLOSE_MODE=false;


    public static int dp2px(Context context, int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    public static String rePid(int iconId){
        String s = "";
        switch (iconId){
            case 0:
                break;
            case 1:
                break;
            case 2:
                s = "左转";
                break;
            case 3:
                s = "右转";
                break;
            case 4:
                s = "向左前方";
                break;
            case 5:
                s = "向右前方";
                break;
            case 6:
                s = "向左后方";
                break;
            case 7:
                s = "向右后方";
                break;
            case 8:
                s = "向左掉头" ;
                break;
            case 9:
                s = "直行" ;
                break;
            case 10:
                s = "到达途经点";
                break;
            case 11:
                s = "进入环岛";
                break;
            case 12:
                s = "驶出环岛";
                break;
            case 13:
                s = "到达服务区";
                break;
            case 14:
                s = "到达收费站";
                break;
            case 15:
                s = "到达目的地";
                break;
            case 16:
                s = "到达隧道";
                break;
            case 17:
                s = "靠左行驶";
                break;
            case 18:
                s = "靠右行驶";
                break;
            case 19:
                s = "通过人行横道";
                break;
            case 20:
                s = "通过过街天桥";
                break;
            case 21:
                s = "通过地下通道";
                break;
            case 22:
                s = "通过广场";
                break;
            case 23:
                s = "到道路斜对面";
                break;
        }

        return s;
    }


    public static boolean startActivitySafely(Activity context,View v, Intent intent ) {
        boolean success = false;
        try {
            success = startActivity(context,v,intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "未发现",
                    Toast.LENGTH_SHORT).show();
        }
        return success;
    }
    static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.xiaopeng." +
            "home.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";

   static boolean startActivity(Activity context,View v, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            // Only launch using the new animation if the shortcut has not opted
            // out (this is a
            // private contract between launcher and may be ignored in the
            // future).
            boolean useLaunchAnimation = (v != null)
                    && !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION);
            if (useLaunchAnimation) {
                ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(v,
                        0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

                context.startActivity(intent, opts.toBundle());
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (SecurityException e) {
            Toast.makeText(context, "未发现",
                    Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    public static int poisitemType(String string){
        if(string.contains("医疗保障"))return 0;
        else if(string.contains("生活服务"))return 4;
        else if(string.contains("购物服务"))return 1;
        else if(string.contains("餐饮服务"))return 2;
        else if(string.contains("剧场"))return 3;
        else if(string.contains("体育休闲"))return 3;
        else if(string.contains("休闲服务"))return 3;
        else if(string.contains("科教文化"))return 5;
        else if(string.contains("公司企业"))return 5;
        else if(string.contains("商务住宅"))return 6;
        else if(string.contains("住宿服务"))return 6;
        else if(string.contains("通行设施"))return 7;
        else if(string.contains("政府机构"))return 0;
        return 0;
    }




}