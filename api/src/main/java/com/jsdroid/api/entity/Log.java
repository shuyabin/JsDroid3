package com.jsdroid.api.entity;

import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcData;
import com.jsdroid.ipc.data.Output;

import java.io.IOException;

public class Log implements IpcData {
    private long time;
    private String content;
    private int type;//bug/print
    private String filename;
    private String className;
    private String methodName;
    private int lineNumber;

    @Override
    public void write(Output output) throws IOException {
        output.write(time);
        output.write(content);
        output.write(filename);
        output.write(className);
        output.write(methodName);
        output.write(lineNumber);
    }

    @Override
    public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        time = input.read();
        content = input.read();
        filename = input.read();
        className = input.read();
        methodName = input.read();
        lineNumber = input.read();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
