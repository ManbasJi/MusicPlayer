package com.manbas.downmusic.utlis;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.manbas.downmusic.R;

/**
 * 显示Toast的工具类
 *
 * @author wuzx
 */
public class ToastUtils {
    private static Toast mToast;
    private static View view;

    public static void showToast(Context context, String str) {
        if (context != null) {
            view = View.inflate(context, R.layout.toast, null);
            TextView txt = ((TextView) view.findViewById(R.id.txt_toast));
            if (mToast == null) {
                mToast = new Toast(context);
                txt.setText(str);
                mToast.setView(view);
                mToast.setDuration(Toast.LENGTH_LONG);
            } else {
                txt.setText(str);
                mToast.setView(view);
            }
            mToast.show();
        }

    }

    public static void showToast(Context context, int stringId) {
        if (context != null) {
            view = View.inflate(context, R.layout.toast, null);
            TextView txt = ((TextView) view.findViewById(R.id.txt_toast));
            if (mToast == null) {
                mToast = new Toast(context);
                txt.setText(context.getString(stringId));
                mToast.setView(view);
                mToast.setDuration(Toast.LENGTH_LONG);
            } else {
                txt.setText(context.getString(stringId));
                mToast.setView(view);
            }
            mToast.show();
        }
    }

    public static void showShortToast(Context context, String str) {
        if (context != null) {
            view = View.inflate(context, R.layout.toast, null);
            TextView txt = ((TextView) view.findViewById(R.id.txt_toast));
            if (mToast == null) {
                mToast = new Toast(context);
                txt.setText(str);
                mToast.setView(view);
                mToast.setDuration(Toast.LENGTH_SHORT);
            } else {
                txt.setText(str);
                mToast.setView(view);
            }
            mToast.show();
        }
    }

    public static void showShortToast(Context context, int stringId) {
        if (context != null) {
            view = View.inflate(context, R.layout.toast, null);
            TextView txt = ((TextView) view.findViewById(R.id.txt_toast));
            if (mToast == null) {
                mToast = new Toast(context);
                txt.setText(context.getString(stringId));
                mToast.setView(view);
                mToast.setDuration(Toast.LENGTH_SHORT);
            } else {
                txt.setText(context.getString(stringId));
                mToast.setView(view);
            }
            mToast.show();
        }
    }



}
