package com.jsdroid.runner;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.jsdroid.app_hidden_api.AdbPortUtil;
import com.jsdroid.ipc.Ipc;
import com.jsdroid.runner.utils.PortUtil;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * 运行JsDroid守护进程的线程类
 */
public class JsDroidDaemonThread extends Thread {
    public static String TAG = "JsDroidDaemon";
    JsDroidApplication application;
    ShellCommandListener shellCommandListener;
    String command;

    public JsDroidDaemonThread(JsDroidApplication application) {
        this.application = application;
        JsDroidAppImpl.getInstance().setApplication(application);
        JsDroidConnector.getInstance().connect();
    }

    public void setShellCommandListener(ShellCommandListener shellCommandListener) {
        this.shellCommandListener = shellCommandListener;
    }

    public interface ShellCommandListener {
        void onPreStartJsDroidServer();

        void onWaitAdbPort();

        void onWaitJsDroidServer();

        void onJsDroidServerStart();

        void onJsDroidServerStop();
    }


    private void init() {
        JsDroidAppEnv.getInstance().init(application);
        command = JsDroidAppEnv.getInstance().getCommand();
    }


    public void run() {
        init();
        while (true) {
            //如果是debug模式或者没启动服务，则启动服务
            if (isDebugMode() || !PortUtil.checkPort(Config.JSDROID_SERVER_PORT)) {
                startServer();
            }
            serverLoop();
        }
    }

    private boolean isDebugMode() {
        return BuildConfig.DEBUG;
    }

    private void startServer() {
        //检测旧版端口，如果已经启动，则无需启动
        if (shellCommandListener != null) {
            shellCommandListener.onPreStartJsDroidServer();
        }
        if (SimpleSu.hasSu()) {
            SimpleSu.su(command);
        } else {
            if (shellCommandListener != null) {
                shellCommandListener.onWaitAdbPort();
            }
            int adbPort = AdbPortUtil.getAdbPort();
            while (!PortUtil.checkPort(adbPort)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
            SimpleAdb simpleAdb = new SimpleAdb();
            try {
                simpleAdb.exec(adbPort, command);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (shellCommandListener != null) {
            shellCommandListener.onWaitJsDroidServer();
        }
    }

    private void serverLoop() {
        boolean notifyServerRunning = true;
        boolean notifyServerDisconnect = false;
        //等待服务启动
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
            if (PortUtil.checkPort(Config.JSDROID_SERVER_PORT)) {
                //10秒连接成功，不需要重新执行启动命令，继续监听
                i = 0;
                //连接成功，需要监听连接断开事件
                if (notifyServerRunning) {
                    notifyServerDisconnect = true;
                    notifyServerRunning = false;
                    if (shellCommandListener != null) {
                        shellCommandListener.onJsDroidServerStart();
                    }
                }
            } else {
                if (notifyServerDisconnect) {
                    notifyServerDisconnect = false;
                    notifyServerRunning = false;
                    if (shellCommandListener != null) {
                        shellCommandListener.onJsDroidServerStop();
                    }
                }
            }
        }

    }
}
