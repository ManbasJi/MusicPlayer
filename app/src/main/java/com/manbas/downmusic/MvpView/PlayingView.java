package com.manbas.downmusic.MvpView;

import com.manbas.downmusic.bean.SingleSongInfoBean;

/**
 * Created by Administrator on 2016/12/30.
 */

public interface PlayingView {
    void onPlayUrl(SingleSongInfoBean bean);
    void toastMsg(String msg);
}
