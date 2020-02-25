package com.jsdroid.runner;

import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import com.jsdroid.api.IJsDroidServer;
import com.jsdroid.api.JsDroidEnv;
import com.jsdroid.app_hidden_api.ProcessUtil;
import com.jsdroid.ipc.Ipc;
import com.jsdroid.runner.utils.LibUtil;

import java.io.IOException;

import dalvik.system.DexClassLoader;

/**
 * shell进程入口
 */
public class ShellMain {
    public static String TAG = "JsDroid";

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.d(TAG, "un know error.", e);
                SystemClock.sleep(1000);
                System.exit(0);
            }
        });
        server(args);
    }

    /**
     * 服务启动，运行稳定，发送signal信号，通知守护进程(父进程)继续循环
     */
    private static void notifyDaemonLoop() {
        Process.sendSignal(ProcessUtil.getPpid(), 10);
    }


    private static void server(String[] args) {
        Ipc.startServer(Config.JSDROID_SERVER_PORT, new Ipc.ServerListener() {
            @Override
            public void onStart(Ipc.Server server) {
                //服务启动成功，通知守护进程进行循环
                notifyDaemonLoop();
                try {
                    LibUtil.extractLibFile(JsDroidEnv.sdkFile, JsDroidEnv.libDir);
                    LibUtil.extractLibFile(JsDroidEnv.shellApkFile, JsDroidEnv.libDir);
                    DexClassLoader shellClassLoader = getDexClassLoader(JsDroidEnv.sdkFile + ":" + JsDroidEnv.shellApkFile);
                    Class<?> aClass = shellClassLoader.loadClass(
                            JsDroidEnv.serverClass);
                    IJsDroidServer jsDroidServer = (IJsDroidServer) aClass.newInstance();
                    //3.调用com.jsdroid.server.JsDroidServer.onServerStart方法
                    jsDroidServer.onServerStart(server);
                } catch (Throwable e) {
                    Log.e(TAG, "start server error: ", e);
                    System.exit(1);
                }
            }

            @Override
            public void onClose(Ipc.Server server) {
                System.exit(0);
            }

            @Override
            public void onOpenServerErr() {
                System.exit(1);
            }
        });

    }

    private static DexClassLoader getDexClassLoader(String file) throws IOException {
        return getDexClassLoader(null, file);
    }

    private static DexClassLoader getDexClassLoader(ClassLoader parent, String file) throws IOException {
        if (parent == null) {
            parent = JsDroidApplication.class.getClassLoader();
        }
        //1.将apk文件里面的so解压出来

        //2.加载com.jsdroid.server.JsDroidServer
        return new DexClassLoader(
                file,
                JsDroidEnv.optDir,
                JsDroidEnv.libDir,
                parent);
    }
}
