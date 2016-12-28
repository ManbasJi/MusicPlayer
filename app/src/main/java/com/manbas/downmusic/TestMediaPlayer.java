package com.manbas.downmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.manbas.downmusic.service.TestService;

/**
 * Created by Administrator on 2016/12/24.
 */

public class TestMediaPlayer extends Activity {
    MediaPlayer player;
    String url="http://abv.cn/music/光辉岁月.mp3";
    ImageView iv_play;
    TextView tv_new;
    SeekBar seekbar;

    int duration;
    int currentPosition=0;

    TestService testService;


    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(testService!=null){
                seekbar.setMax(testService.mediaPlayer.getDuration());
                seekbar.setProgress(testService.mediaPlayer.getCurrentPosition());

                handler.postDelayed(runnable,1000);
            }
        }
    };

    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            testService=((TestService.MyBindler)(service)).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_play);
        iv_play= (ImageView) findViewById(R.id.iv_play);
        tv_new= (TextView) findViewById(R.id.tv_new);
        seekbar= (SeekBar) findViewById(R.id.seekbar);


        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            testService.playOrPause();
                if(testService.mediaPlayer.isPlaying()){
                    iv_play.setImageResource(R.mipmap.pause);
                }else{
                    iv_play.setImageResource(R.mipmap.play);
                }
                handler.post(runnable);
            }
        });

        //绑定
        tv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("music","bind");
                Intent intent=new Intent(TestMediaPlayer.this,TestService.class);
                startService(intent);
                bindService(intent,serviceConnection,TestMediaPlayer.BIND_AUTO_CREATE);
            }
        });



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress=progress * testService.mediaPlayer.getDuration()/seekBar.getMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(progress);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onDestroy() {
        Log.i("music","onDestory");
        super.onDestroy();
        testService.stop();
        handler.removeCallbacks(runnable);
        unbindService(serviceConnection);
        Intent intent=new Intent(TestMediaPlayer.this,TestService.class);
        stopService(intent);
    }


}
