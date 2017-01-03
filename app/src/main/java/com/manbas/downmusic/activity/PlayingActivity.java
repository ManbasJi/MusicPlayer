package com.manbas.downmusic.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.manbas.downmusic.MvpView.PlayingView;
import com.manbas.downmusic.R;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.LyricContent;
import com.manbas.downmusic.bean.SingleSongInfoBean;
import com.manbas.downmusic.bean.SongMsgBean;
import com.manbas.downmusic.config.Config;
import com.manbas.downmusic.fragment.PlayingForCDFragment;
import com.manbas.downmusic.fragment.PlayingForLrcFragment;
import com.manbas.downmusic.presenter.PlayingPresenter;
import com.manbas.downmusic.service.MediaPlayService;
import com.manbas.downmusic.utlis.LrcRead;
import com.manbas.downmusic.utlis.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/29.
 */

public class PlayingActivity extends Activity implements PlayingView, MediaPlayer.OnBufferingUpdateListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_songname)
    TextView tvSongname;
    @BindView(R.id.tv_artistName)
    TextView tvArtistName;
    @BindView(R.id.iv_share)
    ImageView ivShare;

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
    SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    PlayingForCDFragment forCDFragment=new PlayingForCDFragment();

    String musicUrl = "";
    String songName = "";
    String artistName = "";
    String songId = "";
    String songPic = "";
    int CurrentTime=0;
    int CountTime=0;

    int tag=0;
    boolean isShowCd=true;

    int index;
    List<SongMsgBean> songlist;
    List<LyricContent> lyricList=new ArrayList<>();

    Handler mHandler;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int position = mediaPlayService.mediaPlayer.getCurrentPosition();
            int duration = mediaPlayService.mediaPlayer.getDuration();
            if(duration > 0){
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                long pos = seekbar.getMax() * position / duration;
                seekbar.setProgress((int) pos);
                String currentTime = format.format(position);
                tv_currentTime.setText(currentTime);
                LogUtis.Log("currentTime:"+currentTime+"|position:"+position);
            }
            handler.postDelayed(runnable, 1000);
        }
    };


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaPlayService = ((MediaPlayService.MyBindler) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaPlayService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_activity);
        setTopState();//顶部状态栏透明
        ButterKnife.bind(this);
        onBindService();//绑定服务并开启
        playingPresenter = new PlayingPresenter(this, this);
        songlist = (List<SongMsgBean>) getIntent().getSerializableExtra("songList");
        LogUtis.Log("songListSize" + songlist.size());
        index = Integer.parseInt(getIntent().getStringExtra("index"));
        playAndLoadLrc();//加载歌曲和歌词
        singleSongInfoBean = new SingleSongInfoBean();
    }

    private void setFragment(Fragment f){
        Bundle b=new Bundle();
        b.putString("imageUrl",songPic);
        b.putSerializable("lyricList", (Serializable) lyricList);
        f.setArguments(b);
        FragmentManager fragmentManager=getFragmentManager();
        android.app.FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_middleContent,f);
        transaction.commit();
    }

    public void setmHandler(Handler handler){
        this.mHandler=handler;
    }

    /*播放和加载歌词
    * */
    private void playAndLoadLrc(){
        if (songlist != null) {
            playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
            File file=new File(Config.LRCPath+songlist.get(index).getSong_id()+".lrc");
            LogUtis.Log("lrcfilePath"+Config.LRCPath+songlist.get(index).getSong_id()+".lrc");
            if(!file.exists()){
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                new LrcRead().ReadOnline(songlist.get(index).getLrclink(),songlist.get(index).getSong_id());
                                try {
                                    lyricList=new LrcRead().Read(Config.LRCPath+songlist.get(index).getSong_id()+".lrc");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).start();

            }else{
                try {
                    lyricList= new LrcRead().Read(Config.LRCPath+songlist.get(index).getSong_id()+".lrc");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtils.showShortToast(this, "获取歌曲失败!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initPlay(final String url) {
          /*
        * 延迟1.5秒后进行播放音乐（等待Service完成）*/
        if (mediaPlayService == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayService.playUrl(url);
                    onListener();
                    handler.post(runnable);
                    Message message=Message.obtain();
                    message.what=1;
                    mHandler.sendMessage(message);
                    LogUtis.Log("durationTime:" + format.format(mediaPlayService.mediaPlayer.getDuration()));
                    LogUtis.Log("ivPlay.performClick()");
                }
            }, 1000);
        } else {
            mediaPlayService.playUrl(url);
            onListener();
            handler.post(runnable);
            Message message=Message.obtain();
            message.what=1;
            if(mHandler!=null){
                mHandler.sendMessage(message);
            }

//            tv_durationTime.setText(format.format(mediaPlayService.mediaPlayer.getDuration()));
            LogUtis.Log("durationTime:" + format.format(mediaPlayService.mediaPlayer.getDuration()));
        }
    }

    private void initView() {
        tv_durationTime.setText(format.format(new Date(songlist.get(index).getFile_duration() * 1000)));
        tvSongname.setText(songlist.get(index).getTitle());
        tvArtistName.setText(songlist.get(index).getArtist_name());
    }

    private void onBindService() {
        Intent intent = new Intent(this, MediaPlayService.class);
        startService(intent);
        bindService(intent, serviceConnection, MediaPlayService.BIND_AUTO_CREATE);
    }


    private void onListener() {
        seekbar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        mediaPlayService.mediaPlayer.setOnBufferingUpdateListener(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.iv_previous, R.id.iv_play, R.id.iv_next,R.id.fl_middleContent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_middleContent:
                if(tag==0){
                    setFragment(new PlayingForLrcFragment());
                    tag=1;
                }else{
                    setFragment(new PlayingForCDFragment());
                    tag=0;
                }

                break;

            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_songImage:
                break;
            case R.id.iv_previous:
                isShowCd=false;
                if (index == 0) {
                    index = songlist.size() - 1;
                } else {
                    index -= 1;
                }
                playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
                ivPlay.setImageResource(R.mipmap.pause);
                break;
            case R.id.iv_play:
                if (mediaPlayService != null) {
                    mediaPlayService.playOrPause();
                    if (mediaPlayService.mediaPlayer.isPlaying()) {
                        ivPlay.setImageResource(R.mipmap.pause);
                        Message message=Message.obtain();
                        message.what=1;
//                        mHandler.sendMessage(message);
                    } else {
                        ivPlay.setImageResource(R.mipmap.play);
                        Message message=Message.obtain();
                        message.what=2;
//                        mHandler.sendMessage(message);
                    }
                }
                break;
            case R.id.iv_next:
                isShowCd=false;
                if (index == songlist.size() - 1) {
                    index = 0;
                } else {
                    index += 1;
                }
                playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
                ivPlay.setImageResource(R.mipmap.pause);
                break;
        }
    }

    @Override
    public void onPlayUrl(SingleSongInfoBean bean) {
        singleSongInfoBean = bean;
        songPic = bean.getSonginfo().getPic_premium();
        musicUrl = bean.getBitrat().getFile_link();
        if(isShowCd){
            setFragment(forCDFragment);
        }
        initPlay(musicUrl);
        initView();


    }

    @Override
    public void toastMsg(String msg) {

    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtis.Log("seekbarprogress:" + progress + "|" + mediaPlayService.mediaPlayer.getDuration());
            this.progress = progress * mediaPlayService.mediaPlayer.getDuration()
                    / seekBar.getMax();
            if (progress == 100) {
                isShowCd=false;
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        ivNext.performClick();
                        LogUtis.Log("performClick");
                    }
                }.sendEmptyMessageDelayed(0, 1500);
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
//        handler.removeCallbacks(runnable);
        unbindService(serviceConnection);
//        mediaPlayService.stop();
    }

    /*
    * 设置顶部状态栏透明*/
    private void setTopState(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    }

    private int indexc=0;
    public int Index(){

        if(mediaPlayService.mediaPlayer.isPlaying()){
            CurrentTime=mediaPlayService.mediaPlayer.getCurrentPosition();

            CountTime=mediaPlayService.mediaPlayer.getDuration();
        }
        if(CurrentTime<CountTime){

            for(int i=0;i<lyricList.size();i++){
                if(i<lyricList.size()-1){
                    if(CurrentTime<lyricList.get(i).getLyricTime()&&i==0){
                        indexc=i;
                    }

                    if(CurrentTime>lyricList.get(i).getLyricTime()&&CurrentTime<lyricList.get(i+1).getLyricTime()){
                        indexc=i;
                    }
                }

                if(i==lyricList.size()-1&&CurrentTime>lyricList.get(i).getLyricTime()){
                    indexc=i;
                }
            }
        }

        //System.out.println(index);
        return indexc;
    }
}
