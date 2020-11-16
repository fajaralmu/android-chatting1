package com.fajar.android.chatting1.util;

import android.util.Log;

public class Logs {

    static final String LOG_INFO = "app_info";

    public static void log(Object ... obj){
        StringBuilder sb = new StringBuilder();

        for (Object o :
                obj) {
            if(o!=null){
                sb = sb.append(" ").append(o.toString());
            }
        }

        Log.i(LOG_INFO, sb.toString());
    }
}
