package com.manbas.downmusic.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
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
import com.manbas.downmusic.utlis.ProgressDialogUtil;
import com.manbas.downmusic.utlis.ToastUtils;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/29.
 */

public class PlayingActivity extends Activity implements PlayingView, MediaPlayer.OnBufferingUpdateListener {
    //广播Action
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_LAST = "ACTION_LAST";
    String musicStatus = "";

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
    @BindView(R.id.foregroundbg)
    PercentRelativeLayout foregroundbg;

    SingleSongInfoBean singleSongInfoBean;
    PlayingPresenter playingPresenter;
    public static MediaPlayService mediaPlayService;
    SimpleDateFormat format = new SimpleDateFormat("mm:ss");

    PlayingForCDFragment forCDFragment = new PlayingForCDFragment();

    //获取进来的歌曲id
    private static String lastUrl;
    private static String lastMusicId;
    private static String lastimgUrl;
    private static Integer position;

    //初始化界面控件
    String musicUrl = "";
    String songPic = "";
    int CurrentTime = 0;
    int CountTime = 0;
    int currentDurationP = 0;
    int totaldurationP = 0;

    //控制fragment的切换
    int tag = 0;
    boolean isShowCd = true;

    //获取歌词文件
    int index;
    List<SongMsgBean> songlist;
    List<LyricContent> lyricList = new ArrayList<>();


    //更新ui界面
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekbar.setProgress(seekbar.getProgress() + 1000);
            tv_currentTime.setText(duration2Time(seekbar.getProgress()));
            handler.removeCallbacks(runnable);//避免重复post，导致时间增加频率加快
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

        currentDurationP = getIntent().getIntExtra("currentDuration", 0);
        totaldurationP = getIntent().getIntExtra("Duration", 0);

        setTopState();//顶部状态栏透明
        ButterKnife.bind(this);

        if(!isServiceRunning(MediaPlayService.class)){
            onBindService();//绑定服务并开启
        }

        playingPresenter = new PlayingPresenter(this, this);

        songlist = (List<SongMsgBean>) getIntent().getSerializableExtra("songList");
        if (songlist == null) {
            songlist = Config.songMsgBeanList;
        } else {
            Config.songMsgBeanList = songlist;
        }

        if (getIntent().getStringExtra("index") != null) {
            index = Integer.parseInt(getIntent().getStringExtra("index"));
            Config.MUSIC_INDEX = index;
        } else {
            index = Config.MUSIC_INDEX;
        }
        position=index;

        if(songlist.get(position).getSong_id().equals(lastMusicId)){

            if(musicStatus.equals(ACTION_PLAY)){
                ivPlay.setImageResource(R.mipmap.pause);
            }else if(musicStatus.equals(ACTION_PAUSE)){
                ivPlay.setImageResource(R.mipmap.play);
            }
            initlastSeekBar();
            setFragment(forCDFragment,lastimgUrl);
            initView();
            onListener();

        }else{
            playingPresenter.getSingleSongMsg(songlist.get(position).getSong_id());
            lastMusicId=songlist.get(position).getSong_id();
        }



