package com.manbas.downmusic.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            mHandler.removeCallbacks(mRunnable);

        }
        LogUtis.Log("fragment hidden");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        LogUtis.Log("fragment onDestroy()");
    }
}
