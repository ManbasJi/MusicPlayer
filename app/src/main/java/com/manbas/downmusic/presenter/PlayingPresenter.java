package com.manbas.downmusic.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manbas.downmusic.MvpView.PlayingView;
import com.manbas.downmusic.api.SingleSongApi;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.SingleSongInfoBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/30.
 */

public class PlayingPresenter {
    private Context mContext;
    private PlayingView view;
    private SingleSongApi singleSongApi;

    public PlayingPresenter(Context mContext,PlayingView view) {
        this.mContext=mContext;
        this.view=view;
        singleSongApi=new SingleSongApi();
    }

    public void getSingleSongMsg(String songId){
        singleSongApi.setSongId(songId);
        LogUtis.Log(singleSongApi.url()+"&"+singleSongApi.getParams());
        OkHttpUtils.get()
                .url(singleSongApi.url())
                .params(singleSongApi.getParams())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        view.toastMsg(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson=new Gson();
                        Type type=new TypeToken<SingleSongInfoBean>(){}.getType();
                        SingleSongInfoBean bean=gson.fromJson(response,type);
                        if(bean!=null){
                            view.onPlayUrl(bean);
                        }
                    }
                });
    }
}
