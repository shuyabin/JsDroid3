package com.jsdroid.runner;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;

import com.jsdroid.api.IJsDroidShell;
import com.jsdroid.api.JsDroidEnv;
import com.jsdroid.api.annotations.Doc;
import com.jsdroid.ipc.data.IpcService;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class JsDroidApplication<T> extends Application implements JsDroidDaemonThread.ShellCommandListener,
        JsDroidListener.ConnectListener,
        JsDroidListener.OnPrintListener,
        JsDroidListener.OnScriptLoadListener,
        JsDroidListener.OnVolumeDownListener, JsDroidListener.OnScriptRunningListener, JsDroidListener.OnStopListener {


    public static class ProcessErrException extends Exception {
        public ProcessErrException() {
            super();
        }

        public ProcessErrException(String message) {
            super(message);
        }

        public ProcessErrException(String message, Throwable cause) {
            super(message, cause);
        }

        public ProcessErrException(Throwable cause) {
            super(cause);
        }

    }

    private static JsDroidApplication instance;

    public static <T extends JsDroidApplication> T getInstance() {
        return (T) instance;
    }

    public static <T extends JsDroidApplication> T getInstance(Class<T> clazz) {
        return (T) instance;
    }

    private JsDroidDaemonThread jsDroidDaemonThread;
    private Handler singleHandler;
    private HandlerThread singleHandlerThread;
    private String scriptFile;
    private boolean connected;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        singleHandlerThread = new HandlerThread("singleHandlerThread");
        singleHandlerThread.start();
        singleHandler = new Handler(singleHandlerThread.getLooper());
        try {
            checkMainProcess(this);
            jsDroidDaemonThread = new JsDroidDaemonThread(this);
            jsDroidDaemonThread.setShellCommandListener(this);
            jsDroidDaemonThread.start();
            JsDroidListener.getInstance().setConnectListener(this);
            JsDroidListener.getInstance().setOnPrintListener(this);
            JsDroidListener.getInstance().setOnScriptLoadListener(this);
            JsDroidListener.getInstance().setOnVolumeDownListener(this);
            JsDroidListener.getInstance().setOnScriptStartListener(this);
            JsDroidListener.getInstance().setOnStopListener(this);
        } catch (ProcessErrException e) {
        }

    }


    public String getLogFile() {
        return getFilesDir() + "/log.txt";
    }

    public void toast(String text) {
        JsDroidAppImpl.getInstance().toast(text);
    }

    public JsDroidDaemonThread getJsDroidDaemonThread() {
        return jsDroidDaemonThread;
    }

    private void checkMainProcess(Context context) throws ProcessErrException {
        String curProcessName = getCurProcessName(context);
        if (curProcessName == null) {
            throw new ProcessErrException();
        }
        //进程名不为包名，则抛出异常
        if (!curProcessName.equals(context.getPackageName())) {
            throw new ProcessErrException();
        }
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public String readConfig(String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), 0);
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveConfig(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    @Doc("预启动服务时触发")
    @Override
    public void onPreStartJsDroidServer() {

    }

    @Doc("等待wifi调试端口时触发")
    @Override
    public void onWaitAdbPort() {

    }

    @Doc("等待JsDroid服务连接时触发")
    @Override
    public void onWaitJsDroidServer() {

    }

    @Doc("JsDroid服务启动时触发")
    @Override
    public void onJsDroidServerStart() {

    }

    @Doc("JsDroid服务停止时触发")
    @Override
    public void onJsDroidServerStop() {

    }

    @Doc("JsDroid服务连接成功时触发")
    @Override
    public void onJsDroidConnected() {
        connected = true;
    }

    @Doc("JsDroid服务连接断开时触发")
    @Override
    public void onJsDroidDisconnected() {
        connected = false;
    }

    @Doc("脚本打印时触发")
    @Override
    public void onScriptPrint(String text) {

    }

    public String readLog() throws IOException {

        return FileUtils.readFileToString(new File(getLogFile()));
    }

    @Doc("脚本加载时触发")
    @Override
    public void onLoadScript(String file) {
        scriptFile = file;
    }

    @Doc("音量键-按下时触发")
    @Override
    public void onVolumeDown(boolean running) {

    }

    @Doc("脚本运行时触发")
    @Override
    public void onScriptStart() {
        toast("开始运行");
    }

    @Doc("脚本停止时触发")
    @Override
    public void onScriptStop(String result) {
        toast("停止运行");
    }

    public void addService(String name, IpcService service) {
        JsDroidAppImpl.getInstance().addService(name, service);
    }

    public IJsDroidShell getJsDroidShell() {
        return JsDroidConnector.getInstance().getJsDroidShell();
    }

    public void startScript() {
        singleHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!connected) {
                    toast("服务未启动，请稍后重试！");
                    return;
                }
                IJsDroidShell jsDroidShell = getJsDroidShell();
                if (jsDroidShell != null) {
                    if (scriptFile == null) {
                        scriptFile = JsDroidAppEnv.getInstance().getScriptPath();
                    }
                    try {
                        jsDroidShell.runScript(scriptFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void stopScript() {
        singleHandler.post(new Runnable() {
            @Override
            public void run() {
                //这里会卡1秒，因为服务退出，会导致无结果返回，而ipc在等待结果
                IJsDroidShell jsDroidShell = getJsDroidShell();
                if (jsDroidShell != null) {
                    try {
                        jsDroidShell.exit();
                    } catch (InterruptedException ignore) {
                    }
                }
                JsDroidListener.getInstance().performStop(null);
            }
        });

    }

    public void execSingleThread(Runnable runnable) {
        singleHandler.post(runnable);
    }


    public boolean isRunning() {
        IJsDroidShell jsDroidShell = getJsDroidShell();
        if (jsDroidShell != null) {
            try {
                return jsDroidShell.isRunning();
            } catch (InterruptedException ignore) {
            }
        }
        return false;
    }
    public void toggleScript() {
        singleHandler.post(new Runnable() {
            @Override
            public void run() {
                //这里会卡1秒，因为服务退出，会导致无结果返回，而ipc在等待结果
                if (isRunning()) {
                    stopScript();
                } else {
                    startScript();
                }
            }
        });
    }

}
