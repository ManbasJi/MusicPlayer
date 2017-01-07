package com.manbas.downmusic.config;

import android.os.Environment;

import com.manbas.downmusic.bean.SongMsgBean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */

public class Config {
    public static List<SongMsgBean> songMsgBeanList;

    public static final String LRCPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/manbasjiMusic/";

    public static Integer MUSIC_INDEX;

//    /*截图屏幕宽高*/
//    private static final float BASE_SCREEN_WIDTH = (float) 1080.0;
//    private static final float BASE_SCREEN_HEIGHT = (float) 1920.0;
//    public static final float SCALE_DISC_SIZE = (float) (813.0 / BASE_SCREEN_WIDTH);
//    /*专辑图片比例*/
//    public static final float SCALE_MUSIC_PIC_SIZE = (float) (533.0 / BASE_SCREEN_WIDTH);
//
//    /*唱盘比例*/
//    public static final float SCALE_DISC_MARGIN_TOP = (float) (190 / BASE_SCREEN_HEIGHT);
//
//    /*设备屏幕宽度*/
//    public static int getScreenWidth(Context context) {
//        return context.getResources().getDisplayMetrics().widthPixels;
//    }
//
//    /*设备屏幕高度*/
//    public static int getScreenHeight(Context context) {
//        return context.getResources().getDisplayMetrics().heightPixels;
//    }


    public static final String HOST="http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=";

    public static final String GetMusicListApi=HOST+"baidu.ting.billboard.billList";//获取列表
    public static final String SearchSongApi=HOST+"baidu.ting.search.catalogSug";//搜索
    public static final String PlayApi=HOST+"baidu.ting.song.play";//播放
    public static final String LRCApi=HOST+"baidu.ting.song.lry";//歌词
    public static final String RecommandSongList=HOST+"baidu.ting.song.getRecommandSongList";//推荐列表
    public static final String GetArtistInfoApi=HOST+"baidu.ting.artist.getInfo";//获取歌手信息
    public static final String GetArtistSongListApi=HOST+"baidu.ting.artist.getSongList";//获取歌手歌曲列表
    public static final String DownApi=HOST+"baidu.ting.song.downWeb";//下载



}
