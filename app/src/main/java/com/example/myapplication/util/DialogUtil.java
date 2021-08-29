package com.example.myapplication.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class DialogUtil {

    public static void showAlertDialog(final Activity context, String title, String message,
                                       Boolean status, final boolean isActivityFinish) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
//        if(status != null)
//            // Setting alert dialog icon
//            alertDialog.setIcon((status) ? R.mipmap.ic_launcher : R.mipmap.ic_launcher);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (isActivityFinish) {
                    context.finish();
                }
            }
        });
        alertDialog.show();
    }
}
