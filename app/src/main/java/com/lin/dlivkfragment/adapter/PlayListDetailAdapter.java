package com.lin.dlivkfragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.model.MusicInfo;

import java.util.HashMap;
import java.util.List;

/**
 * 二级播放列表（音乐列表）的adapter
 * Created by ZQ on 2015/8/11.
 */
public class PlayListDetailAdapter extends ArrayAdapter<MusicInfo>{

    private int resourceId;
    private int nowIndex = 0x7f7f7f7f;
    private List<MusicInfo> lists;

    public static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();
    // 初始化 设置所有checkbox都为未选择
    public void initCheckList() {
        isSelected = new HashMap<Integer, Boolean>();
        int size = 0;
        if(lists != null) size = lists.size();
        for (int i = 0; i < size; i++) {
            isSelected.put(i, false);
        }
    }

    public static void selectAll(int size){
        if(isSelected == null) isSelected = new HashMap<Integer, Boolean>();
        for(int i = 0; i < size; i++){
            isSelected.put(i, true);
        }
    }

    public PlayListDetailAdapter(Context context, int textViewResourceId, List<MusicInfo> lists,
                                 int nowIndex){
        super(context, textViewResourceId, lists);
        resourceId = textViewResourceId;
        this.nowIndex = nowIndex;
        this.lists = lists;
        initCheckList();
    }

    public PlayListDetailAdapter(Context context, int textViewResourceId, List<MusicInfo> lists){
        super(context, textViewResourceId, lists);
        resourceId = textViewResourceId;
        this.lists = lists;
        initCheckList();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        MusicInfo musicInfo = getItem(position);

        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.tvMusicName = (TextView) view.findViewById(R.id.tv_playlist_music);
            viewHolder.tvArtistName = (TextView) view.findViewById(R.id.tv_playlist_artist);
            viewHolder.ivState = (ImageView) view.findViewById(R.id.iv_playlist_state);
            viewHolder.cbDel = (CheckBox) view.findViewById(R.id.cb_delete);

            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tvMusicName.setText(musicInfo.musicName);
        viewHolder.tvArtistName.setText(musicInfo.artist);

        if(position == nowIndex){
            viewHolder.ivState.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivState.setVisibility(View.INVISIBLE);
        }

        viewHolder.cbDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelected.put(position, false)){
                    isSelected.put(position, false);
                }else{
                    isSelected.put(position, true);
                }
            }
        });

        //根据isSelected中记录的信息，设置checkbox的状态
        if(isSelected.size() == 0 || isSelected == null) initCheckList();
        if(isSelected.containsKey(position)){
            viewHolder.cbDel.setChecked(isSelected.get(position));
        }
        return view;
    }

    class ViewHolder{
        TextView tvMusicName;
        TextView tvArtistName;
        ImageView ivState;
        CheckBox cbDel;
    }
}
