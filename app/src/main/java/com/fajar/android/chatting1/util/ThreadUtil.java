package com.fajar.android.chatting1.util;

public class ThreadUtil {
    public static Thread runAndStart(Runnable runnable) {

        Thread thread  = new Thread(runnable);

        thread.start();
        return thread;
    }
}
