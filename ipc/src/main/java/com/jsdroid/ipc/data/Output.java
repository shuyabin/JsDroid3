package com.jsdroid.ipc.data;


import java.io.IOException;


public abstract class Output {
    protected abstract void writeShort(short b) throws IOException;

    protected abstract void writeChar(char ch) throws IOException;

    protected abstract void writeByte(byte b) throws IOException;

    protected abstract void writeInt(int b) throws IOException;

    protected abstract void writeLong(long l) throws IOException;

    protected abstract void writeFloat(float f) throws IOException;

    protected abstract void writeBoolean(boolean b) throws IOException;

    protected abstract void writeDouble(double d) throws IOException;

    protected abstract void writeString(String s) throws IOException;

    protected abstract void writeBytes(byte[] bytes) throws IOException;

    protected abstract void writeData(IpcData ipcData) throws IOException;

    public abstract void close() throws IOException;

    /**
     * 支持能被序列化的Json类型和Data
     *
     * @param data
     * @throws IOException
     */
    public abstract void write(Object data) throws IOException;

    public abstract void flush() throws IOException;
}
