package com.manbas.downmusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.manbas.downmusic.R;
import com.manbas.downmusic.TestMediaPlayer;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/27.
 */

public class TestService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{
    public MediaPlayer mediaPlayer;
    public static int PLAYING=1;
    public static int PAUSING=2;
    public int state=0;

    MyBindler myBindler=new MyBindler();


    public TestService() {
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource("http://abv.cn/music/光辉岁月.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);//防止手机休眠时终止掉服务

        mediaPlayer.prepareAsync();

        String songName;
        PendingIntent pi=PendingIntent.getActivity(this,0,
                new Intent(this, TestMediaPlayer.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder=new Notification.Builder(this);
        builder.setContentTitle("Bmob Test");
        builder.setContentText("mediaplay");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pi);
        Notification notification=builder.build();
        notification.tickerText="mediaplay";
        notification.icon= R.mipmap.play;
        notification.flags|=Notification.FLAG_ONGOING_EVENT;
        startForeground(0, notification);
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
//        mp.start();
    }


    public class MyBindler extends Binder{

        public TestService getService(){
            return TestService.this;
        }
    }
}
