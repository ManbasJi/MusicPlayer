package com.manbas.downmusic.api;

import com.manbas.downmusic.base.BaseApi;
import com.manbas.downmusic.config.Config;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/30.
 */

public class SingleSongApi implements BaseApi {
    private String songId;
    @Override
    public String url() {
        return Config.PlayApi;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("songid",songId);
        return hashMap;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }
}
