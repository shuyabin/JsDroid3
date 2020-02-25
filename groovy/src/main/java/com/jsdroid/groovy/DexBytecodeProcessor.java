package com.jsdroid.groovy;

import android.util.Log;

import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.code.PositionList;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.BytecodeProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * 动态编译groovy文件的时候，需要将生成的class文件转换为dex文件
 */
class DexBytecodeProcessor implements BytecodeProcessor {
    private DexFile dexFile;
    private final DexOptions dexOptions;
    private final CfOptions cfOptions;
    private ArrayList<String> classNames;

    public DexBytecodeProcessor() {
        dexOptions = new DexOptions();
        dexFile = new DexFile(dexOptions);
        cfOptions = new CfOptions();
        classNames = new ArrayList<>();
        config();
    }

    private void config() {
        dexOptions.targetApiLevel = 13;
        cfOptions.positionInfo = PositionList.LINES;
        cfOptions.localInfo = true;
        cfOptions.strictNameCheck = true;
        cfOptions.optimize = false;
        cfOptions.optimizeListFile = null;
        cfOptions.dontOptimizeListFile = null;
        cfOptions.statistics = false;
    }

    @Override
    public byte[] processBytecode(ClassNode classNode, byte[] bytes) {
        String pkgName = classNode.getPackageName();
        String className = classNode.getName();
        classNames.add(className);
        String filePath;
        if (pkgName != null) {
            pkgName = pkgName.replace(".", "/");
            className = className.substring(pkgName.length() + 1);
            filePath = pkgName + "/" + className + ".class";
        } else {
            filePath = className + ".class";
        }
        ClassDefItem classDefItem = CfTranslator.translate(filePath, bytes, cfOptions, dexOptions);
        dexFile.add(classDefItem);
        return bytes;
    }

    public byte[] getDexBytes() throws IOException {
        try (OutputStreamWriter humanOut = new OutputStreamWriter(new ByteArrayOutputStream())) {
            return dexFile.toDex(humanOut, false);
        }
    }

    public void writeDexToFile(OutputStream outputStream) throws IOException {
        try (OutputStreamWriter humanOut = new OutputStreamWriter(new ByteArrayOutputStream())) {
            dexFile.writeTo(outputStream, humanOut, false);
        }
    }

    public ArrayList<String> getClassNames() {
        return classNames;
    }
}
