package com.lin.dlivkfragment.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于管理所有Activity，实现一键退出APP
 * Created by zzq on 15-8-22.
 */
public class ActivityManager {

    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
