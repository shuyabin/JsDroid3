package com.jsdroid.ipc.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISocket {
    void setAddress(String ip, int port) throws IOException;

    void connect() throws IOException;

    void close() throws IOException;

    boolean isClosed();

    InputStream getInputStream() throws IOException;

    OutputStream getOutPutStream() throws IOException;

}
