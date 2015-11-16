package com.lin.dlivkfragment.interfaces;

/**
 * 保存APP中所有的公用常量，若需使用这些常量请实现此接口
 * Created by ZQ on 2015/7/20.
 */
public interface IConstants {

    //General Name
    public static final String BROADCAST_NAME = "com.xiaopeng.musicplayer.broadcast";
    public static final String SERVICE_NAME = "com.lin.dlivkfragment.service.MediaService";

    //Database
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "xiaopeng_music";
    public static final String TABLE_MUSIC = "music";
    public static final String TABLE_LIST = "list";
    public static final String TABLE_MUSICLIST = "musiclist";

    //InfoDao
    public static final String KEY_MUSIC = "music";
    public static final String KEY_ID = "_id";
    public static final String KEY_SONG_ID = "songid";
    public static final String KEY_ALBUM_ID = "albumid";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_MUSIC_NAME = "musicname";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_DATA = "data";
    public static final String KEY_FOLDER = "folder";
    public static final String KEY_MUSICNAME_KEY = "musicnamekey";
    public static final String KEY_ARTIST_KEY = "artistkey";
    public static final String KEY_FAVORITE = "favorite";
    public static final String KEY_LISTNAME = "list_name";
    public static final String KEY_LISTID = "listid";

    //SharedPreferences
    public static final String SP_NAME = "com.xiaopeng.musicplayer.sharedpreferences";
    public static final String SP_BG_PATH = "bg_path";
    public static final String SP_FILTER_SIZE = "filter_size";
    public static final String SP_FILTER_TIME = "filter_time";

    //Play Mode
    public static final int MPM_LIST_LOOP_PLAY = 0;
    public static final int MPM_SINGLE_LOOP_PLAY = 1;
    public static final int MPM_RANDOM_PLAY = 2;

    //Play State
    public static final int MPS_NOFILE = -1;
    public static final int MPS_INVALID = 0;
    public static final int MPS_PREPARE = 1;
    public static final int MPS_PLAYING = 2;
    public static final int MPS_PAUSE = 3;

    public static final String PLAY_STATE_NAME = "PLAY_STATE_NAME";
    public static final String PLAY_MUSIC_INDEX = "PLAY_MUSIC_INDEX";

    //MusicUtils
    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟
}
