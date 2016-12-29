package com.manbas.downmusic.base;

import android.util.Log;

/**
 * Created by Administrator on 2016/12/19.
 */

public class LogUtis {
    public static final String TAG="MUSIC_PLAYER";

    public static void Log(String message){
        Log.i(TAG,message);
    }

    public static void Log(String tag,String message){
        Log.i(tag,message);
    }
}
