package com.jsdroid.ipc.data;

import com.alibaba.fastjson.JSON;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;


public class DataInput extends Input {
    DataInputStream dataInputStream;

    public DataInput(InputStream dataInputStream) {
        this.dataInputStream = new DataInputStream(dataInputStream);
    }

    @Override
    public byte readByte() throws IOException {
        return dataInputStream.readByte();
    }

    @Override
    public short readShort() throws IOException {
        return dataInputStream.readShort();
    }

    @Override
    public char readChar() throws IOException {
        return dataInputStream.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return dataInputStream.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return dataInputStream.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return dataInputStream.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return dataInputStream.readDouble();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return dataInputStream.readBoolean();
    }

    @Override
    public IpcData readData(Class tClass) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        IpcData ipcData = (IpcData) tClass.newInstance();
        ipcData.read(this);
        return ipcData;
    }

    @Override
    public String readString() throws IOException {
        byte[] bytes = readBytes();
        if (bytes == null) {
            return null;
        }
        return new String(bytes, "utf-8");
    }

    @Override
    public byte[] readBytes() throws IOException {
        int i = dataInputStream.readInt();
        byte[] bytes = new byte[i];
        dataInputStream.readFully(bytes);
        return bytes;
    }

    @Override
    public Object read() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        byte b = readByte();
        switch (b) {
            case 1:
                return readShort();
            case 2:
                return readChar();
            case 3:
                return readByte();
            case 4:
                return readInt();
            case 5:
                return readLong();
            case 6:
                return readFloat();
            case 7:
                return readBoolean();
            case 8:
                return readDouble();
            case 9:
                return readString();
            case 10:
                return readBytes();
            case 11: {
                String className = readString();
                return readData(Class.forName(className));
            }
            case 12: {
                String className = readString();
                Class<?> componentType = Class.forName(className).getComponentType();
                //Object[]
                int len = readInt();
                Object[] arr = (Object[]) Array.newInstance(componentType, len);
                for (int i = 0; i < len; i++) {
                    arr[i] = read();
                }
                return arr;
            }
            case 13: {
                //Object
                String className = readString();
                String json = readString();
                return JSON.parseObject(json, Class.forName(className));
            }
            case 0:
                //null

                break;
        }
        return null;
    }


    @Override
    public void close() throws IOException {
        dataInputStream.close();
    }
}