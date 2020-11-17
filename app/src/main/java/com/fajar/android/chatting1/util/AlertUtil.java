package com.fajar.android.chatting1.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class AlertUtil {
    public static void YesAlert(Context ctx, String content) {
        YesAlert(ctx, "Info", content, null);
    }

    public static void YesAlert(Context ctx, String title, String content) {
        YesAlert(ctx, title, content, null);
    }

    public static void YesAlert(Context ctx, String title, String content, DialogInterface.OnClickListener yesListener) {
        AlertDialog.Builder a_builder = new AlertDialog.Builder(ctx);
        a_builder.setMessage(content)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        yesListener != null ? yesListener : new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
        ;
        AlertDialog alert = a_builder.create();
        alert.setTitle(title);
        alert.show();
    }

    public static void confirm(Context ctx, String message, DialogInterface.OnClickListener yesListener) {
        new AlertDialog.Builder(ctx)
                .setTitle("Confirmation")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, yesListener)
                .setNegativeButton(android.R.string.no, null).show();
    }

}
