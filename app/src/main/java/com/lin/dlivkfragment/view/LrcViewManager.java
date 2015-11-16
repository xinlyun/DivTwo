package com.lin.dlivkfragment.view;

import android.widget.TextView;


import com.lin.dlivkfragment.MusicApp;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.model.LyricSentence;
import com.lin.dlivkfragment.util.LyricLoadHelper;

import java.util.List;

/**
 * 歌词视图
 * Created by zzq on 15-8-3.
 */
public class LrcViewManager {
    private List<LyricSentence> list;

    private TextView lrcPrev;
    private TextView lrcNext;
    private TextView lrcNow;
    private LyricLoadHelper mHelper;

    public LrcViewManager(TextView prev, TextView now, TextView next,
                          LyricLoadHelper helper){
        lrcPrev = prev;
        lrcNow = now;
        lrcNext = next;
        mHelper = helper;

    }

    public void setLyric(List<LyricSentence> lyricSentences){
        list = lyricSentences;
    }

    public void clear(){
        lrcPrev.setText("");
        lrcNow.setText(MusicApp.getContext().getString(R.string.default_lrc));
        lrcNext.setText("");
        list = null;
    }

    //更新歌词序列
    public void updateIndex(long time){
        if(list == null) return;


        //歌词序号
        mHelper.notifyTime(time);
        int t = mHelper.getIndexOfCurrentSentence();
        if(t == -1 || t >= list.size()) return;

        if(t > 0) {
            lrcPrev.setText(list.get(t - 1).getContentText());
        }else{
            lrcPrev.setText("");
        }
        lrcNow.setText(list.get(t).getContentText());
        if(t < list.size() - 1) {
            lrcNext.setText(list.get(t + 1).getContentText());
        }else {
            lrcNext.setText("");
        }

    }
}
