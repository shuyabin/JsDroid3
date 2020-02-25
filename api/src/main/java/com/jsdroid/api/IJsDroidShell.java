package com.jsdroid.api;

import com.jsdroid.api.entity.DeviceInfo;
import com.jsdroid.api.entity.MyFile;
import com.jsdroid.ipc.annotations.Timeout;
import com.jsdroid.ipc.data.IpcService;

/**
 * app连接脚本服务：获取截图，运行脚本。
 * idea连接脚本服务：运行脚本，停止脚本，退出服务。
 * 手机助手连接脚本服务：截图，获取节点。
 */
public interface IJsDroidShell extends IpcService {

    @Timeout(1000)
    boolean runScript(String file) throws InterruptedException;

    @Timeout(1000)
    boolean runCode(String code) throws InterruptedException;

    @Timeout(1000)
    void exit() throws InterruptedException;

    /**
     * png截图
     *
     * @param scale   缩放
     * @param quality 质量
     * @return
     */
    @Timeout(5000)
    byte[] capturePng(float scale, int quality) throws InterruptedException;

    /**
     * jpg截图
     *
     * @param scale   缩放
     * @param quality 质量
     * @return
     */
    @Timeout(1000)
    byte[] captureJpg(float scale, int quality) throws InterruptedException;

    @Timeout(3000)
    byte[] cap(int left, int top, int right, int bottom) throws InterruptedException;

    /**
     * 获取屏幕节点信息
     *
     * @return
     */
    @Timeout(5000)
    String getNodes() throws InterruptedException;

    @Timeout(1000)
    void touchDown(int x, int y) throws InterruptedException;

    @Timeout(1000)
    void touchUp(int x, int y) throws InterruptedException;

    @Timeout(1000)
    void touchMove(int x, int y) throws InterruptedException;

    @Timeout(3000)
    DeviceInfo getDeviceInfo() throws InterruptedException;

    @Timeout(1000)
    void input(String text) throws InterruptedException;

    @Timeout(1000)
    void clear(int before, int after) throws InterruptedException;


    @Timeout(1000)
    void pressKeyCode(int keyCode) throws InterruptedException;

    @Timeout(1000)
    void pressBack() throws InterruptedException;

    @Timeout(1000)
    void pressHome() throws InterruptedException;

    @Timeout(1000)
    void pressRecent() throws InterruptedException;

    @Timeout(1000)
    void wakeUp() throws InterruptedException;

    @Timeout(1000)
    MyFile[] getFiles(String path) throws InterruptedException;

    /**
     * 打开文件
     *
     * @param filename 文件名
     * @param readMode 读取模式
     * @return
     * @throws InterruptedException
     */
    @Timeout(1000)
    int openFile(String filename, boolean readMode) throws InterruptedException;

    /**
     * 关闭文件
     *
     * @param id
     * @throws InterruptedException
     */
    @Timeout(1000)
    void closeFile(int id) throws InterruptedException;

    /**
     * 读取文件
     *
     * @param id
     * @return
     * @throws InterruptedException
     */
    @Timeout(10000)
    byte[] readFile(int id, int length) throws InterruptedException;

    /**
     * 写入文件
     *
     * @param id
     * @param data
     * @throws InterruptedException
     */
    @Timeout(10000)
    void writeFile(int id, byte[] data) throws InterruptedException;

    /**
     * 执行shell命令
     *
     * @param shell
     * @return
     * @throws InterruptedException
     */
    @Timeout(20000)
    String exec(String shell) throws InterruptedException;

    /**
     * 打开输入法
     *
     * @throws InterruptedException
     */
    @Timeout(1000)
    void openInputMethod() throws InterruptedException;

    /**
     * 关闭输入法
     *
     * @throws InterruptedException
     */
    @Timeout(1000)
    void closeInputMethod() throws InterruptedException;


    //ping一下，看是否连接正常
    @Timeout(1000)
    boolean ping() throws InterruptedException;

    /**
     * 是否在运行中
     *
     * @return
     */
    @Timeout(1000)
    boolean isRunning() throws InterruptedException;

}