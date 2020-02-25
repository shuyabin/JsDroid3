package com.jsdroid.sdk.zips;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipBase {

    private String filename;

    public ZipBase(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }


}
