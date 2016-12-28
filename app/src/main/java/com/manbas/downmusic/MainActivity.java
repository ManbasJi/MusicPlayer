package com.manbas.downmusic;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.manbas.downmusic.service.Player;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnBufferingUpdateListener{
    private static final int PROCESSING=1;
    private static final int FAILURE=-1;

    TextView tv_musicName;
    ImageView iv_playorpause;
    SeekBar seekBar;
    private Player player;
    private String musicPath="http://abv.cn/music/光辉岁月.mp3";
    private Timer mTimer = new Timer(); // 计时器






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filename = musicPath.substring(musicPath.lastIndexOf('/') + 1);
        findViewId();
        tv_musicName.setText(filename);
        bindServiceConnection();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = player.mediaPlayer.getCurrentPosition();
            int duration = player.mediaPlayer.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
            }
        }
    };

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (player.mediaPlayer == null)
                return;
            if (player.mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }
    };

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            player=((Player.MyPlayer) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            player=null;
        }
    };

    //  在Activity中调用 bindService 保持与 Service 的通信
    private void bindServiceConnection() {
        Intent intent = new Intent(MainActivity.this, Player.class);
        startService(intent);
        bindService(intent, connection, this.BIND_AUTO_CREATE);
    }

    private void findViewId(){
        tv_musicName= (TextView) findViewById(R.id.tv_musicName);
        iv_playorpause= (ImageView) findViewById(R.id.iv_play);
        seekBar= (SeekBar) findViewById(R.id.seekbar);

        iv_playorpause.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.iv_play){
            player.playUrl(musicPath);
            mTimer.schedule(timerTask,0,1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBar.setSecondaryProgress(percent);
        int currentProgress = seekBar.getMax()
                * player.mediaPlayer.getCurrentPosition() / player.mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", percent + " buffer");
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener{
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress=(progress/seekBar.getMax())*player.mediaPlayer.getDuration();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.mediaPlayer.seekTo(progress);
        }
    }
}
