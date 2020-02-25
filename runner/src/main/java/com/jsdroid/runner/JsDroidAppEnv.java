package com.jsdroid.runner;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class JsDroidAppEnv {
    private static class Single {
        static JsDroidAppEnv single = new JsDroidAppEnv();
    }

    public static JsDroidAppEnv getInstance() {
        return Single.single;
    }

    private File dataDir;
    private File bootstrapFile;
    private File shFile;
    private File serverFile;
    private File scriptFile;
    private File sdkFile;
    private String command;

    private JsDroidAppEnv() {
    }

    public void init(Context context) {
        dataDir = context.getFilesDir().getParentFile();
        shFile = new File(dataDir, "jsdroid.sh");
        scriptFile = new File(dataDir, "main.jsd");
        serverFile = new File(dataDir, "server.apk");
        sdkFile = new File(dataDir, "sdk.apk");
        bootstrapFile = getBootstrapFile(context);
        command = "sh " + shFile.getPath();
        try (
                InputStream jsdroidInputStream = context.getResources().openRawResource(R.raw.jsdroid);
                InputStream serverFileInputStream = context.getResources().openRawResource(R.raw.server);
                InputStream sdkFileInputStream = getSdkAssetsStream(context);
                FileOutputStream serverFileOutputStream = new FileOutputStream(serverFile);
                FileOutputStream sdkFileOutputStream = new FileOutputStream(sdkFile);
                PrintWriter pw = new PrintWriter(shFile);
        ) {
            pw.println("path=" + bootstrapFile);
            pw.println("abi64=" + bootstrapFile.getPath().contains("64"));
            pw.println("export server_apk=" + serverFile.getPath());
            pw.println("export sdk_apk=" + sdkFile.getPath());
            pw.println("export app_pkg=" + context.getPackageName());
            String ime_id = context.getPackageName() + "/" + new ComponentName(context.getPackageName(),
                    Input.class.getName()).getShortClassName();
            pw.println("export ime_id=" + ime_id);
            pw.println("export CLASSPATH=" + context.getPackageCodePath());
            pw.println();
            pw.println(IOUtils.toString(jsdroidInputStream));
            IOUtils.copy(serverFileInputStream, serverFileOutputStream);
            IOUtils.copy(sdkFileInputStream, sdkFileOutputStream);

        } catch (Exception err) {
            Log.d("JsDroid", "init err: ", err);
        }
        try (InputStream input = context.getResources().getAssets().open("main.jsd");
             FileOutputStream outputStream = new FileOutputStream(scriptFile);
        ) {
            IOUtils.copy(input, outputStream);
        } catch (Exception err) {
            Log.d("JsDroid", "init err: ", err);
        }
        setWriteAble(dataDir);
        setWriteAble(shFile);
        setWriteAble(serverFile);
        setWriteAble(scriptFile);
        setWriteAble(sdkFile);
        JsDroidListener.getInstance().performScriptLoad(scriptFile.getPath());
    }

    private InputStream getSdkAssetsStream(Context context) throws IOException {
        String file = "sdk" + Build.VERSION.SDK_INT + ".apk";
        try {
            String[] list = context.getAssets().list("");
            if (Arrays.asList(list).contains(file)) {
                return context.getAssets().open(file);
            }
        } catch (Exception e) {
        }
        return context.getAssets().open("sdk28.apk");
    }

    public File getDataDir() {
        return dataDir;
    }

    public File getBootstrapFile() {
        return bootstrapFile;
    }

    public File getShFile() {
        return shFile;
    }

    public File getServerFile() {
        return serverFile;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public String getScriptPath() {
        if (scriptFile != null) {
            return scriptFile.getPath();
        }
        return null;
    }

    public String getCommand() {
        return command;
    }

    private void setWriteAble(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            return;
        }
        file.setWritable(true, false);
        file.setReadable(true, false);
        file.setExecutable(true, false);
    }

    private File getBootstrapFile(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            String nativeLibraryDir = packageManager
                    .getApplicationInfo(context.getPackageName(), 0).nativeLibraryDir;
            File file = new File(nativeLibraryDir, "libjsdroid.so");
            if (file.exists()) {
                return file;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }
}
