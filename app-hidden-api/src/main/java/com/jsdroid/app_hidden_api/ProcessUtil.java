package com.jsdroid.app_hidden_api;

import android.os.Process;

public class ProcessUtil {
    public static int getPpid() {
        return Process.myPpid();
    }

}
