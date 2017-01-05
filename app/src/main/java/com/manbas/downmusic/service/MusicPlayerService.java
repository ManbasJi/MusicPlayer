//package com.manbas.downmusic.service;
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.MediaPlayer;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
//
//import com.manbas.downmusic.bean.SongMsgBean;
//import com.manbas.downmusic.config.Config;
//
//import java.util.List;
//
///**
// * Created by Administrator on 2017/1/5.
// */
//
//public class MusicPlayerService  extends Service{
//    /*操作指令*/
//    public static final String ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY";
//    public static final String ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE";
//    public static final String ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT";
//    public static final String ACTION_OPT_MUSIC_LAST = "ACTION_OPT_MUSIC_LAST";
//    public static final String ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO";
//
//    /*状态指令*/
//    public static final String ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY";
//    public static final String ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE";
//    public static final String ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE";
//    public static final String ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION";
//
//    public static final String PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION";
//    public static final String PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO";
//    public static final String PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION";
//    public static final String PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER";
//
//    private List<SongMsgBean> mlist;
//    private int musicIndex;
//    private String musicUrl;
//
//    public static MediaPlayer mediaPlayer;
//    MusicReciver musicReciver;
//
//    public MusicPlayerService() {
//        mediaPlayer=new MediaPlayer();
//        musicReciver=new MusicReciver();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        initData(intent);
//        return super.onStartCommand(intent, flags, startId);
//
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initBroadCast();
//    }
//
//    private void initData(Intent intent){
//        musicIndex=Integer.parseInt(intent.getStringExtra("index"));
//        mlist= Config.songMsgBeanList;
//
//    }
//
//    private void initBroadCast(){
//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY);
//        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE);
//        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT);
//        intentFilter.addAction(ACTION_OPT_MUSIC_LAST);
//        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO);
//        LocalBroadcastManager.getInstance(this).registerReceiver(musicReciver,intentFilter);
//    }
//
//    private void play(){
//
//    }
//
//    private void pause(){
//
//    }
//
//
//    private void next(){
//
//    }
//
//    private void last(){
//
//    }
//
//    private void seekTo(){
//
//    }
//
//
////
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    class MusicReciver extends BroadcastReceiver{
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action=intent.getAction();
//            switch (action){
//                case ACTION_OPT_MUSIC_PLAY:
////                    break;
//                case ACTION_OPT_MUSIC_PAUSE:
//
//                    break;
//                case ACTION_OPT_MUSIC_NEXT:
//
//                    break;
//                case ACTION_OPT_MUSIC_LAST:
//
//                    break;
//                case ACTION_OPT_MUSIC_SEEK_TO:
//
//                    break;
//            }
//        }
//    }
//}
