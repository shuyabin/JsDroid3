package com.jsdroid.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class JsdService extends Service {
    public JsdService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
