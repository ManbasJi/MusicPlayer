package com.manbas.downmusic.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.manbas.downmusic.R;
import com.manbas.downmusic.bean.LyricContent;
import com.manbas.downmusic.utlis.LrcRead;
import com.manbas.downmusic.view.LyricView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LyricActivity extends Activity {
	/** Called when the activity is first created. */

	private Button play;

	private MediaPlayer mMediaPlayer;

	private LrcRead mLrcRead;

	private LyricView mLyricView;

	private int index=0;

	private int CurrentTime=0;

	private int CountTime=0;

	private List<LyricContent> LyricList=new ArrayList<LyricContent>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//初始化
		init();

		try {
			String sdState=Environment.getExternalStorageState();
			if(sdState.equals(Environment.MEDIA_MOUNTED)){
				String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
//				Toast.makeText(this, sdPath+"/manbasji/geci.lrc", Toast.LENGTH_LONG).show();

				File flie=new File(sdPath+"/manbasji/");
				if(!flie.exists()){
					Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
				}else{
					mLrcRead.Read(sdPath+"/manbasji/geci.lrc");
				}
			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LyricList=mLrcRead.GetLyricContent();

		//设置歌词资源
		mLyricView.setSentenceEntities( LyricList);


		mHandler.post(mRunnable);

		for (int i = 0; i < mLrcRead.GetLyricContent().size(); i++) {
			System.out.println(mLrcRead.GetLyricContent().get(i).getLyricTime()+"-");
			System.out.println(mLrcRead.GetLyricContent().get(i).getLyric()+"----");
		}


		play.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				Play();

			}
		});
	}

	//播放音乐
	public void Play(){

		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource("sdcard/music/张靓颖 - 无法言喻.mp3");
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setLooping(true);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	//初始化
	public void init(){
		mMediaPlayer=new MediaPlayer();

		mLrcRead=new LrcRead();

		play=(Button)findViewById(R.id.play);

		mLyricView=(LyricView)findViewById(R.id.LyricShow);

	}


	Handler mHandler=new Handler();

	Runnable mRunnable= new Runnable() {
		public void run() {

			mLyricView.SetIndex(Index());

			mLyricView.invalidate();

			mHandler.postDelayed(mRunnable, 100);
		}
	};

	public int Index(){
		if(mMediaPlayer.isPlaying()){
			CurrentTime=mMediaPlayer.getCurrentPosition();

			CountTime=mMediaPlayer.getDuration();
		}
		if(CurrentTime<CountTime){

			for(int i=0;i<LyricList.size();i++){
				if(i<LyricList.size()-1){
					if(CurrentTime<LyricList.get(i).getLyricTime()&&i==0){
						index=i;
					}

					if(CurrentTime>LyricList.get(i).getLyricTime()&&CurrentTime<LyricList.get(i+1).getLyricTime()){
						index=i;
					}
				}

				if(i==LyricList.size()-1&&CurrentTime>LyricList.get(i).getLyricTime()){
					index=i;
				}
			}
		}

		//System.out.println(index);
		return index;
	}

}