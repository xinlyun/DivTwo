package com.lin.dlivkfragment.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.lin.dlivkfragment.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xinlyun on 15-10-22.
 */
public class OfflineMapService extends Service implements OfflineMapManager.OfflineMapDownloadListener{
    private OfflineMapManager amapManager = null;// 离线地图下载控制器
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市
    private HashMap<Object, List<OfflineMapCity>> cityMap = new HashMap<Object, List<OfflineMapCity>>();// 保存二级目录的市
    private boolean isStart = false;// 判断是否开始下载,true表示开始下载，false表示下载失败
    private boolean[] isOpen;// 记录一级目录是否打开
    private boolean stop=false;
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("OfflineMapService","onBind");
        MyBind myBind = new MyBind();
        return myBind;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("OfflineMapService","onBind");
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("OfflineMapService","onCreate");
        amapManager = new OfflineMapManager(this.getApplicationContext(), this);
        init();
    }

    @Override
    public void onDestroy() {
        Log.d("OfflineMapService","onDestroy");
        super.onDestroy();
    }



    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("OfflineMapService","onStart");
        super.onStart(intent, startId);
    }

    /**
     * 初始化UI布局文件 以及初始化OfflineMapManager
     */
    public void init() {

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }




        provinceList = amapManager.getOfflineMapProvinceList();

        List<OfflineMapProvince> bigCityList = new ArrayList<OfflineMapProvince>();// 以省格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();// 以市格式保存直辖市、港澳、全国概要图
        List<OfflineMapCity> gangaoList = new ArrayList<OfflineMapCity>();// 保存港澳城市
        List<OfflineMapCity> gaiyaotuList = new ArrayList<OfflineMapCity>();// 保存概要图
        for (int i = 0; i < provinceList.size(); i++) {
            OfflineMapProvince offlineMapProvince = provinceList.get(i);
            List<OfflineMapCity> city = new ArrayList<OfflineMapCity>();
            OfflineMapCity aMapCity = getCicy(offlineMapProvince);
            if (offlineMapProvince.getCityList().size() != 1) {
                city.add(aMapCity);
                city.addAll(offlineMapProvince.getCityList());
            } else {
                cityList.add(aMapCity);
                bigCityList.add(offlineMapProvince);
            }
            cityMap.put(i + 3, city);
        }
        OfflineMapProvince title = new OfflineMapProvince();

        title.setProvinceName("概要图");
        provinceList.add(0, title);
        title = new OfflineMapProvince();
        title.setProvinceName("直辖市");
        provinceList.add(1, title);
        title = new OfflineMapProvince();
        title.setProvinceName("港澳");
        provinceList.add(2, title);
        provinceList.removeAll(bigCityList);

        for (OfflineMapProvince aMapProvince : bigCityList) {
            if (aMapProvince.getProvinceName().contains("香港")
                    || aMapProvince.getProvinceName().contains("澳门")) {
                gangaoList.add(getCicy(aMapProvince));
            } else if (aMapProvince.getProvinceName().contains("全国概要图")) {
                gaiyaotuList.add(getCicy(aMapProvince));
            }
        }
        try {
            cityList.remove(4);// 从List集合体中删除香港
            cityList.remove(4);// 从List集合体中删除澳门
            cityList.remove(4);// 从List集合体中删除澳门
        } catch (Throwable e) {
            e.printStackTrace();
        }
        cityMap.put(0, gaiyaotuList);// 在HashMap中第0位置添加全国概要图
        cityMap.put(1, cityList);// 在HashMap中第1位置添加直辖市
        cityMap.put(2, gangaoList);// 在HashMap中第2位置添加港澳
        isOpen = new boolean[provinceList.size()];
    }

    /**
     * 把一个省的对象转化为一个市的对象
     */
    public OfflineMapCity getCicy(OfflineMapProvince aMapProvince) {
        OfflineMapCity aMapCity = new OfflineMapCity();
        aMapCity.setCity(aMapProvince.getProvinceName());
        aMapCity.setSize(aMapProvince.getSize());
        aMapCity.setCompleteCode(aMapProvince.getcompleteCode());
        aMapCity.setState(aMapProvince.getState());
        aMapCity.setUrl(aMapProvince.getUrl());
        return aMapCity;
    }


    public OfflineMapProvince getItemByProvinceName(String name){
        return amapManager.getItemByProvinceName(name);
    }


    // 一些可能会用到的方法
    /**
     * 暂停所有下载和等待
     */
    public void stopAll() {
        if (amapManager != null) {
            amapManager.stop();
        }
    }

    /**
     * 继续下载所有暂停中
     */
    public void startAllInPause() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                try {
                    amapManager.downloadByCityName(mapCity.getCity());
                } catch (AMapException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    OfflineMapManager.OfflineMapDownloadListener listener;
    public List<OfflineMapProvince> getProvinceList(){
        return provinceList;
    }
    public HashMap<Object, List<OfflineMapCity>> getCityMap(){
        return cityMap;
    }
    public OfflineMapManager getAmapManager(){
        return amapManager;
    }
    public void setOfflineListener(OfflineMapManager.OfflineMapDownloadListener listener){
        this.listener = listener;
    }
    /**
     * 取消所有<br>
     * 即：删除下载列表中除了已完成的所有<br>
     * 会在OfflineMapDownloadListener.onRemove接口中回调是否取消（删除）成功
     */
    public void cancelAll() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                amapManager.remove(mapCity.getCity());
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }




    @Override
    public void onDownload(int i, int i1, String s) {
        Log.d("OffineMapService", "onDownload   style" + i);
        switch (i) {
            case OfflineMapStatus.SUCCESS:
                notification.contentView.setTextViewText(R.id.noti_tv,"下载完成!");
                notificationManager.notify(998, notification);
                notificationManager.cancel(998);
                break;
            case OfflineMapStatus.LOADING:
                notification.contentView.setTextViewText(R.id.noti_tv,"正在下载:"+s+"  进度:"+i1+"%");
                notification.contentView.setProgressBar(R.id.noti_pd, 100, i1, false);
                notificationManager.notify(998, notification);
                break;
            case OfflineMapStatus.UNZIP:
                notification.contentView.setTextViewText(R.id.noti_tv,"正在解压:"+s+"  进度:"+i1+"%");
                notification.contentView.setProgressBar(R.id.noti_pd, 100, i1, false);
                notificationManager.notify(998, notification);
                break;
            case OfflineMapStatus.WAITING:
                break;
            case OfflineMapStatus.PAUSE:
                break;
            case OfflineMapStatus.STOP:
                break;
            case OfflineMapStatus.ERROR:
                break;
            case OfflineMapStatus.EXCEPTION_AMAP:
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:

                break;
            case OfflineMapStatus.EXCEPTION_SDCARD:
                break;
            default:
                break;
        }

        if(listener!=null)listener.onDownload(i,i1,s);
//        if(!stop && i==OfflineMapStatus.PAUSE)
//            startAllInPause();
    }

    @Override
    public void onCheckUpdate(boolean b, String s) {
        Log.d("OffineMapService","onCheckUpdate");
        if(listener!=null)listener.onCheckUpdate(b, s);
    }

    @Override
    public void onRemove(boolean b, String s, String s1) {
        Log.d("OffineMapService","onRemove");
        if(listener!=null)listener.onRemove(b, s, s1);
    }
    NotificationManager notificationManager;
    Notification notification;
    public void downBegin(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new Notification(R.drawable.ic_allapps,"下载",System
                .currentTimeMillis());
        RemoteViews view = new RemoteViews(getPackageName(),R.layout.noti);
        notification.contentView = view;
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                998, new Intent(),
                PendingIntent.FLAG_CANCEL_CURRENT);

        notification.contentIntent = contentIntent;
    }
    public class MyBind extends Binder {
        public OfflineMapService getService(){
            return OfflineMapService.this;
        }
        public List<OfflineMapProvince> getProvinceList(){
            return OfflineMapService.this.getProvinceList();
        }
        public HashMap<Object, List<OfflineMapCity>> getCityMap(){
            return OfflineMapService.this.getCityMap();
        }
        public OfflineMapManager getAmapManager(){
            return OfflineMapService.this.amapManager;
        }
        public void setOfflineListener(OfflineMapManager.OfflineMapDownloadListener listener){
            OfflineMapService.this.listener = listener;
        }
    }
}