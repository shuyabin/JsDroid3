package com.jsdroid.sdk.zips;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 1.解压指定文件到指定文件夹
 * 2.解压得到指定文件内容
 */
public class ZipInput extends ZipBase {
    public ZipInput(String filename) {
        super(filename);
    }

    public void unzipFileToDir(final String file, final String outDir) throws IOException {
        if (!new File(outDir).exists()) {
            new File(outDir).mkdirs();
        }
        //file -> outDir/file
        eachFile(new ZipEach() {
            @Override
            public boolean each(ZipInputStream zipInputStream, ZipEntry entry) throws IOException {

                String name = entry.getName();
                if (name.startsWith(file)) {
                    if (entry.isDirectory()) {
                        new File(outDir, name).mkdirs();
                    } else {
                        new File(outDir, name).getParentFile().mkdirs();
                        try (FileOutputStream fileOutputStream = new FileOutputStream(new File(outDir, name))) {
                            IOUtils.copy(zipInputStream, fileOutputStream);
                        }
                    }
                }
                return false;
            }
        });
    }

    public void unzipToDir(final String outDir) throws IOException {
        if (!new File(outDir).exists()) {
            new File(outDir).mkdirs();
        }
        eachFile(new ZipEach() {
            @Override
            public boolean each(ZipInputStream zipInputStream, ZipEntry entry) throws IOException {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    return false;
                }
                File file = new File(outDir, name);
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                try (FileOutputStream fileOutputStream = new FileOutputStream(new File(outDir, name))) {
                    IOUtils.copy(zipInputStream, fileOutputStream);
                }
                return false;
            }
        });
    }

    public void eachFile(ZipEach each) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(getFilename());
             ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);) {
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                if (each.each(zipInputStream, nextEntry)) {
                    break;
                }
            }
        }
    }

}
