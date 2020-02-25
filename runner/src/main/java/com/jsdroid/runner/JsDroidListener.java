package com.jsdroid.runner;

public class JsDroidListener {


    public interface OnVolumeDownListener {
        void onVolumeDown(boolean running);
    }

    public interface OnPrintListener {
        void onScriptPrint(String text);
    }

    public interface OnStopListener {
        void onScriptStop(String result);
    }

    public interface OnToastListener {
        void onToast(String text);
    }

    public interface OnScriptLoadListener {
        void onLoadScript(String file);
    }

    public interface ConnectListener {
        void onJsDroidConnected();

        void onJsDroidDisconnected();
    }

    public interface OnScriptRunningListener {
        void onScriptStart();
    }

    private static class Single {
        static JsDroidListener instance = new JsDroidListener();
    }

    public static JsDroidListener getInstance() {
        return Single.instance;
    }


    private OnVolumeDownListener onVolumeDownListener;
    private OnPrintListener onPrintListener;
    private OnStopListener onStopListener;
    private OnToastListener onToastListener;
    private OnScriptLoadListener onScriptLoadListener;
    private ConnectListener connectListener;
    private OnScriptRunningListener onScriptStartListener;

    private JsDroidListener() {
    }

    public void setOnVolumeDownListener(OnVolumeDownListener onVolumeDownListener) {
        this.onVolumeDownListener = onVolumeDownListener;
    }

    public void setOnPrintListener(OnPrintListener onPrintListener) {
        this.onPrintListener = onPrintListener;
    }

    public void setOnStopListener(OnStopListener onStopListener) {
        this.onStopListener = onStopListener;
    }

    public void setOnToastListener(OnToastListener onToastListener) {
        this.onToastListener = onToastListener;
    }

    public void setOnScriptLoadListener(OnScriptLoadListener onScriptLoadListener) {
        this.onScriptLoadListener = onScriptLoadListener;
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void setOnScriptStartListener(OnScriptRunningListener onScriptStartListener) {
        this.onScriptStartListener = onScriptStartListener;
    }

    public void performVolumeDown(boolean running) {
        if (onVolumeDownListener != null) {
            onVolumeDownListener.onVolumeDown(running);
        }
    }

    public void performPrint(String text) {
        if (onPrintListener != null) {
            onPrintListener.onScriptPrint(text);
        }
    }

    public void performStop(String result) {
        if (onStopListener != null) {
            onStopListener.onScriptStop(result);
        }
    }

    public void performToast(String text) {
        if (onToastListener != null) {
            onToastListener.onToast(text);
        }
    }

    public void performScriptLoad(String file) {
        if (onScriptLoadListener != null) {
            onScriptLoadListener.onLoadScript(file);
        }
    }

    public void performConnected() {
        if (connectListener != null) {
            connectListener.onJsDroidConnected();
        }
    }

    public void performDisconnected() {
        if (connectListener != null) {
            connectListener.onJsDroidDisconnected();
        }
    }

    public void performScriptStart() {
        if (onScriptStartListener != null) {
            onScriptStartListener.onScriptStart();
        }
    }
}
