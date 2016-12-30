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
import android.os.Message;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.manbas.downmusic.MvpView.PlayingView;
import com.manbas.downmusic.R;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.SingleSongInfoBean;
import com.manbas.downmusic.bean.SongMsgBean;
import com.manbas.downmusic.presenter.PlayingPresenter;
import com.manbas.downmusic.service.MediaPlayService;
import com.manbas.downmusic.utlis.ToastUtils;
import com.manbas.downmusic.view.CircleTransform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/29.
 */

public class PlayingActivity extends Activity implements PlayingView,MediaPlayer.OnBufferingUpdateListener{


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
    @BindView(R.id.tv_currentTime)
            TextView tv_currentTime;
    @BindView(R.id.tv_durationTime)
            TextView tv_durationTime;

    SingleSongInfoBean singleSongInfoBean;
    PlayingPresenter playingPresenter;
    MediaPlayService mediaPlayService;
    ObjectAnimator objectAnimator,objectAnimator1;
    SimpleDateFormat format=new SimpleDateFormat("mm:ss");

    String musicUrl="";
    String songName="";
    String artistName="";
    String songId="";
    String songPic="";

    int index;
    List<SongMsgBean> songlist;

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
                String currentTime=format.format(position);
                tv_currentTime.setText(currentTime);
            }
            handler.postDelayed(runnable, 1000);
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
        playingPresenter=new PlayingPresenter(this,this);
        songlist= (List<SongMsgBean>) getIntent().getSerializableExtra("songList");
        LogUtis.Log("songListSize"+songlist.size());
        index=Integer.parseInt(getIntent().getStringExtra("index"));
        if(songlist!=null){
            LogUtis.Log("songId:"+songId);
            playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
        }else{
            ToastUtils.showShortToast(this,"获取歌曲失败!");
        }
        singleSongInfoBean=new SingleSongInfoBean();


//        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initPlay(final String url){
          /*
        * 延迟1.5秒后进行播放音乐（等待Service完成）*/

        if(mediaPlayService==null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayService.playUrl(url);
                    onListener();
                    handler.post(runnable);
                    objectAnimator.start();
                    objectAnimator1.start();

                    LogUtis.Log("durationTime:"+format.format(mediaPlayService.mediaPlayer.getDuration()));
                    LogUtis.Log("ivPlay.performClick()");
                }
            }, 1000);
        }else{
            mediaPlayService.playUrl(url);
            onListener();
            handler.post(runnable);
            objectAnimator.start();
            objectAnimator1.start();
            tv_durationTime.setText(format.format(mediaPlayService.mediaPlayer.getDuration()));
            LogUtis.Log("durationTime:"+format.format(mediaPlayService.mediaPlayer.getDuration()));
        }
    }

    private void initView(){
        tv_durationTime.setText(format.format(new Date(songlist.get(index).getFile_duration()*1000)));
            tvSongname.setText(songlist.get(index).getTitle());
            tvArtistName.setText(songlist.get(index).getArtist_name());
        Glide.with(this).load(songPic).transform(new CircleTransform(this)).into(ivSongImage);
    }

    private void onBindService(){
        Intent intent=new Intent(this,MediaPlayService.class);
        startService(intent);
        bindService(intent,serviceConnection,MediaPlayService.BIND_AUTO_CREATE);
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

                if(index==0){
                    index=songlist.size()-1;
                }else{
                    index-=1;
                }
                playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
                ivPlay.setImageResource(R.mipmap.pause);
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
                if(index==songlist.size()-1){
                    index=0;
                }else{
                    index+=1;
                }
                playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
                ivPlay.setImageResource(R.mipmap.pause);
                break;
        }
    }

    @Override
    public void onPlayUrl(SingleSongInfoBean bean) {
        singleSongInfoBean=bean;
        songPic=bean.getSonginfo().getPic_premium();
        musicUrl=bean.getBitrat().getFile_link();
        initPlay(musicUrl);
        initView();
    }

    @Override
    public void toastMsg(String msg) {

    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtis.Log("seekbarprogress:"+progress+"|"+mediaPlayService.mediaPlayer.getDuration());
            this.progress = progress * mediaPlayService.mediaPlayer.getDuration()
                    / seekBar.getMax();
            if(progress==100){
                new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        ivNext.performClick();
                        LogUtis.Log("performClick");
                    }
                }.sendEmptyMessageDelayed(0,1500);
            }
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
//        mediaPlayService.stop();
    }
}
