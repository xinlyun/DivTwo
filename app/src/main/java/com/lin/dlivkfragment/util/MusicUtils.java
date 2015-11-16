package com.lin.dlivkfragment.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;


import com.lin.dlivkfragment.interfaces.IConstants;
import com.lin.dlivkfragment.MusicApp;
import com.lin.dlivkfragment.R;
import com.lin.dlivkfragment.db.DatabaseHelper;
import com.lin.dlivkfragment.db.ListInfoDao;
import com.lin.dlivkfragment.db.MusicInfoDao;
import com.lin.dlivkfragment.db.MusicListDao;
import com.lin.dlivkfragment.model.ListInfo;
import com.lin.dlivkfragment.model.MusicInfo;
import com.lin.dlivkfragment.model.MusicListInfo;
import com.lin.dlivkfragment.storage.SPStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于管理音乐的工具类，封装了所有数据库操作，并且提供专辑图获取和音乐信息检索的静态方法
 * 如果以后要扩充数据库功能，请在此类中增加相应的封装
 * Created by zzq on 15-7-27.
 */
public class MusicUtils implements IConstants {

    private Context mContext = MusicApp.getContext();

    private MusicInfoDao mMusicInfoDao;
    private ListInfoDao mListInfoDao ;
    private MusicListDao mMusicListDao;

    private int favoriteListId;

    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final HashMap<Long, Bitmap> sArtCache = new HashMap<Long, Bitmap>();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private static String[] proj_music = new String[] {
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION };

    public MusicUtils(){
        mContext = MusicApp.getContext();

        mMusicInfoDao = new MusicInfoDao(mContext);
        mListInfoDao = new ListInfoDao(mContext);
        mMusicListDao = new MusicListDao(mContext);

        favoriteListId = mListInfoDao.getListId(
                mContext.getString(R.string.favorite_list_name));
    }

    //返回特定列表的所有歌曲信息
    public List<MusicInfo> getMusicInfoList(){
        return mMusicInfoDao.getMusicInfo();
    }

    public List<MusicInfo> getMusicInfoList(String listName){
        if(listName.equals(MusicApp.getContext().getString(R.string.all_list_name))){
            return mMusicInfoDao.getMusicInfo();
        }
        int listId = mListInfoDao.getListId(listName);
        return getMusicInfoList(listId);
    }

    public List<MusicInfo> getMusicInfoList(ListInfo list){
        if(list.listName.equals(MusicApp.getContext().getString(R.string.all_list_name))){
            return mMusicInfoDao.getMusicInfo();
        }
        List<MusicListInfo> temp = mMusicListDao.getMusicListInfo(list);
        if(temp.size() == 0) return null;
        else return getMusicInfoList(temp);
    }

    private List<MusicInfo> getMusicInfoList(int listId){
        List<MusicListInfo> temp = mMusicListDao.getMusicListInfo(listId);
        if(temp.size() == 0) return null;
        else return getMusicInfoList(temp);
    }

    private List<MusicInfo> getMusicInfoList(List<MusicListInfo> listInfo){
        List<MusicInfo> result = new ArrayList<>();
        for(MusicListInfo musicListInfo : listInfo){
            result.add(mMusicInfoDao.getMusicInfoById(musicListInfo.songId));
        }
        return result;
    }

    //设置歌曲的＂我的最爱＂状态
    public void setFavoriteState(int id, int favorite) {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "update " + TABLE_MUSIC + " set favorite = " + favorite + " where songid = " + id;
        if(favorite == 1){
            mMusicListDao.insertToListById(id, favoriteListId);
        }else if(favorite == 0){
            mMusicListDao.deleteFromListById(id, favoriteListId);
        }

        db.execSQL(sql);
    }

    //添加歌曲到列表
    public void insertToList(String listName, int id){
        mMusicListDao.insertToListById(id, mListInfoDao.getListId(listName));
    }

    public void insertToList(String listName, MusicInfo music){
        insertToList(listName, music.songId);
    }

