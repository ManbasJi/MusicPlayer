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
    private Context mContext;
    private Notification notification;
    private Notification.Builder builder;
    private NotificationManager mNotificationManager;

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

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent it = new Intent(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);
        it.setComponent(new ComponentName(this, TestMediaPlayer.class));
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


    @Override
    public void onDestroy() {
        super.onDestroy();
//        mNotificationManager.cancel(1);
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
