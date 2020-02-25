package com.jsdroid.sdk.zips;

import java.io.File;

public interface FileEach {

    boolean each(File file, int depth);

    static boolean eachFile(File file, FileEach fileEach) {
        return eachFile(file, fileEach, 0);
    }

    static boolean eachFile(File file, FileEach fileEach, int depth) {
        if (file.isFile()) {
            if (fileEach.each(file, depth)) {
                return true;
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (eachFile(child, fileEach, depth + 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
