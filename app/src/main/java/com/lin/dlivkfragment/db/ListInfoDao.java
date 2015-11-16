package com.lin.dlivkfragment.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.model.ListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表信息的数据访问类
 * Created by ZQ on 2015/8/11.
 */
public class ListInfoDao implements IConstants {

    private Context mContext;

    public ListInfoDao(Context context){
        mContext = context;
    }

    private List<ListInfo> parseCursor(Cursor cursor){
        List<ListInfo> list = new ArrayList<ListInfo>();

        if(cursor.moveToFirst()){
            do{
                ListInfo info = new ListInfo();
                info.listName = cursor.getString(cursor.getColumnIndex(KEY_LISTNAME));
                info._id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                list.add(info);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ListInfo> getListInfo() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_LIST;

        return parseCursor(db.rawQuery(sql, null));
    }

    private boolean isInList(ListInfo list){
        return isInList(list.listName);
    }

    private boolean isInList(int listId){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_LIST
                + " where " + KEY_ID + " = " + listId;

        List<ListInfo> temp = parseCursor(db.rawQuery(sql, null));
        return temp.size() != 0;
    }

    private boolean isInList(String listName){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_LIST
                + " where " + KEY_LISTNAME + " = '" + listName + "'";

        List<ListInfo> temp = parseCursor(db.rawQuery(sql, null));
        return temp.size() != 0;
    }

    public int getListId(ListInfo list){
        return getListId(list.listName);
    }

    public int getListId(String listName){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from " + TABLE_LIST
                + " where " + KEY_LISTNAME + " = '" + listName + "'";
        List<ListInfo> temp = parseCursor(db.rawQuery(sql, null));
        if(temp.size() == 0) return -1;
        else return temp.get(0)._id;
    }

    public void createList(String listName){
        if(isInList(listName)) return;
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        ContentValues values = new ContentValues();
        values.put(KEY_LISTNAME, listName);
        db.insert(TABLE_LIST, null, values);
    }

    public void deleteList(String listName){
        if(!isInList(listName)) return;
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        db.delete(TABLE_LIST, "list_name = ?", new String[] { listName });
    }

    public void deleteListById(int listId){
        if(!isInList(listId)) return;
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        db.delete(TABLE_LIST, "_id = ?", new String[] { "" + listId});
    }

    public boolean hasData() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_LIST;
        Cursor cursor = db.rawQuery(sql, null);
        boolean has = false;
        if(cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if(count > 0) {
                has = true;
            }
        }
        cursor.close();
        return has;
    }

    public int getDataCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_LIST;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
}
