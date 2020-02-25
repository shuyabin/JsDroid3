package com.jsdroid.runner.utils;

import android.os.Build;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LibUtil {

    public static List<String> extractLibFile(String apkFile, String outDir) throws IOException {
        List<String> supportABIs = getSupportABIs();
        List<String> libFiles = new ArrayList<>();
        try (
                FileInputStream fileInput = new FileInputStream(apkFile);
                ZipInputStream zipInput = new ZipInputStream(fileInput);

        ) {
            while (true) {
                //lib/x86/libxxx.so
                ZipEntry nextEntry = zipInput.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                String name = nextEntry.getName();
                if (name.endsWith(".so")) {
                    File file = new File(name);
                    String libFile = file.getName();

                    //获取abi，判断是否在支持
                    File abiDir = file.getParentFile();
                    if (abiDir == null) {
                        continue;
                    }
                    //判断是否为lib文件夹
                    File libDir = abiDir.getParentFile();
                    if (libDir == null) {
                        continue;
                    }

                    if (!libDir.getName().equals("lib")) {
                        continue;
                    }

                    if (libDir.getParentFile() != null) {
                        if (!libDir.getParent().equals(".")) {
                            continue;
                        }
                    }

                    String abi = abiDir.getName();
                    //判断是否为支持的abi
                    if (!supportABIs.contains(abi)) {
                        continue;
                    }
                    //已经解压同名的so文件，并且当前so的abi不是默认abi，则不再继续解压，优选支持默认的abi
                    if (libFiles.contains(libFile) && (!supportABIs.get(0).equals(abi))) {
                        continue;
                    }
                    Log.d("jsdroid", "extractLibFile abi:" + abi + " lib:" + libFile);
                    //解压so到outDir
                    try (FileOutputStream libOutput = new FileOutputStream(new File(outDir, libFile))) {
                        IOUtils.copy(zipInput, libOutput);
                        libFiles.add(libFile);
                    } catch (IOException e) {
                    }
                }
            }
        }

        return libFiles;
    }

    public static List<String> getSupportABIs() {
        List<String> ret = new ArrayList<>();
        try {
            if (!ret.contains(Build.CPU_ABI)) {
                if (Build.CPU_ABI != null)
                    ret.add(Build.CPU_ABI);
                Log.d("JsDroid", "getSupportABIs: " + Build.CPU_ABI);
            }
        } catch (Throwable ignore) {
        }
        try {
            if (!ret.contains(Build.CPU_ABI2)) {
                if (Build.CPU_ABI2 != null)
                    ret.add(Build.CPU_ABI2);
            }
        } catch (Throwable ignore) {
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String supportedAbi : Build.SUPPORTED_ABIS) {
                    if (supportedAbi != null) {
                        if (!ret.contains(supportedAbi)) {
                            ret.add(supportedAbi);
                        }
                    }

                }
            }
        } catch (Throwable ignore) {
        }
        return ret;
    }
}
