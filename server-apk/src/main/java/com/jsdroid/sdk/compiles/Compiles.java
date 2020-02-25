package com.jsdroid.sdk.compiles;

import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.code.PositionList;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;
import com.jsdroid.script.JsDroidScript;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Compiles {
    private String sourceDir;
    private String pluginDir;
    private String apiDir;
    private String target;
    private CompilerConfiguration configuration;

    public Compiles(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public void setApiDir(String apiDir) {
        this.apiDir = apiDir;
    }

    public void setPluginDir(String pluginDir) {
        this.pluginDir = pluginDir;
    }

    public void build() throws IOException {
        configuration = new CompilerConfiguration();
        if (target == null) {
            target = "target";
        }
        configuration.setTargetDirectory(target);
        configuration.setScriptBaseClass(JsDroidScript.class.getName());
        List<String> libClassPath = getLibClassPath(pluginDir, apiDir);
        configuration.setClasspathList(libClassPath);
        CompilationUnit compilationUnit = new CompilationUnit(configuration);
        addSourceTree(compilationUnit, sourceDir);
        if (configuration.getTargetDirectory() != null) {
            compilationUnit.compile(8);
        } else {
            compilationUnit.compile(7);
        }
        //将所有文件合并
        //将class文件转为dex
        toDex();
    }

    private void toDex() throws IOException {
        DexOptions dexOptions = new DexOptions();
        CfOptions cfOptions = new CfOptions();
        dexOptions.targetApiLevel = 13;
        cfOptions.positionInfo = PositionList.LINES;
        cfOptions.localInfo = true;
        cfOptions.strictNameCheck = true;
        cfOptions.optimize = false;
        cfOptions.optimizeListFile = null;
        cfOptions.dontOptimizeListFile = null;
        cfOptions.statistics = false;

        DexFile dexFile = new DexFile(dexOptions);
        addClassFile(dexFile, cfOptions, new File(target), null);
        try (FileOutputStream outputStream = new FileOutputStream(new File(target, "classes.dex"));
             OutputStreamWriter humanOut = new OutputStreamWriter(new ByteArrayOutputStream())) {
            dexFile.writeTo(outputStream, humanOut, false);
        }

    }

    private void addClassFile(DexFile dexFile, CfOptions cfOptions, File file, String dir) throws IOException {

        if (file.isFile()) {
            if (dir == null) {
                dir = "";
            }
            if (file.getName().endsWith(".class")) {
                String filePath = dir + "/" + file.getName();
                if (filePath.startsWith("/")) {
                    filePath = filePath.substring(1);
                }
                byte[] bytes = FileUtils.readFileToByteArray(file);
                ClassDefItem classDefItem = CfTranslator.translate(filePath, bytes, cfOptions, dexFile.getDexOptions());
                dexFile.add(classDefItem);
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                if (dir == null) {
                    dir = "";
                } else {
                    dir = dir + "/" + file.getName();
                }
                for (File c : files) {

                    addClassFile(dexFile, cfOptions, c, dir);
                }
            }
        }
    }

    private void addSourceTree(CompilationUnit compilationUnit, String file) {
        if (file != null) {
            addSourceTree(compilationUnit, new File(file));
        }
    }

    private void addSourceTree(CompilationUnit compilationUnit, File file) {
        if (file.isFile() && file.getName().endsWith(".groovy")) {
            compilationUnit.addSource(file);
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    addSourceTree(compilationUnit, child);
                }
            }
        }
    }


    private List<String> getLibClassPath(String... libDirs) {
        List<String> arr = new ArrayList<>();
        if (libDirs == null) {
            return arr;
        }
        for (String libDir : libDirs) {
            if (libDir == null) {
                continue;
            }
            File dir = new File(libDir);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jar")) {
                        arr.add(file.getPath());
                    }
                }
            }
        }
        return arr;
    }

    public static void main(String[] args) throws IOException {
        long st  = System.currentTimeMillis();
        try {
            System.out.println(new File("target/test.groovy").getAbsolutePath());
            if (new File("target/test.groovy").exists()) {
                System.out.println("build");
                Compiles target = new Compiles("target");
                target.setApiDir("target");
                target.build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis()-st);
    }
}
