package com.jsdroid.test;

import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.UiMessageUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.jsdroid.runner.JsDroidApplication;

import java.io.File;


public class JsdApp extends JsDroidApplication {
    private String scriptFile;
    private boolean volumeControl;
    private boolean showFloatView;

    @Override
    public void onCreate() {
        super.onCreate();
        readFloatWindowState();
        readVolumeControlState();
        FloatLogo.getInstance().init(this);
        FloatMenu.getInstance().init(this);
        if (showFloatView) {
            //3秒后显示
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FloatLogo.getInstance().show();
                }
            }, 3000);

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FloatLogo.getInstance().onConfigureChanged();
        FloatMenu.getInstance().hide();
    }

    @Override
    public void onJsDroidConnected() {
        super.onJsDroidConnected();
        UiMessageUtils.getInstance().send(UiMessage.JSDROID_CONNECT);
    }

    @Override
    public void onScriptStart() {
        super.onScriptStart();
        UiMessageUtils.getInstance().send(UiMessage.SRIPT_START);
    }

    @Override
    public void onScriptStop(String result) {
        super.onScriptStop(result);
        if (result != null) {
            UiMessageUtils.getInstance().send(UiMessage.SRIPT_STOP, result);
        } else {
            UiMessageUtils.getInstance().send(UiMessage.SRIPT_STOP);
        }
    }

    @Override
    public void onJsDroidServerStop() {
        super.onJsDroidServerStop();
    }

    @Override
    public void onJsDroidDisconnected() {
        super.onJsDroidDisconnected();
        UiMessageUtils.getInstance().send(UiMessage.JSDROID_DISCONNECT);
    }

    @Override
    public void onScriptPrint(String text) {
        super.onScriptPrint(text);
        UiMessageUtils.getInstance().send(UiMessage.PRINT, text);
    }

    private void readFloatWindowState() {
        showFloatView = Boolean.parseBoolean(readConfig("jsd_float_menu", "true"));
    }

    private void readVolumeControlState() {
        volumeControl = Boolean.parseBoolean(readConfig("jsd_volume_control", "false"));
    }

    public void switchFloatWindowState(boolean state) {
        showFloatView = state;
        saveConfig("jsd_float_menu", Boolean.toString(state));
        if (state) {
            FloatLogo.getInstance().show();
        } else {
            FloatLogo.getInstance().hide();
            FloatMenu.getInstance().hide();
        }
    }


    public void switchVolumeControlState(boolean state) {
        volumeControl = state;
        saveConfig("jsd_volume_control", Boolean.toString(state));
    }

    @Override
    public void onVolumeDown(boolean running) {
        super.onVolumeDown(running);
        if (volumeControl) {
            startScript();
        }
    }

    @Override
    public void onLoadScript(final String file) {
        super.onLoadScript(file);
        scriptFile = file;
        ThreadUtils.executeByCached(new ThreadUtils.Task<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                ZipUtils.unzipFile(new File(file), getScriptDir());
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                UiMessageUtils.getInstance().send(UiMessage.LOAD_SCRIPT, file);
                UiMessageUtils.getInstance().send(UiMessage.OPTION_CHANGED, file);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        });
    }

    public File getScriptDir() {
        return getDir("script_dir", 0);
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public boolean isShowFloatView() {
        return showFloatView;
    }

    public boolean isVolumeControl() {
        return volumeControl;
    }
}
