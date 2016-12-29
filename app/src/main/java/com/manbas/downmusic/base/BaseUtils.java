package com.manbas.downmusic.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2016/12/29.
 */

public class BaseUtils {
    public static void ToAc(Context packageContext,Class<?> cls){
        Intent intent=new Intent(packageContext,cls);
        packageContext.startActivity(intent);
    }

    public static void ToAcb(Context packageContext, Class<?> cls, Bundle bundle){
        Intent intent=new Intent(packageContext,cls);
        intent.putExtras(bundle);
        packageContext.startActivity(intent);
    }
}