    //从列表中删除歌曲
    public void deleteFromList(String listName, int id){
        mMusicListDao.deleteFromListById(id, mListInfoDao.getListId(listName));
    }

    public void deleteFromList(String listName, MusicInfo music){
        deleteFromList(listName, music.songId);
    }


    public void deleteMusicInfo(MusicInfo music){
        mMusicInfoDao.deleteMusicInfo(music);
        mMusicListDao.deleteMusicInfo(music);
    }

    public void clearData(){
        mMusicInfoDao.deleteMusicInfo();
        mMusicListDao.deleteMusicInfo();
    }

    //获取所有播放列表
    public List<ListInfo> getListInfo(){
        return mListInfoDao.getListInfo();
    }

    //添加播放列表
    public void createList(String listName){
        mListInfoDao.createList(listName);
    }

    public boolean musicHasData(){
        return mMusicInfoDao.hasData();
    }

    //删除播放列表
    public void deleteList(String listName) {
        deleteList(mListInfoDao.getListId(listName));
    }

    public void deleteList(int listId){
        mListInfoDao.deleteListById(listId);
        mMusicListDao.deleteFromListById(listId);
    }

    //根据歌曲的ID，寻找出歌曲在当前播放列表中的位置
    public static int seekPosInListById(List<MusicInfo> list, int id) {
        if(id == -1 || list == null) {
            return -1;
        }
        int result = -1;

        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).songId) {
                result = i;
                break;
            }
        }

        return result;
    }

    public List<ListInfo> queryList(Context context){
        if(mListInfoDao == null){
            mListInfoDao = new ListInfoDao(context);
        }

        return mListInfoDao.getListInfo();
    }

    public static int seekListPos(List<ListInfo> list, String listName){
        if(listName.equals("") || list == null || list.size() == 0){
            return -1;
        }
        if(MusicApp.getContext().getString(R.string.all_list_name).equals(listName)){
            return 0;
        }
        int result = -1;
        for(int i = 0; i < list.size(); i++){
            if (listName.equals(list.get(i).listName)){
                result = i;
                break;
            }
        }
        return result;
    }

//    /**
//     * 获取包含音频文件的文件夹信息
//     * @param context
//     * @return
//     */
//    public static List<FolderInfo> queryFolder(Context context) {
//        if(mFolderInfoDao == null) {
//            mFolderInfoDao = new FolderInfoDao(context);
//        }
//        SPStorage sp = new SPStorage(context);
//        Uri uri = MediaStore.Files.getContentUri("external");
//        ContentResolver cr = context.getContentResolver();
//        StringBuilder mSelection = new StringBuilder(MediaStore.Files.FileColumns.MEDIA_TYPE
//                + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " and " + "("
//                + MediaStore.Files.FileColumns.DATA + " like'%.mp3' or " + MediaStore.Audio.Media.DATA
//                + " like'%.wma')");
//        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
//        if(sp.getFilterSize()) {
//            mSelection.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
//        }
//        if(sp.getFilterTime()) {
//            mSelection.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
//        }
//        mSelection.append(") group by ( " + MediaStore.Files.FileColumns.PARENT);
//        if (mFolderInfoDao.hasData()) {
//            return mFolderInfoDao.getFolderInfo();
//        } else {
//            List<FolderInfo> list = getFolderList(cr.query(uri, proj_folder, mSelection.toString(), null, null));
//            mFolderInfoDao.saveFolderInfo(list);
//            return list;
//        }
//    }

