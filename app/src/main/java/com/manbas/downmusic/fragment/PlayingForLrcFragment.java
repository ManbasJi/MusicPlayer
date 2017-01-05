package com.manbas.downmusic.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manbas.downmusic.R;
import com.manbas.downmusic.activity.PlayingActivity;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.LyricContent;
import com.manbas.downmusic.view.LyricView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/31.
 */

public class PlayingForLrcFragment extends Fragment {

    View view;
    LyricView lyricView;
    List<LyricContent> lyricList=new ArrayList<>();
    private MusicReciver musicReciver=new MusicReciver();

    Handler mHandler=new Handler();

    Runnable mRunnable= new Runnable() {
        public void run() {

            lyricView.SetIndex(((PlayingActivity)getActivity()).Index());

            lyricView.invalidate();

            mHandler.postDelayed(mRunnable, 100);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.playingforlrc_fragment,null);
        initBroadCast();
        lyricView= (LyricView) view.findViewById(R.id.lv_lrc);
        Bundle b=getArguments();
        lyricList= (List<LyricContent>) b.getSerializable("lyricList");
        LogUtis.Log("lyricList:"+lyricList);
        setLrc();
        return view;
    }

    private void setLrc(){
        lyricView.setSentenceEntities(lyricList);
        mHandler.post(mRunnable);
    }

    private void initBroadCast(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(PlayingActivity.ACTION_PLAY);
        intentFilter.addAction(PlayingActivity.ACTION_PAUSE);
        intentFilter.addAction(PlayingActivity.ACTION_NEXT);
        intentFilter.addAction(PlayingActivity.ACTION_LAST);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(musicReciver,intentFilter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            mHandler.removeCallbacks(mRunnable);
        }
        LogUtis.Log("fragment hidden");
    }

    class MusicReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case PlayingActivity.ACTION_PLAY:

                    break;
                case PlayingActivity.ACTION_PAUSE:
                    break;
                case PlayingActivity.ACTION_NEXT:
                    lyricList= (List<LyricContent>) intent.getSerializableExtra("lyricList");
                    setLrc();
                    break;
                case PlayingActivity.ACTION_LAST:
                    lyricList= (List<LyricContent>) intent.getSerializableExtra("lyricList");
                    setLrc();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        LogUtis.Log("fragment onDestroy()");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(musicReciver);
    }
}
