package com.manbas.downmusic.fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.manbas.downmusic.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/31.
 */

public class PlayingForCDFragment extends Fragment {
    private View view;
    private String imgUrl;

    @BindView(R.id.iv_songImage)
    ImageView ivSongImage;
    @BindView(R.id.iv_cd)
    ImageView ivCd;

    ObjectAnimator objectAnimator1,objectAnimator2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.playingforcd_fragment,container,false);
        Bundle b=getArguments().getBundle("imgUrl");
        imgUrl=b.getString("imageUrl");
        ButterKnife.bind(this,view);
        setAnimator();
        return view;

    }

    private void setAnimator() {
        objectAnimator2 = ObjectAnimator.ofFloat(ivCd, "rotation", 0f, 360.0f);
        objectAnimator2.setDuration(10000);
        objectAnimator2.setRepeatCount(-1);
        objectAnimator2.setInterpolator(new LinearInterpolator());

        objectAnimator1 = ObjectAnimator.ofFloat(ivSongImage, "rotation", 0f, 360.0f);
        objectAnimator1.setDuration(10000);
        objectAnimator1.setRepeatCount(-1);
        objectAnimator1.setInterpolator(new LinearInterpolator());
    }

    public void startRotation(){
        objectAnimator1.start();
        objectAnimator2.start();
    }

    public void stopRotation(){
        objectAnimator1.pause();
        objectAnimator2.pause();
    }

}
