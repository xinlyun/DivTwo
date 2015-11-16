package com.lin.dlivkfragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.navi.model.AMapNaviGuide;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinlyun on 15-10-16.
 */
public class RoadInfoexpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<AMapNaviGuide> aMapNaviGuides;
    private List<Data> datas;
    public RoadInfoexpandableListAdapter(Context context, List<AMapNaviGuide> aMapNaviGuides){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.aMapNaviGuides = aMapNaviGuides;
        initData();
    }
    private void initData(){
        datas = new ArrayList<>();
        String name = "";
        List<String> strings = null;
        for(AMapNaviGuide aMapNaviGuide:aMapNaviGuides){

            if((!name.equals(aMapNaviGuide.getName()) && !aMapNaviGuide.getName().equals(""))|| datas.size()==0){
                name = new String(aMapNaviGuide.getName());
                strings = new ArrayList<>();
                Data d = new Data(name);
                d.setStrs(strings);
                strings.add("行驶 " + aMapNaviGuide.getLength() + "米    " + Utils.rePid(aMapNaviGuide.getIconType()));
                datas.add(d);
            }else if(name.equals(aMapNaviGuide.getName()) || aMapNaviGuide.getName().equals("")){
                strings.add("行驶 "+aMapNaviGuide.getLength()+"米    "+ Utils.rePid(aMapNaviGuide.getIconType()));
            }
        }
    }


    @Override
    public int getGroupCount() {
        return datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return datas.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return datas.get(groupPosition).getStrs().get(childPosition);
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
    View view1;
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.group1, null);
            groupHolder.textView = (TextView) convertView
                    .findViewById(R.id.group1);
            groupHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.image1);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        this.view1 = convertView;
        datas.get(groupPosition).setView(view1);
        groupHolder.textView.setText(datas.get(groupPosition).getName());
        if (isExpanded)// ture is Expanded or false is not isExpanded
            groupHolder.imageView.setImageResource(R.drawable.expanded);
        else
            groupHolder.imageView.setImageResource(R.drawable.collapse);
        return convertView;
//        View view = inflater.inflate(R.layout.group1,null);
//        TextView textView = (TextView) view.findViewById(R.id.group1);
//        ImageView imageView = (ImageView) view.findViewById(R.id.image1);
//        textView.setText(datas.get(groupPosition).getName());
//        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder childHolder = null;
        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.child1, null);

            childHolder.textName = (TextView) convertView
                    .findViewById(R.id.name2);
            childHolder.imageView = (ImageView) convertView
                    .findViewById(R.id.image2);

            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        childHolder.textName.setText(datas.get(groupPosition).getStrs().get(childPosition));
        return convertView;
//        View view = inflater.inflate(R.layout.child1,null);
//        TextView textView = (TextView) view.findViewById(R.id.name2);
//        ImageView imageView = (ImageView) view.findViewById(R.id.image2);
//        Button button = (Button) view.findViewById(R.id.button2);
//        textView.setText(datas.get(groupPosition).getStrs().get(childPosition));
//        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {


        return true;
    }


    public class Data{
        String name="";
        List<String> s;
        View headView;
        Data(String name){
            this.name = ""+name;

        }
        private void setView(View view){
            this.headView = view;
        }
        public View getHeadView(){
            return headView;
        }
        private void setStrs(List<String> s){
            this.s = s;
        }

        public String getName() {
            if(name==null)return "";
            return name;
        }
        private List<String> getStrs() {
            return s;
        }
        private int size(){
            return  s.size();
        }
    }



    public class GroupHolder {
        TextView textView;
        ImageView imageView;
    }

    public class ChildHolder {
        TextView textName;
        ImageView imageView;
    }

}
