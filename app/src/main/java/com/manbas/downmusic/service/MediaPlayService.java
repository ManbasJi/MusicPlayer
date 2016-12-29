package com.manbas.downmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/27.
 */

public class MediaPlayService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{
    public MediaPlayer mediaPlayer;
    public static int PLAYING=1;
    public static int PAUSING=2;
    public int state=0;

    public String URL="";

    MyBindler myBindler=new MyBindler();


    public MediaPlayService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.prepareAsync();
    }

    public void playUrl(String url){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);//设置数据源
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
    }

    public void playOrPause(){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            state=PAUSING;
        }else{
            mediaPlayer.start();
            state=PLAYING;
        }
    }

    public void stop(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBindler;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public class MyBindler extends Binder{
        public MediaPlayService getService(){
            return MediaPlayService.this;
        }
    }
}
