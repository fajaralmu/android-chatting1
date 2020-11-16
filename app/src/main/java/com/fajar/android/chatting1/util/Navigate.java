package com.fajar.android.chatting1.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.Serializable;

public class Navigate {

    public static void navigate(Context c, Class className) {
        Intent i = new Intent(c, className);
        c.startActivity(i);
    } public static void navigate(Context c, Class className, Object extrasKey, Serializable extras) {
        Intent i = new Intent(c, className);
        i.putExtra(extrasKey.toString()
                , extras);

        c.startActivity(i);

    }

    public static void openLink(String link, Context c){
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            c.startActivity(browserIntent);
        }catch (Exception e){

        }
    }

    public static void shareText(Context c, String content){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
       c. startActivity(shareIntent);

    }

//    public static void cekLogin(Context c) {
//        SharedPreferences sharedpreferences = c.getSharedPreferences(Constant.PREF_AKUN, c.MODE_PRIVATE);
//        String session_guru = sharedpreferences.getString("session", null);
//        if (session_guru.equals(null) || session_guru.equals("")) {
//            Navigate.navigate(c, LoginActivity.class);
//        }
//    }
}
