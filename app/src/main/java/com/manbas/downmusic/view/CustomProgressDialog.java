package com.manbas.downmusic.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.manbas.downmusic.R;

/**
 * Created by jyb on 2017/1/7.
 */

public class CustomProgressDialog extends ProgressDialog {
    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customprogressdialog);

        // setContentView(android.R.layout.alert_dialog_progress);

    }

    public static CustomProgressDialog show(Context context) {
        CustomProgressDialog dialog = new CustomProgressDialog(context,
                R.style.dialogback);
        dialog.show();
        return dialog;
    }

}