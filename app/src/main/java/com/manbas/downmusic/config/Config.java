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
