package com.jsdroid.ipc.data;

import com.alibaba.fastjson.JSON;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class DataOutput extends Output {
    public void close() throws IOException {
        outputStream.close();
    }

    @Override
    public void write(Object data) throws IOException {
        if (data == null) {
            writeByte((byte) 0);
        } else if (data instanceof String) {
            writeByte((byte) 9);
            writeString((String) data);
        } else if (data instanceof Short) {
            writeByte((byte) 1);
            writeShort((short) data);
        } else if (data instanceof Character) {
            writeByte((byte) 2);
            writeChar((char) data);
        } else if (data instanceof Byte) {
            writeByte((byte) 3);
            writeByte((byte) data);
        } else if (data instanceof Integer) {
            writeByte((byte) 4);
            writeInt((int) data);
        } else if (data instanceof Long) {
            writeByte((byte) 5);
            writeLong((long) data);
        } else if (data instanceof Float) {
            writeByte((byte) 6);
            writeFloat((float) data);
        } else if (data instanceof Boolean) {
            writeByte((byte) 7);
            writeBoolean((boolean) data);
        } else if (data instanceof Double) {
            writeByte((byte) 8);
            writeDouble((double) data);
        } else if (data instanceof byte[]) {
            writeByte((byte) 10);
            writeBytes((byte[]) data);
        } else if (data instanceof IpcData) {
            writeByte((byte) 11);
            writeString(data.getClass().getName());
            writeData((IpcData) data);
        } else if (data instanceof Object[]) {
            writeByte((byte) 12);
            Object[] arr = (Object[]) data;
            writeString(arr.getClass().getName());
            writeInt(arr.length);
            for (int i = 0; i < arr.length; i++) {
                write(arr[i]);
            }
        } else {
            writeByte((byte) 13);
            writeString(data.getClass().getName());
            writeString(JSON.toJSONString(data));
        }
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    public DataOutput(OutputStream outputStream) {
        this.outputStream = new DataOutputStream(outputStream);
    }

    private DataOutputStream outputStream;

    @Override
    public void writeShort(short b) throws IOException {
        outputStream.writeShort(b);
    }

    @Override
    public void writeChar(char ch) throws IOException {
        outputStream.writeChar(ch);
    }

    @Override
    public void writeByte(byte b) throws IOException {
        outputStream.writeByte(b);
    }

    @Override
    public void writeInt(int b) throws IOException {
        outputStream.writeInt(b);
    }

    @Override
    public void writeLong(long l) throws IOException {
        outputStream.writeLong(l);
    }

    @Override
    public void writeFloat(float f) throws IOException {
        outputStream.writeFloat(f);
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        outputStream.writeBoolean(b);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        outputStream.writeDouble(d);
    }

    @Override
    public void writeString(String s) throws IOException {
        byte[] bytes = s.getBytes("utf-8");
        writeBytes(bytes);
    }

    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        writeInt(bytes.length);
        outputStream.write(bytes);
    }

    @Override
    public void writeData(IpcData ipcData) throws IOException {
        ipcData.write(this);
    }


}