package com.jsdroid.sdk.zips;

import java.io.File;

public interface ZipFilter {
    boolean need(String file, File zipFile);
}
