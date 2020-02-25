package com.jsdroid.api.entity;

import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcData;
import com.jsdroid.ipc.data.Output;

import java.io.IOException;

public class MyFile implements IpcData {
    public boolean file;
    public String path;
    public boolean hasChild;

    @Override
    public void write(Output output) throws IOException {
        output.write(file);
        output.write(path);
        output.write(hasChild);
    }

    @Override
    public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        file = input.read();
        path = input.read();
        hasChild = input.read();
    }

    @Override
    public String toString() {
        if (path != null) {
            String[] split = path.split("/|\\\\");
            if (split.length > 0) {
                return split[split.length - 1];
            } else {
                return "";
            }
        }
        return path;
    }
}
