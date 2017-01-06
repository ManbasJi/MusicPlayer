package com.manbas.downmusic.fragment;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
    MusicReciver musicReciver=new MusicReciver();

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
                case 3:
                    imgUrl=msg.getData().getString("songPic");
                    if(imgUrl!=null &&imgUrl!=""){
                        setImage(imgUrl);
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.playingforcd_fragment,container,false);

        Bundle b=getArguments();

        ButterKnife.bind(this,view);
        initBroadCast();
        imgUrl=b.getString("imageUrl");
        Glide.with(getActivity()).load(imgUrl).transform(new CircleTransform(getActivity())).into(ivSongImage);

        setAnimator();
        startRotation();
        return view;

    }

    private void setImage(final String imgUrl){
        LogUtis.Log("songPic"+imgUrl);
        Glide.with(getActivity()).load(imgUrl).transform(new CircleTransform(getActivity())).placeholder(R.mipmap.ic_launcher).into(ivSongImage);
        startRotation();
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtis.Log("onAttach","context");
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtis.Log("onAttach","activity");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        mActivity= (PlayingActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initBroadCast(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(PlayingActivity.ACTION_PLAY);
        intentFilter.addAction(PlayingActivity.ACTION_PAUSE);
        intentFilter.addAction(PlayingActivity.ACTION_NEXT);
        intentFilter.addAction(PlayingActivity.ACTION_LAST);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(musicReciver,intentFilter);
    }

    public void setImageUrl(){
        Glide.with(this).load(imgUrl).transform(new CircleTransform(getActivity())).into(ivSongImage);
        startRotation();
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


    class MusicReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            switch (action){
                case PlayingActivity.ACTION_PLAY:
                    startRotation();
                    break;
                case PlayingActivity.ACTION_PAUSE:
                    stopRotation();
                    break;
                case PlayingActivity.ACTION_NEXT:
                    imgUrl=intent.getStringExtra("songPic");
                    setImageUrl();
                    break;
                case PlayingActivity.ACTION_LAST:
                    imgUrl=intent.getStringExtra("songPic");
                    setImageUrl();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(musicReciver);
    }
}
