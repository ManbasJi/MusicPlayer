package com.manbas.downmusic.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manbas.downmusic.MvpView.SongListView;
import com.manbas.downmusic.api.GetSongListApi;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.bean.SongMsgBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/29.
 */

public class SongListPresenter {
    private SongListView songListView;
    private GetSongListApi getSongListApi;
    private Context mContext;

    private ArrayList<SongMsgBean> mlist;



    public SongListPresenter(Context mContext,SongListView view){
        this.mContext=mContext;
        this.songListView=view;
        mlist=new ArrayList<>();
    }

    public void getSongListMsg(){
        getSongListApi=new GetSongListApi();
        getSongListApi.setType("2");
        getSongListApi.setSize("20");
        LogUtis.Log(getSongListApi.url()+"&"+getSongListApi.getParams());
        OkHttpUtils.get()
                .url(getSongListApi.url())
                .params(getSongListApi.getParams())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        songListView.toastMessage(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtis.Log(response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("song_list");
                            Gson gson=new Gson();
                            Type type=new TypeToken<ArrayList<SongMsgBean>>(){}.getType();
                            ArrayList<SongMsgBean> list=gson.fromJson(jsonArray.toString(),type);
                            if(list!=null&&list.size()>0){
                                mlist=list;
                                songListView.setSongList(mlist);
                            }
                            LogUtis.Log(jsonArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
}
