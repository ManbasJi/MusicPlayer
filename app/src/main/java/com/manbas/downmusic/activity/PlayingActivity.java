package com.manbas.downmusic.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manbas.downmusic.R;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.service.MediaPlayService;
import com.manbas.downmusic.view.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/29.
 */

public class PlayingActivity extends Activity implements MediaPlayer.OnBufferingUpdateListener{


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_songname)
    TextView tvSongname;
    @BindView(R.id.tv_artistName)
    TextView tvArtistName;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.iv_songImage)
    ImageView ivSongImage;
    @BindView(R.id.iv_cd)
    ImageView ivCd;
    @BindView(R.id.seekbar)
    AppCompatSeekBar seekbar;
    @BindView(R.id.iv_previous)
    ImageView ivPrevious;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;

    MediaPlayService mediaPlayService;
    ObjectAnimator objectAnimator,objectAnimator1;

    String musicUrl="";
    String songName="";
    String artistName="";
    String songImage = "";

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int position = mediaPlayService.mediaPlayer.getCurrentPosition();
            int duration = mediaPlayService.mediaPlayer.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                long pos = seekbar.getMax() * position / duration;
                seekbar.setProgress((int) pos);
            }
            handler.postDelayed(runnable, 200);
        }
    };


    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaPlayService=((MediaPlayService.MyBindler)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaPlayService=null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        ButterKnife.bind(this);
        onBindService();
        musicUrl=getIntent().getStringExtra("musicUrl");
        songName=getIntent().getStringExtra("songName");
        artistName=getIntent().getStringExtra("artistName");
        songImage = getIntent().getStringExtra("songImage");
        initView();
        initPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initPlay(){
          /*
        * 延迟1.5秒后进行播放音乐（等待Service完成）*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayService.playUrl("http://yinyueshiting.baidu.com/data2/music/42783748/42783748.mp3?xcode=3f58d8f080eebe32cc3224dc97e4d7a9");
                onListener();
                handler.post(runnable);
                objectAnimator.start();
                objectAnimator1.start();
                LogUtis.Log("ivPlay.performClick()");
            }
        }, 1000);
    }

    private void initView(){
            tvSongname.setText(songName);
            tvArtistName.setText(artistName);
        Glide.with(this).load(songImage).transform(new CircleTransform(this)).into(ivSongImage);
    }

    private void onBindService(){
        Intent intent=new Intent(this,MediaPlayService.class);
        startService(intent);
        bindService(intent,serviceConnection,MediaPlayService.BIND_AUTO_CREATE);
//        mediaPlayService.playOrPause();
        setAnimator();

    }


    private void onListener(){
        seekbar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        mediaPlayService.mediaPlayer.setOnBufferingUpdateListener(this);
    }

    private void setAnimator(){
        objectAnimator=ObjectAnimator.ofFloat(ivCd,"rotation",0f,360.0f);
        objectAnimator.setDuration(10000);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setInterpolator(new LinearInterpolator());

        objectAnimator1=ObjectAnimator.ofFloat(ivSongImage,"rotation",0f,360.0f);
        objectAnimator1.setDuration(10000);
        objectAnimator1.setRepeatCount(-1);
        objectAnimator1.setInterpolator(new LinearInterpolator());
    }






    @OnClick({R.id.iv_back, R.id.iv_share, R.id.iv_songImage,R.id.iv_previous, R.id.iv_play, R.id.iv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_songImage:
                break;
            case R.id.iv_previous:
                break;
            case R.id.iv_play:
                if(mediaPlayService!=null){
                    mediaPlayService.playOrPause();
                        if(mediaPlayService.mediaPlayer.isPlaying()){
                            ivPlay.setImageResource(R.mipmap.pause);
                            objectAnimator.start();
                            objectAnimator1.start();
                        }else{
                            ivPlay.setImageResource(R.mipmap.play);
                            objectAnimator.pause();
                            objectAnimator1.pause();
                        }
                }
                break;
            case R.id.iv_next:
                break;
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress * mediaPlayService.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayService.mediaPlayer.seekTo(progress);
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekbar.setSecondaryProgress(percent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayService.stop();
    }
}