//    public static List<MusicInfo> queryFavorite(Context context) {
//        if(mFavoriteInfoDao == null) {
//            mFavoriteInfoDao = new FavoriteInfoDao(context);
//        }
//        return mFavoriteInfoDao.getMusicInfo();
//    }

    //查找音乐文件
    public List<MusicInfo> queryMusic(Context context){
        return queryMusic(context, null, null);
    }

    public List<MusicInfo> queryMusic(Context context, String selections, String selection){
        Log.d("MusicUtils","queryMusic:"+mMusicInfoDao.hasData()+" "+mMusicInfoDao.getMusicInfo().size());


        if(mMusicInfoDao == null){
            mMusicInfoDao = new MusicInfoDao(context);
        }
        SPStorage sp = new SPStorage(context);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();

        StringBuilder select = new StringBuilder(" 1=1 ");
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        if(sp.getFilterSize()){
            select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        }
        if(sp.getFilterTime()){
            select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        }
        if(!TextUtils.isEmpty(selections)){
            select.append(selections);
        }

        if(mMusicInfoDao.hasData()){
            if(mMusicInfoDao.getDataCount()!=0)
                return mMusicInfoDao.getMusicInfo();
            else {
                List<MusicInfo> list = getMusicList(cr.query(uri, proj_music,
                        select.toString(), null, MediaStore.Audio.Media.ARTIST_KEY));
                mMusicInfoDao.saveMusicInfo(list);
                return list;
            }
        }else{
            List<MusicInfo> list = getMusicList(cr.query(uri, proj_music,
                    select.toString(), null, MediaStore.Audio.Media.ARTIST_KEY));
            mMusicInfoDao.saveMusicInfo(list);
            return list;
        }
    }

    public static ArrayList<MusicInfo> getMusicList(Cursor cursor){
        if(cursor == null){
            return null;
        }
        ArrayList<MusicInfo> musicList = new ArrayList<MusicInfo>();
        while(cursor.moveToNext()){
            MusicInfo music = new MusicInfo();
            music.songId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            music.albumId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            music.duration = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            music.musicName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE));
            music.artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));

            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            music.data = filePath;
            music.folder = filePath.substring(0, filePath.lastIndexOf(File.separator));
            music.musicNameKey = StringHelper.getPingYin(music.musicName);
            music.artistkey = StringHelper.getPingYin(music.artist);
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }

    public static Bitmap getCachedArtwork(Context context, long artIndex,
                                          Bitmap defaultArtwork) {
        Bitmap bitmap = null;
        synchronized (sArtCache) {
            bitmap = sArtCache.get(artIndex);
        }
        if(context == null) {
            return null;
        }
        if (bitmap == null  ) {
            bitmap = defaultArtwork;
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Bitmap b = MusicUtils.getArtworkQuick(context, artIndex, w, h);
            if (b != null) {
                bitmap = b;
                synchronized (sArtCache) {
                    // the cache may have changed since we checked
                    Bitmap value = sArtCache.get(artIndex);
                    if (value == null) {
                        sArtCache.put(artIndex, bitmap);
                    } else {
                        bitmap = value;
                    }
                }
            }
        }
        return bitmap;
    }

    // A really simple BitmapDrawable-like class, that doesn't do
    // scaling, dithering or filtering.
	/*
	 * private static class FastBitmapDrawable extends Drawable { private Bitmap
	 * mBitmap; public FastBitmapDrawable(Bitmap b) { mBitmap = b; }
	 *
	 * @Override public void draw(Canvas canvas) { canvas.drawBitmap(mBitmap, 0,
	 * 0, null); }
	 *
	 * @Override public int getOpacity() { return PixelFormat.OPAQUE; }
	 *
	 * @Override public void setAlpha(int alpha) { }
	 *
	 * @Override public void setColorFilter(ColorFilter cf) { } }
	 */

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will
    // it attempt to repair the database.
    public static Bitmap getArtworkQuick(Context context, long album_id, int w,
                                         int h) {
        // NOTE: There is in fact a 1 pixel border on the right side in the
        // ImageView
        // used to display this drawable. Take it into account now, so we don't
        // have to
        // scale later.
        w -= 1;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                // Compute the closest power-of-two scale factor
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(),
                        null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth > w && nextHeight > h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w
                            || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        // Bitmap.createScaledBitmap() can return the same
                        // bitmap
                        if (tmp != b)
                            b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
