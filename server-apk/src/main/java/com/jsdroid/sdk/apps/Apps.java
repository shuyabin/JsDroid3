package com.jsdroid.sdk.apps;

import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Apps {
    private static Map<String, Apps> instanceMap = new HashMap<>();

    private static Apps runnerApp;

    public static Apps getInstance(String pkg) {
        return instanceMap.get(pkg);
    }

    //获取运行app
    public static Apps getRunnerApp() {
        return runnerApp;
    }

    public synchronized static void putApp(String pkg, IJsDroidApp app) {
        Apps apps = new Apps(pkg, app);
        String app_pkg = System.getenv("app_pkg");
        if (pkg.equals(app_pkg)) {
            runnerApp = apps;
        }
        instanceMap.put(pkg, apps);
    }

    private String pkg;
    private IJsDroidApp app;

    public Apps(String pkg, IJsDroidApp app) {
        this.pkg = pkg;
        this.app = app;
    }

    public static void loadScript(String file) throws InterruptedException {
        Apps runnerApp = getRunnerApp();
        if (runnerApp != null && "com.jsdroid.test".equals(runnerApp.pkg)) {
            new File(file).setExecutable(true, false);
            new File(file).setReadable(true, false);
            new File(file).setWritable(true, false);
            runnerApp.app.loadScript(file);
        }
    }

    public String readConfig(String key, String defaultValue) {
        if ("jsd.exe".equals(key)) {
            Apps defaultApp = Apps.getInstance("com.jsdroid.test");
            if (defaultApp == null) {
                return defaultValue;
            }
            return defaultApp.readConfig(key, defaultValue);
        }
        try {
            return app.readConfig(key, defaultValue);
        } catch (InterruptedException e) {
        }
        return null;
    }

    public IInput getInput() throws InterruptedException {
        return app.getInput();
    }

    public void println() {
        doPrint("");
    }

    public void print(Object value) {
        doPrint("" + value);
    }

    public void toast(Object value) {
        try {
            app.toast("" + value);
        } catch (InterruptedException e) {
        }
    }

    private void doPrint(String text) {
        try {
            app.print(text);
        } catch (Exception e) {
        }
        if ("jsd.exe".equals(pkg)) {
            Apps runnerApp = getRunnerApp();
            if (runnerApp != null) {
                runnerApp.doPrint(text);
            }
        }
    }

    public void println(Object value) {
        doPrint("" + value);

    }

    public void printf(String format, Object value) {
        printf(format, value);
    }

    public void printf(String format, Object... values) {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(out);
        ) {
            pw.printf(format, values);
            pw.flush();
            doPrint(out.toString());
        } catch (IOException e) {
        }
    }

    public IJsDroidApp getApp() {
        return app;
    }
}
