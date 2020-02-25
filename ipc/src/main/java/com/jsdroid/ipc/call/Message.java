package com.jsdroid.ipc.call;

import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcData;
import com.jsdroid.ipc.data.Output;

import java.io.IOException;


public class Message implements IpcData {
    public static final int CALL = 0;
    public static final int RESULT = 1;
    public int type;

    public IpcData ipcData;

    @Override
    public void write(Output output) throws IOException {
        output.write(type);
        output.write(ipcData);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", ipcData=" + ipcData +
                '}';
    }

    @Override
    public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        type = input.read();
        ipcData = input.read();
    }


}
