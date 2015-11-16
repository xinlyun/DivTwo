package com.lin.dlivkfragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.model.ListInfo;

import java.util.List;

/**
 * 播放列表list的adapter
 * Created by ZQ on 2015/8/11.
 */
public class PlayListAdapter extends ArrayAdapter<ListInfo> {
    private int resourceId;
    private int nowIndex = 0x7f7f7f7f;

    public PlayListAdapter(Context context, int textViewResourceId, List<ListInfo> lists,
                           int nowIndex){
        super(context, textViewResourceId, lists);
        resourceId = textViewResourceId;
        this.nowIndex = nowIndex;
    }

    public PlayListAdapter(Context context, int textViewResourceId, List<ListInfo> lists){
        super(context, textViewResourceId, lists);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String listName = getItem(position).listName;
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvListName = (TextView) view.findViewById(R.id.tv_list_name);
            viewHolder.ivState = (ImageView) view.findViewById(R.id.iv_playlist_state);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if(position == nowIndex){
            viewHolder.ivState.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivState.setVisibility(View.INVISIBLE);
        }

        viewHolder.tvListName.setText(listName);
        return view;
    }

    class ViewHolder{
        TextView tvListName;
        ImageView ivState;
    }
}
