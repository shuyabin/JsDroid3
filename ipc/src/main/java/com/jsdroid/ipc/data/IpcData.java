package com.jsdroid.ipc.data;

import java.io.IOException;


public interface IpcData {
    void write(Output output) throws IOException;

    void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException;
}
