package com.jsdroid.sdk.scripts;

public class ScriptClassLoader extends BaseClassLoader {
    public ScriptClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath) {
        super(dexPath, optimizedDirectory, librarySearchPath, ScriptClassLoader.class.getClassLoader());
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.loadClass(name);
        } catch (Throwable e) {
        }
        return PluginClassLoader.getInstance().loadClass(name);
    }

}
