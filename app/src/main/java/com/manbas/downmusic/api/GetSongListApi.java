package com.manbas.downmusic.api;

import com.manbas.downmusic.base.BaseApi;
import com.manbas.downmusic.config.Config;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/12/19.
 */

public class GetSongListApi implements BaseApi{
    String type;
    String size;
    @Override
    public String url() {
        return Config.GetMusicListApi;
    }

    @Override
    public HashMap<String, String> getParams() {
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("type",type);
        hashMap.put("size",size);
        return hashMap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
