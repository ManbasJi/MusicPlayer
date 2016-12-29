package com.manbas.downmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.manbas.downmusic.R;
import com.manbas.downmusic.activity.PlayingActivity;
import com.manbas.downmusic.activity.SongListActivity;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/27.
 */

public class MediaPlayService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{
    public MediaPlayer mediaPlayer;
    public static int PLAYING=1;
    public static int PAUSING=2;
    public int state=0;

    private Context mContext;
    private Notification notification;
    private Notification.Builder builder;
    private NotificationManager mNotificationManager;

    public String URL="";

    MyBindler myBindler=new MyBindler();


    public MediaPlayService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.prepareAsync();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent it = new Intent();
//        it.addCategory(Intent.CATEGORY_LAUNCHER);
        it.setComponent(new ComponentName(this, PlayingActivity.class));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//设置启动模式

        PendingIntent pi = PendingIntent.getActivity(mContext, 0,
                it, PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new Notification.Builder(mContext);
        builder.setSmallIcon(R.mipmap.play);
        builder.setContentTitle("manbasji's mediaplayer");
        builder.setTicker("MEIDAPLAYER");
        builder.setContentText("");
        builder.setContentIntent(pi);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        startForeground(1, notification);

        return START_STICKY;
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
