package com.manbas.downmusic.MvpView;

import com.manbas.downmusic.bean.SongMsgBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/29.
 */

public interface SongListView {
    void setSongList(ArrayList<SongMsgBean> list);
    void toastMessage(String msg);
}
