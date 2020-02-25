package com.jsdroid.sdk.scripts;

import dalvik.system.DexClassLoader;

public class BaseClassLoader extends DexClassLoader {

    public BaseClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);

    }

}
