package com.jsdroid.server;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.IJsDroidShell;
import com.jsdroid.api.entity.DeviceInfo;
import com.jsdroid.api.entity.MyFile;
import com.jsdroid.ipc.call.ServiceProxy;
import com.jsdroid.script.JsDroidScript;
import com.jsdroid.sdk.apps.Apps;
import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.events.Event;
import com.jsdroid.sdk.events.EventListener;
import com.jsdroid.sdk.events.Events;
import com.jsdroid.sdk.inputs.Inputs;
import com.jsdroid.sdk.nodes.Node;
import com.jsdroid.sdk.nodes.Nodes;
import com.jsdroid.sdk.screens.Screens;
import com.jsdroid.sdk.scripts.Scripts;
import com.jsdroid.sdk.threads.SingleThread;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

import groovy.lang.Script;

public class JsDroidShell implements IJsDroidShell {
    protected JsDroidService service;
    protected IJsDroidApp app;
    protected boolean running;
    protected ServiceProxy serviceProxy;
    protected String pkg;

    @Override
    public void onAddService(String serviceId, ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    public void execute(Runnable runnable) {
        serviceProxy.execute(runnable);
    }

    public JsDroidShell(JsDroidService service, IJsDroidApp app) throws InterruptedException {
        this.service = service;
        this.app = app;
        this.pkg = app.getPackage();
        listenVolumeDown();
    }

    public void setApp(IJsDroidApp app) throws InterruptedException {
        this.app = app;
        this.pkg = app.getPackage();
        if (running) {
            if ("jsd.exe".equals(pkg)) {
                Apps.getRunnerApp().getApp().onScriptStart();
            } else {
                app.onScriptStart();
            }
        }
    }


    protected void listenVolumeDown() {
        Events.getInstance().onVolumeDown(new EventListener() {
            @Override
            public void onEvent(Event event) {
                execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            app.onVolumeDown(running);
                        } catch (InterruptedException e) {
                        }
                    }
                });
            }
        });
    }


    protected Bitmap capture(float scale) {
        try {
            return Screens.getInstance().capture(scale);
        } catch (InterruptedException e) {
        }
        return null;
    }


    protected int getDpi() {
        return Devices.getInstance().getDpi();
    }


    protected int getScreenWidth() {
        return Devices.getInstance().getWidth();
    }


    protected int getScreenHeight() {
        return Devices.getInstance().getHeight();
    }


    public String getNodes() throws InterruptedException {
        try {
            JSONArray arr = new JSONArray();
            List<Node> rootNodes = Nodes.getInstance().getRootNodes();
            for (Node rootNode : rootNodes) {
                arr.put(rootNode.toJsonString());
            }
            return arr.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void touchDown(int x, int y) throws InterruptedException {
        Events.getInstance().touchDown(x, y);
    }


    public void touchUp(int x, int y) throws InterruptedException {
        Events.getInstance().touchUp(x, y);
    }


    public void touchMove(int x, int y) throws InterruptedException {
        Events.getInstance().touchMove(x, y);
    }

    private void onScriptStart() {
        SingleThread.execute(new Runnable() {
            @Override
            public void run() {
                Inputs.getInstance().onScriptStart(pkg);
                try {
                    if ("jsd.exe".equals(pkg)) {
                        Apps.getRunnerApp().getApp().onScriptStart();
                    } else {
                        app.onScriptStart();
                    }
                } catch (InterruptedException e) {
                }
            }
        });

    }

    private void onScriptStop() {
        SingleThread.execute(new Runnable() {
            @Override
            public void run() {
                Inputs.getInstance().onScriptStop(pkg);
            }
        });
    }

    public boolean runScript(String file) throws InterruptedException {
        synchronized (JsDroidShell.this) {
            if (running) {
                return false;
            }
            setRunning(true);
        }
        if (file == null) {
            return false;
        }
        if (file.endsWith(".groovy")) {
            return runScript(file, true);
        }
        if (file.endsWith(".jsd")) {
            return runScript(file, false);
        }
        return false;
    }

    @Override
    public boolean runCode(String code) throws InterruptedException {
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Scripts.getInstance(pkg).setApp(app);
                    Script script = Scripts.getInstance(pkg).createGroovyScriptFromCode(code);
                    Object run = script.run();
                    sendScriptStop(run);
                } catch (Throwable e) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                         PrintStream printStream = new PrintStream(out)) {
                        e.printStackTrace(printStream);
                        sendScriptStop(out);
                    } catch (Exception ex) {
                    }
                } finally {
                    setRunning(false);
                }
            }
        });
        return true;
    }


    private boolean runScript(final String file, boolean isSource) throws InterruptedException {
        System.gc();
        Apps.loadScript(file);
        execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Scripts.getInstance(pkg).setApp(app);
                    Script script;
                    if (isSource) {
                        script = Scripts.getInstance(pkg).createGroovyScriptFromSource(file);
                    } else {
                        script = Scripts.getInstance(pkg).createGroovyScriptFromJsd(file);
                    }
                    if (script instanceof JsDroidScript) {
                        ((JsDroidScript) script).setApp(pkg, app);
                    }
                    Object run = script.run();
                    sendScriptStop(run);
                } catch (Throwable e) {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                         PrintStream printStream = new PrintStream(out)) {
                        e.printStackTrace(printStream);
                        sendScriptStop(out.toString());
                    } catch (Exception ex) {
                    }
                } finally {
                    setRunning(false);
                }
            }
        });
        return true;
    }

    private void setRunning(boolean running) {
        synchronized (JsDroidShell.this) {
            this.running = running;
            if (running) {
                onScriptStart();
            } else {
                onScriptStop();
            }
        }
    }

    private void sendScriptStop(final Object out) {
        execute(new Runnable() {
            @Override
            public void run() {
                try {

                    if (out == null) {
                        app.onScriptStop(null);
                    } else {
                        app.onScriptStop(out.toString());
                    }

                } catch (Exception ex) {
                }
                try {
                    if ("jsd.exe".equals(pkg)) {
                        Log.d("JsDroid", "stop jsd.exe: ");
                        if (out == null) {
                            Apps.getRunnerApp().getApp().onScriptStop(null);
                        } else {
                            Apps.getRunnerApp().getApp().onScriptStop(out.toString());
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void exit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (running) {
                        app.onScriptStop(null);
                    }
                    Thread.sleep(200);
                } catch (Exception e) {
                }
                System.exit(0);
            }
        }).start();
    }


    @Override
    public byte[] capturePng(float scale, int quality) throws InterruptedException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            capture(scale).compress(Bitmap.CompressFormat.PNG, quality, out);
            return out.toByteArray();
        } catch (Exception err) {
        }
        return null;
    }

    @Override
    public byte[] captureJpg(float scale, int quality) throws InterruptedException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            capture(scale).compress(Bitmap.CompressFormat.JPEG, quality, out);
            return out.toByteArray();
        } catch (Exception err) {
        }
        return null;
    }

    @Override
    public byte[] cap(int left, int top, int right, int bottom) throws InterruptedException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Bitmap capture = capture(1);
            Bitmap bitmap = Bitmap.createBitmap(capture, left, top, right - left, bottom - top);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return out.toByteArray();
        } catch (Exception err) {
        }
        return new byte[0];
    }


    @Override
    public DeviceInfo getDeviceInfo() throws InterruptedException {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.abi = Build.CPU_ABI;
        deviceInfo.brand = Build.BRAND;
        deviceInfo.dpi = getDpi();
        deviceInfo.model = Build.MODEL;
        deviceInfo.screenWidth = getScreenWidth();
        deviceInfo.screenHeight = getScreenHeight();
        deviceInfo.sdk = Build.VERSION.SDK_INT;
        try {
            deviceInfo.uuid = getUUID();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUUID() throws IOException {
        File file = new File("/data/local/tmp/.uuid");
        if (file.exists()) {
            return FileUtils.readFileToString(file);
        } else {
            String uuid = UUID.randomUUID().toString();
            FileUtils.writeStringToFile(file, uuid);
            return uuid;
        }
    }


    @Override
    public void input(String text) throws InterruptedException {

    }

    @Override
    public void clear(int before, int after) throws InterruptedException {

    }

    @Override
    public void pressKeyCode(int keyCode) throws InterruptedException {

    }

    @Override
    public void pressBack() throws InterruptedException {

    }

    @Override
    public void pressHome() throws InterruptedException {

    }

    @Override
    public void pressRecent() throws InterruptedException {

    }

    @Override
    public void wakeUp() throws InterruptedException {

    }

    @Override
    public MyFile[] getFiles(String path) throws InterruptedException {
        return new MyFile[0];
    }

    @Override
    public int openFile(String filename, boolean readMode) throws InterruptedException {
        return 0;
    }

    @Override
    public void closeFile(int id) throws InterruptedException {

    }

    @Override
    public byte[] readFile(int id, int length) throws InterruptedException {
        return new byte[0];
    }

    @Override
    public void writeFile(int id, byte[] data) throws InterruptedException {

    }

    @Override
    public String exec(String shell) throws InterruptedException {
        return null;
    }

    @Override
    public void openInputMethod() throws InterruptedException {

    }

    @Override
    public void closeInputMethod() throws InterruptedException {

    }

    @Override
    public boolean ping() throws InterruptedException {
        return true;
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
