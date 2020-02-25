package com.jsdroid.groovy;

import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import dalvik.system.DexClassLoader;
import groovy.lang.Script;

/**
 * 此类用于加载groovy文件,安卓不能直接加载class文件，需要将class文件转换为dex文件才能加载到内存。
 * 调用Groovy中的GroovyClassloader得到class文件，通过google的dx工具将class文件转换为dex文件，然后通过DexClassLoader将dex文件加载进来即可。
 */
public class AndroidGroovyScriptLoader {
    private String dexOptDir;
    private String dexTmpDir;
    private String scriptBaseClass;

    public AndroidGroovyScriptLoader(String dexOptDir, String dexTmpDir, String scriptBaseClass) {
        this.dexOptDir = dexOptDir;
        this.dexTmpDir = dexTmpDir;
        this.scriptBaseClass = scriptBaseClass;

    }

    public Script loadScript(Script parent, String sourceText, String fileName) throws IOException {
        DexBytecodeProcessor bytecodeProcessor = new DexBytecodeProcessor();
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(scriptBaseClass);
        configuration.setBytecodePostprocessor(bytecodeProcessor);
        ClassLoader parentClassLoader = AndroidGroovyScriptLoader.class.getClassLoader();
        if (parent != null) {
            parentClassLoader = parent.getClass().getClassLoader();
        }
        AndroidGroovyClassLoader androidGroovyClassLoader = new AndroidGroovyClassLoader(parentClassLoader, configuration);
        try {
            androidGroovyClassLoader.parseClass(sourceText, fileName);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Script script = createScript(parentClassLoader, bytecodeProcessor);
        if (script != null && parent != null) {
            script.setBinding(parent.getBinding());
        }
        return script;

    }

    private Script createScript(ClassLoader parentClassLoader, DexBytecodeProcessor bytecodeProcessor) throws IOException {
        File tmpDex = new File(dexTmpDir, UUID.randomUUID() + ".dex");
        try (FileOutputStream out = new FileOutputStream(tmpDex)) {
            bytecodeProcessor.writeDexToFile(out);
        }
        try {
            Class scriptClass = null;
            DexClassLoader loader = new DexClassLoader(tmpDex.getPath(), dexOptDir,
                    null,
                    parentClassLoader);
            for (String className : bytecodeProcessor.getClassNames()) {
                Class<?> aClass = loader.loadClass(className);
                if (Script.class.isAssignableFrom(aClass)) {
                    scriptClass = aClass;
                }
            }
            if (scriptClass != null) {
                return (Script) scriptClass.newInstance();
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            Log.d("JsDroid", "loadClass err ", e);
        } finally {
            tmpDex.delete();
        }
        return null;
    }

}
