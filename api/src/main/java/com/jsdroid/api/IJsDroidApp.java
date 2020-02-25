package com.jsdroid.api;

import com.jsdroid.ipc.annotations.Timeout;
import com.jsdroid.ipc.data.IpcService;

/**
 * 服务给应用发送相关指令，例如让应用弹出toast，获取用户配置
 */
public interface IJsDroidApp extends IpcService {

    @Timeout(1000)
    void toast(String text) throws InterruptedException;

    @Timeout(1000)
    String getPackage() throws InterruptedException;

    @Timeout(1000)
    String getVersion() throws InterruptedException;

    //保存脚本日志
    @Timeout(1000)
    void print(String text) throws InterruptedException;

    @Timeout(1000)
    void onScriptStart() throws InterruptedException;

    //脚本运行停止
    @Timeout(1000)
    void onScriptStop(String result) throws InterruptedException;

    //音量键触发
    @Timeout(1000)
    void onVolumeDown(boolean scriptRunning) throws InterruptedException;

    @Timeout(1000)
    IInput getInput() throws InterruptedException;

    @Timeout(10000)
    void loadScript(String file) throws InterruptedException;

    @Timeout(1000)
    String readConfig(String key, String defaultValue) throws InterruptedException;


}
