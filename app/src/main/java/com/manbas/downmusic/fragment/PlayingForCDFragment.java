package com.manbas.downmusic.fragment;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.manbas.downmusic.R;
import com.manbas.downmusic.activity.PlayingActivity;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.view.CircleTransform;

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

    PlayingActivity mActivity;

    public ObjectAnimator objectAnimator1,objectAnimator2;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    startRotation();
                    LogUtis.Log("startRotation");
                    break;
                case 2:
                    stopRotation();
                    LogUtis.Log("stopRotation");
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.playingforcd_fragment,container,false);
        Bundle b=getArguments();
        imgUrl=b.getString("imageUrl");

        ButterKnife.bind(this,view);
        if(imgUrl!=null){
            Glide.with(this).load(imgUrl).transform(new CircleTransform(getActivity())).into(ivSongImage);
        }
        setAnimator();
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=(PlayingActivity)context;
        mActivity.setmHandler(handler);
    }

    @Override
    public void onResume() {
        super.onResume();
//        startRotation();\
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
