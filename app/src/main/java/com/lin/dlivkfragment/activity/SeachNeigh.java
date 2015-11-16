package com.lin.dlivkfragment.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.activity.NaviPrepare;
import com.lin.myfloatactionbtn.SystemBarTintManager;
import com.lin.myfloatactionbtn.pinnedlistview.PinnedHeaderExpandableListView;
import com.lin.myfloatactionbtn.pinnedlistview.StickyLayout;
import com.lin.myfloatactionbtn.swipeback.SwipeBackActivity;
import com.lin.myfloatactionbtn.swipeback.SwipeBackLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinlyun on 15-10-13.
 */
public class SeachNeigh extends SwipeBackActivity implements View.OnClickListener,
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener, StickyLayout.OnGiveUpTouchEventListener
        ,PoiSearch.OnPoiSearchListener
{
    private ImageView btn_return;
    private String[] strings = {"充电站","停车场","商务休闲","餐饮美食","酒店住宿","商场购物"};
    //    private  ListAdapter listAdapter;
    private PinnedHeaderExpandableListView expandableListView;
    private StickyLayout stickyLayout;
    private ArrayList<Group> groupList;
    private ArrayList<List<String>> childList;
    private ArrayList<List<LatLonPoint>> childPosi;
    private MyexpandableListAdapter adapter;
    private String cityName;
    private LatLonPoint mLocation = null;
    private int time;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time = 0;
        //--以下这段代码设置基本的浸入式状态栏----
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_seachnegih);
        Intent i = getIntent();
        bundle = i.getBundleExtra("myown");
