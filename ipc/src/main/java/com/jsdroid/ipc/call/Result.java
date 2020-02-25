package com.jsdroid.ipc.call;


import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcData;
import com.jsdroid.ipc.data.Output;

import java.io.IOException;


public class Result implements IpcData {
    public long id;
    public Object data;

    @Override
    public void write(Output output) throws IOException {
        output.write(id);
        output.write(data);
    }

    @Override
    public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        id = input.read();
        data = input.read();
    }
}
