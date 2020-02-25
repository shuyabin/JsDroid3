package com.jsdroid.ipc.service;

import java.io.IOException;

public interface IServer {
    ISocket accept() throws IOException;

    void close() throws IOException;
}
