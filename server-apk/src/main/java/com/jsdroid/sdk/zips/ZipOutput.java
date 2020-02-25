package com.jsdroid.sdk.zips;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 1.打包文件夹 规则：dir -> xxx.zip/
 * 2.打包文件 规则：dir -> xxx.zip/dir
 */
public class ZipOutput extends ZipBase {
    public ZipOutput(String filename) {
        super(filename);
    }

    private String getName(String file) {
        return new File(file).getName();
    }

    private boolean isFile(String file) {
        return new File(file).isFile();
    }

    /**
     * 打包文件夹
     * dir -> xxx.zip/
     *
     * @param dirs
     * @throws IOException
     */
    public void zipDirs(String... dirs) throws IOException {
        zipFiles(null, false, dirs);
    }

    public void zipDirs(ZipFilter zipFilter, String... dirs) throws IOException {
        zipFiles(zipFilter, false, dirs);
    }

    /**
     * 打包文件
     * dir -> xxx.zip/dir
     *
     * @param files
     * @throws IOException
     */
    public void zipFiles(String... files) throws IOException {
        zipFiles(null, true, files);
    }

    public void zipFiles(ZipFilter zipFilter, String... dirs) throws IOException {
        zipFiles(zipFilter, true, dirs);
    }

    private void zipFiles(ZipFilter filter, boolean packDir, String... files) throws IOException {
        File zipFile = new File(getFilename());
        zipFile.getParentFile().mkdirs();
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            for (String file : files) {
                String resFile = file;
                if (file == null) {
                    continue;
                }
                if (isFile(file)) {
                    String name = getName(file);
                    ZipEntry zipEntry = new ZipEntry(name);
                    zipOutputStream.putNextEntry(zipEntry);
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        IOUtils.copy(fileInputStream, zipOutputStream);
                    }
                } else {
                    File dir = new File(file);
                    int abstractPathPos;
                    if (packDir) {
                        abstractPathPos = dir.getCanonicalFile().getParentFile().getCanonicalPath().length() + 1;
                    } else {
                        abstractPathPos = dir.getCanonicalPath().length() + 1;
                    }
                    FileEach.eachFile(dir, new FileEach() {
                        @Override
                        public boolean each(File file,int depth) {
                            if (filter != null) {
                                if (!filter.need(resFile, file)) {
                                    return false;
                                }
                            }
                            if (file.isFile()) {
                                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                    String relativePath = file.getCanonicalPath().substring(abstractPathPos);
                                    relativePath = relativePath.replace("\\", "/");
                                    ZipEntry zipEntry = new ZipEntry(relativePath);
                                    zipOutputStream.putNextEntry(zipEntry);
                                    IOUtils.copy(fileInputStream, zipOutputStream);
                                    return false;
                                } catch (Exception e) {
                                }
                            } else {
                                return false;
                            }
                            return true;
                        }
                    });
                }
            }
        }
    }

}
