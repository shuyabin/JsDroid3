package com.jsdroid.app_hidden_api;

import android.os.SystemProperties;
import android.text.TextUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AdbPortUtil {
    public static int getAdbPort() {
        // XXX: SystemProperties.get is @hide method
        String port = SystemProperties.get("service.adb.tcp.port", "");
        if (!TextUtils.isEmpty(port) && TextUtils.isDigitsOnly(port)) {
            int p = Integer.parseInt(port);
            if (p > 0 && p <= 0xffff) {
                return p;
            }
        }
        return 5555;
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> submit = executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("127.0.0.1", 5037);
                    while (true) {
                        socket.getInputStream().read();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        submit.cancel(true);
    }
}
