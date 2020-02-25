package com.jsdroid.ipc.call;


import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcData;
import com.jsdroid.ipc.data.Output;

import java.io.IOException;
import java.util.Arrays;


public class CallData implements IpcData {
    public String name;
    public long id;
    public String methodName;
    public Object params[];
    public String paramTypes[];

    @Override
    public void write(Output output) throws IOException {
        output.write(id);
        output.write(name);
        output.write(methodName);
        output.write(paramTypes);
        output.write(params);
    }

    @Override
    public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        id = input.read();
        name = input.read();
        methodName = input.read();
        paramTypes = input.read();

        params = input.read();

    }

}
