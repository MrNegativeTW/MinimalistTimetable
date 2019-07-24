package com.txwstudio.app.timetable;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class CustomDialog extends AlertDialog implements DialogInterface.OnClickListener {

    Activity mActivity;
    public CustomDialog(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
