package com.jsdroid.ipc.data;

import java.io.IOException;


public abstract class Input {
    protected abstract byte readByte() throws IOException;

    protected abstract short readShort() throws IOException;

    protected abstract char readChar() throws IOException;

    protected abstract int readInt() throws IOException;

    protected abstract long readLong() throws IOException;

    protected abstract float readFloat() throws IOException;

    protected abstract double readDouble() throws IOException;

    protected abstract boolean readBoolean() throws IOException;

    protected abstract IpcData readData(Class tClass) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException;


    protected abstract String readString() throws IOException;

    protected abstract byte[] readBytes() throws IOException;

    public abstract <T> T read() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    public abstract void close() throws IOException;

}
