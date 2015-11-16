package com.lin.dlivkfragment.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.lin.dlivkfragment.interfaces.IConstants;


/**
 * 播放列表信息
 * Created by ZQ on 2015/8/18.
 */
public class ListInfo implements Parcelable, IConstants {
    public int _id = -1;
    public String listName = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, _id);
        bundle.putString(KEY_LISTNAME, listName);
        dest.writeBundle(bundle);
    }

    public static final Creator<ListInfo> CREATOR =
            new Creator<ListInfo>(){
                @Override
                public ListInfo createFromParcel(Parcel source){
                    ListInfo list = new ListInfo();
                    Bundle bundle = source.readBundle();
                    list._id = bundle.getInt(KEY_ID);
                    list.listName = bundle.getString(KEY_LISTNAME);
                    return list;
                }
                @Override
                public ListInfo[] newArray(int size) {
                    return new ListInfo[size];
                }
            };
}
