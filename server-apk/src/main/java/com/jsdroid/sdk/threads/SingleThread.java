package com.jsdroid.sdk.threads;

import android.os.Handler;
import android.os.HandlerThread;

public class SingleThread {
    static HandlerThread handlerThread = new HandlerThread("single thread");

    static {
        handlerThread.start();
    }

    public static void execute(Runnable runnable) {
        new Handler(handlerThread.getLooper()).post(runnable);
    }
}
