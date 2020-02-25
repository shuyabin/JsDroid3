package com.jsdroid.sdk.zips;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public interface ZipEach {
    boolean each(ZipInputStream zipInputStream, ZipEntry entry) throws IOException;
}
