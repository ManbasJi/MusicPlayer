package com.manbas.downmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;

/**
 * Created by Administrator on 2016/12/21.
 */

public class Player extends Service{

    public MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Timer mTimer=new Timer();

    public Player(){
        try {
            mediaPlayer=new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setOnBufferingUpdateListener(this);
//            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public Player(SeekBar seekBar){
//        this.seekBar=seekBar;
//
//
//        mTimer.schedule(timerTask,0,1000);
//
//    }
//
//    TimerTask timerTask = new TimerTask() {
//
//        @Override
//        public void run() {
//            if (mediaPlayer == null)
//                return;
//            if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
//                handler.sendEmptyMessage(0); // 发送消息
//            }
//        }
//    };

//    Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            int position = mediaPlayer.getCurrentPosition();
//            int duration = mediaPlayer.getDuration();
//            if (duration > 0) {
//                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
//                long pos = seekBar.getMax() * position / duration;
//                seekBar.setProgress((int) pos);
//            }
//        };
//    };


    public void playorpause() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }else{
            mediaPlayer.pause();
        }
    }

    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MyPlayer binder=new MyPlayer();


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class MyPlayer extends Binder{
        public Player getService(){
            return Player.this;
        }
    }

//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        seekBar.setSecondaryProgress(percent);
//        int currentProgress = seekBar.getMax()
//                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
//        Log.e(currentProgress + "% play", percent + " buffer");
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        Log.e("mediaPlayer", "onCompletion");
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        Log.e("mediaPlayer", "onPrepared");
//    }
}