//        bundle.putParcelable("myown",mLocation)
        cityName = bundle.getString("city","广州");
        mLocation = bundle.getParcelable("myown");
        if(mLocation!=null){
            SharedPreferences s = getSharedPreferences("myown",MODE_PRIVATE);
            SharedPreferences.Editor editor = s.edit();
            editor.putFloat("SeachNeighX", (float) mLocation.getLatitude());
            editor.putFloat("SeachNeighY", (float) mLocation.getLongitude());
            editor.commit();
        }
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

        initView();
        initData();

        adapter = new MyexpandableListAdapter(this);
        expandableListView.setAdapter(adapter);


        initListen();
    }

    private void initView(){
        btn_return = (ImageView) findViewById(R.id.btn_return_bar);
        expandableListView = (PinnedHeaderExpandableListView) findViewById(R.id.expandablelist);
        stickyLayout = (StickyLayout)findViewById(R.id.sticky_layout);
        ((TextView)findViewById(R.id.btn_return_bar_title)).setText("附近搜索");

    }
    private void initListen(){
        btn_return.setOnClickListener(this);
        expandableListView.setOnHeaderUpdateListener(this);
        expandableListView.setOnChildClickListener(this);
        expandableListView.setOnGroupClickListener(this);
        stickyLayout.setOnGiveUpTouchEventListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_return_bar:
                finish();
                break;
        }
    }


    /***
     * InitData
     */
    void initData() {
        groupList = new ArrayList<Group>();
        Group group = null;
//        for (int i = 0; i < 3; i++) {
//            group = new Group();
//            group.setTitle("group-" + i);
//            groupList.add(group);
//        }
        group = new Group();
        group.setTitle("汽车服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("汽车销售");
        groupList.add(group);

        group = new Group();
        group.setTitle("汽车维修");
        groupList.add(group);

        group = new Group();
        group.setTitle("摩托车服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("餐饮服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("购物服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("生活服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("体育休闲服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("医疗保健服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("住宿服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("风景名胜");
        groupList.add(group);

        group = new Group();
        group.setTitle("商务住宅");
        groupList.add(group);

        group = new Group();
        group.setTitle("政府机构及社会团体");
        groupList.add(group);

        group = new Group();
        group.setTitle("科教文化服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("交通设施服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("金融保险服务");
        groupList.add(group);

        group = new Group();
        group.setTitle("公司企业");
        groupList.add(group);

        group = new Group();
        group.setTitle("道路附属设施");
        groupList.add(group);

        group = new Group();
        group.setTitle("公共设施");
        groupList.add(group);


        childList = new ArrayList<>();
        childPosi = new ArrayList<>();


//        for(Group group1:groupList) {
//        ArrayList<String> childTemp = new ArrayList<String>();
//        ArrayList<LatLonPoint> posi = new ArrayList<>();
        PoiSearch.Query queryq = new PoiSearch.Query("",groupList.get(time).getTitle(),cityName);
        queryq.setPageNum(1);
        queryq.setPageSize(10);
        PoiSearch p = new PoiSearch(this,queryq);
        if(mLocation!=null)
            p.setBound(new PoiSearch.SearchBound(mLocation,1000));
        else {
            SharedPreferences s = getSharedPreferences("myown",MODE_PRIVATE);
//            ("SeachNeighX", (float) mLocation.getLatitude());
//            editor.putFloat("SeachNeighY",
            double posix = s.getFloat("SeachNeighX",0);
            double posiy = s.getFloat("SeachNeighY",0);
            mLocation = new LatLonPoint(posix,posiy);
            p.setBound(new PoiSearch.SearchBound(mLocation,1000));
        }

        p.setOnPoiSearchListener(this);
        p.searchPOIAsyn();

//            try {
//                PoiResult result =  p.searchPOI();
//
//                if (result != null && result.getQuery() != null) {// 搜索poi的结果
//                    // 取得搜索到的poiitems有多少页
//                    List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    if(poiItems!=null)
//                        for(PoiItem poiItem : poiItems){
//                            childTemp.add(poiItem.getTitle());
//                            posi.add(poiItem.getLatLonPoint());
//                        }
//                    childList.add(childTemp);
//                    childPosi.add(posi);
//                }
//            } catch (AMapException e) {
//                e.printStackTrace();
//            }
//        }

//        for (int i = 0; i < groupList.size(); i++) {
//            ArrayList<String> childTemp;
//            if (i == 0) {
//                childTemp = new ArrayList<String>();
//                for (int j = 0; j < 13; j++) {
//                    String people = "group 1:"+j;
//
//
//                    childTemp.add(people);
//                }
//            } else if (i == 1) {
//                childTemp = new ArrayList<String>();
//                for (int j = 0; j < 8; j++) {
//                    String people = "group 2:"+j;
//
//                    childTemp.add(people);
//                }
//            } else {
//                childTemp = new ArrayList<String>();
//                for (int j = 0; j < 5; j++) {
//                    String people = "group"+(i+1)+":"+j;
//
//                    childTemp.add(people);
//                }
//            }
//            childList.add(childTemp);
//        }

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(this,"这里是"+childList.get(groupPosition).get(childPosition)+"  位置"+childPosi.get(groupPosition).get(childPosition).getLatitude()+","+
                childPosi.get(groupPosition).get(childPosition).getLongitude(),Toast.LENGTH_SHORT).show();
        bundle.putInt("style",2);
        bundle.putString("posiname", childList.get(groupPosition).get(childPosition));
        bundle.putParcelable("posi", childPosi.get(groupPosition).get(childPosition));
        Intent intent = new Intent(this,NaviPrepare.class);
        intent.putExtra("myown",bundle);


        startActivity(intent);
        finish();

        return false;
    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {
        if (expandableListView.getFirstVisiblePosition() == 0) {
            View view = expandableListView.getChildAt(0);
            if (view != null && view.getTop() >= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public View getPinnedHeader() {

        View headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.group, null);
        headerView.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        Group firstVisibleGroup = (Group) adapter.getGroup(firstVisibleGroupPos);
        TextView textView = (TextView) headerView.findViewById(R.id.group);
        textView.setText(firstVisibleGroup.getTitle());
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        ArrayList<String> childTemp = new ArrayList<String>();
        ArrayList<LatLonPoint> posi = new ArrayList<>();
        if (poiResult != null && poiResult.getQuery() != null
                && poiResult.getPois() != null && poiResult.getPois().size() > 0) {// 搜索poi的结果
//            if (poiResult.getQuery().equals(startSearchQuery)) {
            ArrayList<PoiItem> poiItems = poiResult.getPois();// 取得poiitem数据
            if(poiItems!=null)
                for (PoiItem poiItem:poiItems){
                    childTemp.add(poiItem.getTitle());
                    posi.add(poiItem.getLatLonPoint());
                }
//            childList.add(childTemp);
//            childPosi.add(posi);
        }
        childList.add(childTemp);
        childPosi.add(posi);

        time++;
        if(time<groupList.size()){
            PoiSearch.Query queryq = new PoiSearch.Query("",groupList.get(time).getTitle(),cityName);
            queryq.setPageNum(1);
            queryq.setPageSize(10);
            PoiSearch p = new PoiSearch(this,queryq);
            if(mLocation!=null)
                p.setBound(new PoiSearch.SearchBound(mLocation,50000));
            p.setOnPoiSearchListener(this);

            p.searchPOIAsyn();
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }


    class Group {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    class GroupHolder {
        TextView textView;
        ImageView imageView;
    }

    class ChildHolder {
        TextView textName;
        ImageView imageView;
        TextView textDis;
    }

    /***
     * 数据源
     *
     * @author Administrator
     *
     */
    class MyexpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private LayoutInflater inflater;

        public MyexpandableListAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        // 返回父列表个数
        @Override
        public int getGroupCount() {
            return groupList.size();
        }

        // 返回子列表个数
        @Override
        public int getChildrenCount(int groupPosition) {
//            if(childList.size()<=groupPosition)return 0;
            if(childList.size()<=groupPosition)return 0;
            return childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {

            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {

            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder groupHolder = null;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group, null);
                groupHolder.textView = (TextView) convertView
                        .findViewById(R.id.group);
                groupHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.image);

                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }

            groupHolder.textView.setText(((Group) getGroup(groupPosition))
                    .getTitle());
            if (isExpanded)// ture is Expanded or false is not isExpanded
                groupHolder.imageView.setImageResource(R.drawable.expanded);
            else
                groupHolder.imageView.setImageResource(R.drawable.collapse);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder childHolder = null;
            if (convertView == null) {
                childHolder = new ChildHolder();
                convertView = inflater.inflate(R.layout.child, null);

                childHolder.textName = (TextView) convertView
                        .findViewById(R.id.name);
                childHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.image);
                childHolder.textDis = (TextView) convertView.findViewById(R.id.distance);
//                Button button = (Button) convertView
//                        .findViewById(R.id.button1);
//                button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(SeachNeigh.this, "clicked pos=", Toast.LENGTH_SHORT).show();
//                    }
//                });

                convertView.setTag(childHolder);
            } else {
                childHolder = (ChildHolder) convertView.getTag();
            }

            childHolder.textName.setText(( getChild(groupPosition,
                    childPosition)).toString());
//            childHolder.textDis.setText(""+childPosi.get(groupPosition).get(childPosition).getLatitude()+","+childPosi.get(groupPosition).get(childPosition).getLongitude());

            float dis = AMapUtils.calculateLineDistance(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()),new LatLng(childPosi.get(groupPosition).get(childPosition).getLatitude(),childPosi.get(groupPosition).get(childPosition).getLongitude()));
            if(dis<1000)
                childHolder.textDis.setText("距离 ：" + (int) dis + " 米");
            else {
                dis = dis/1000f;
                java.text.DecimalFormat   df=new   java.text.DecimalFormat("#.##");
//                dis=((int)(dis*100))/100;
                childHolder.textDis.setText("距离 ：" + df.format(dis)+ " 公里");
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }



}