        playAndLoadLrc();//加载歌曲和歌词
        singleSongInfoBean = new SingleSongInfoBean();
    }

    private void onListener() {
        seekbar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        mediaPlayService.mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayService.mediaPlayer.setOnCompletionListener(new MeidaOnCompletion());
    }

    /*根据时长格式化称时间文本*/
    private String duration2Time(int duration) {
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        Log.i("music", "min:" + min + "| sec:" + sec);
        return (min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }

    private void initSeekBar(int totalduration) {
        seekbar.setMax(totalduration);
        tv_currentTime.setText(duration2Time(0));
        tv_durationTime.setText(duration2Time(totalduration));
        handler.post(runnable);
    }

    private void initlastSeekBar(){
        seekbar.setMax(mediaPlayService.mediaPlayer.getDuration());
        seekbar.setProgress(mediaPlayService.mediaPlayer.getCurrentPosition());
        tv_currentTime.setText(duration2Time(mediaPlayService.mediaPlayer.getCurrentPosition()));
        tv_durationTime.setText(duration2Time(mediaPlayService.mediaPlayer.getDuration()));
        handler.post(runnable);
    }

    /**
     * @param
     * @return
     * @desc 初始化界面控件
     */
    private void initView() {
//        tv_durationTime.setText(format.format(new Date(songlist.get(index).getFile_duration() * 1000)));
        tvSongname.setText(songlist.get(index).getTitle());
        tvArtistName.setText(songlist.get(index).getArtist_name());
    }

    /**
     * @param
     * @return
     * @desc 歌词加载，本地没有歌词文件时通过在线加载并缓存本地
     */
    private void playAndLoadLrc() {
        if (songlist != null) {
            File file = new File(Config.LRCPath + songlist.get(index).getSong_id() + ".lrc");
            LogUtis.Log("lrcfilePath" + Config.LRCPath + songlist.get(index).getSong_id() + ".lrc");
            if (!file.exists()) {
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                new LrcRead().ReadOnline(songlist.get(index).getLrclink(), songlist.get(index).getSong_id());
                                try {
                                    lyricList = new LrcRead().Read(Config.LRCPath + songlist.get(index).getSong_id() + ".lrc");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                ).start();

            } else {
                try {
                    lyricList = new LrcRead().Read(Config.LRCPath + songlist.get(index).getSong_id() + ".lrc");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtils.showShortToast(this, "获取歌曲失败!");
        }
    }

    /**
     * @param url 播放url
     * @return
     * @desc 播放音乐，这里需要判断Service是否绑定完成
     */
    private void initPlay(final String url) {
        if (mediaPlayService == null) {
            new Handler().postDelayed(new Runnable() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    try {
                        mediaPlayService.playUrl(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onListener();
                    initSeekBar(mediaPlayService.mediaPlayer.getDuration());


                    LogUtis.Log("durationTime:" + duration2Time(mediaPlayService.mediaPlayer.getDuration()));
                }
            }, 1000);
        } else {
            try {
                mediaPlayService.playUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            onListener();
            initSeekBar(mediaPlayService.mediaPlayer.getDuration());
            LogUtis.Log("durationTime:" + format.format(mediaPlayService.mediaPlayer.getDuration()));
        }
    }

    /**
     * @param
     * @return
     * @desc cd和歌词fragment切换
     */
    private void setFragment(Fragment f,String imgUrl) {
        Bundle b = new Bundle();
        b.putString("imageUrl", imgUrl);
        b.putSerializable("lyricList", (Serializable) lyricList);
        f.setArguments(b);
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_middleContent, f);
        transaction.commit();
    }

    /**
     * @param
     * @return
     * @desc 绑定服务
     */
    private void onBindService() {
        Intent intent = new Intent(this, MediaPlayService.class);
        startService(intent);
        bindService(intent, serviceConnection, MediaPlayService.BIND_AUTO_CREATE);
    }

    @OnClick({R.id.iv_back, R.id.iv_share, R.id.iv_previous, R.id.iv_play, R.id.iv_next, R.id.fl_middleContent})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_middleContent:
                if (tag == 0) {
                    setFragment(new PlayingForLrcFragment(),"");
                    tag = 1;
                } else {
                    setFragment(new PlayingForCDFragment(),songPic);
                    tag = 0;
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_share:
                break;
            case R.id.iv_previous:
                musicStatus = ACTION_LAST;
                isShowCd = false;
                if (index == 0) {
                    index = songlist.size() - 1;
                } else {
                    index -= 1;
                }
                seekbar.setProgress(0);
                playAndLoadLrc();
                mediaPlayService.mediaPlayer.pause();
                playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
                lastMusicId=songlist.get(index).getSong_id();
                ivPlay.setImageResource(R.mipmap.pause);
                break;
            case R.id.iv_play:
                if (mediaPlayService != null) {
                    mediaPlayService.playOrPause();
                    if (mediaPlayService.mediaPlayer.isPlaying()) {
                        handler.post(runnable);
                        musicStatus = ACTION_PLAY;
                        ivPlay.setImageResource(R.mipmap.pause);
                        //发送广播
                        LocalBroadcastManager.getInstance(PlayingActivity.this).sendBroadcast(new Intent(ACTION_PLAY));
                    } else {
                        handler.removeCallbacks(runnable);
                        musicStatus = ACTION_PAUSE;
                        ivPlay.setImageResource(R.mipmap.play);
                        //发送广播
                        LocalBroadcastManager.getInstance(PlayingActivity.this).sendBroadcast(new Intent(ACTION_PAUSE));
                    }
                }
                break;
            case R.id.iv_next:
                musicStatus = ACTION_NEXT;
                isShowCd = false;
                if (index == songlist.size() - 1) {
                    index = 0;
                } else {
                    index += 1;
                }
                seekbar.setProgress(0);
                playAndLoadLrc();
                mediaPlayService.mediaPlayer.pause();
                playingPresenter.getSingleSongMsg(songlist.get(index).getSong_id());
                lastMusicId=songlist.get(index).getSong_id();
                ivPlay.setImageResource(R.mipmap.pause);
                break;
        }
    }

    @Override
    public void onPlayUrl(SingleSongInfoBean bean) {
        singleSongInfoBean = bean;
        songPic = bean.getSonginfo().getPic_premium();
        musicUrl = bean.getBitrat().getFile_link();
        lastUrl=musicUrl;//储存播放时的音乐url
        lastimgUrl=songPic;
        initPlay(musicUrl);
        if (isShowCd) {
            setFragment(forCDFragment,songPic);
        } else {
            if (musicStatus == ACTION_PLAY) {

            } else if (musicStatus == ACTION_NEXT || musicStatus == ACTION_LAST) {
                Intent intent = new Intent(musicStatus);
                Bundle b = new Bundle();
                b.putString("songPic", songPic);
                b.putSerializable("lyricList", (Serializable) lyricList);
                intent.putExtras(b);
                LocalBroadcastManager.getInstance(PlayingActivity.this).sendBroadcast(intent);
            }
        }
        initView();
    }

    @Override
    public void toastMsg(String msg) {

    }


    @Override
    public void openProgressDialog(String msg) {
        ProgressDialogUtil.openProgressDialog(this, "", msg);
    }

    @Override
    public void closeProgressDialog(String msg) {
        ProgressDialogUtil.closeProgressDialog();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekbar.setSecondaryProgress(percent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(serviceConnection);
    }

    /**
     * @desc 通过获取音乐的播放进度来控制歌词list的index
     * @param
     * @return indexc
     */
    private int indexc = 0;

    public int Index() {
        if (mediaPlayService.mediaPlayer.isPlaying()) {
            CurrentTime = mediaPlayService.mediaPlayer.getCurrentPosition();
            CountTime = mediaPlayService.mediaPlayer.getDuration();
        }
        if (CurrentTime < CountTime) {
            for (int i = 0; i < lyricList.size(); i++) {
                if (i < lyricList.size() - 1) {
                    if (CurrentTime < lyricList.get(i).getLyricTime() && i == 0) {
                        indexc = i;
                    }
                    if (CurrentTime > lyricList.get(i).getLyricTime() && CurrentTime < lyricList.get(i + 1).getLyricTime()) {
                        indexc = i;
                    }
                }
                if (i == lyricList.size() - 1 && CurrentTime > lyricList.get(i).getLyricTime()) {
                    indexc = i;
                }
            }
        }
        return indexc;
    }

    /**
     * @param
     * @return
     * @desc 设置状态栏透明
     */
    private void setTopState() {
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

    /**
     * @desc 判断Service是否开启
     * @param
     * @return
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            LogUtis.Log("seekbarprogress:" + progress + "|" + mediaPlayService.mediaPlayer.getDuration());
//            this.progress = progress * mediaPlayService.mediaPlayer.getDuration()
//                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayService.mediaPlayer.seekTo(seekBar.getProgress());

        }

    }

    class MeidaOnCompletion implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            LogUtis.Log("播放完成");
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
}
