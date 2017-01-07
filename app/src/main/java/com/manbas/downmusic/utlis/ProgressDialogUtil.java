package com.manbas.downmusic.utlis;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.manbas.downmusic.R;
import com.manbas.downmusic.base.LogUtis;
import com.manbas.downmusic.view.CustomProgressDialog;

/**
 * Created by jyb on 2017/1/7.
 */
public class ProgressDialogUtil {
    private static CustomProgressDialog progressDialog;
    //private static Dialog loadingDialog;
    /**
     * 显示进度框
     *
     * @param context
     * @param title
     *            标题
     * @param msg
     *            显示内容
     */
    public static void openProgressDialog(Context context, String title,
                                          String msg) {
//        LogUtis.e("openProgressDialog " + context.getClass().getName());
        progressDialog = new CustomProgressDialog(context, R.style.dialogback);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public static void openProgressDialog(Context context, String title,
                                          String msg, boolean cancelable) {
//        LogUtil.e("openProgressDialog cancelable"
//                + context.getClass().getName());
        progressDialog = (CustomProgressDialog) ProgressDialog.show(context,
                title, msg, true, cancelable);

    }

    /**
     * 显示进度框
     *
     * @param context
     * @param titleId
     *            标题
     * @param msgId
     *            显示内容
     */
    public static void openProgressDialog(Context context, int titleId,
                                          int msgId) {
        progressDialog = new CustomProgressDialog(context);
        progressDialog.setTitle(context.getString(titleId));
        progressDialog.setMessage(context.getString(msgId));
         Window wd= progressDialog.getWindow();
         WindowManager.LayoutParams lp = wd.getAttributes();
         lp.alpha = 0.5f;
        lp.x=100;
        lp.y=700;
         wd.setAttributes(lp);

        if (null != progressDialog && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public static void setProgressDialogMsg(String msg) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.setMessage(msg);
            }
        }
    }

    /**
     * 关闭进度框
     */
    public static void closeProgressDialog(String action) {
        LogUtis.Log("closeProgressDialog Action:" + action);
        if (progressDialog != null) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }

    /**
     * 关闭进度框
     */
    public static void closeProgressDialog() {
        LogUtis.Log("closeProgressDialog");
        if (progressDialog != null) {

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }
//	public static void closeLoadingDialog() {
//		LogUtil.Log("loadingDialog");
//		if (loadingDialog != null) {
//
//			if (loadingDialog.isShowing()) {
//				loadingDialog.dismiss();
//			}
//
//		}
//	}

}

