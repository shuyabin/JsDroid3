package com.jsdroid.runner;


import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 一个简单的运行adb命令的工具类
 */
public class SimpleAdb {

    private static final String TAG = "SimpleAdb";
    public static String DEFAULT_PUB =
            "QAAAAMMJ5XgV0epk7GiyBoZJHF71aH72cvIOUkXF2gNjEbk4g0XUnpGXG1SC8w/TkQXwL4PDxvDixR/jptqela6ZEk176Ikq0ligvveyKQyXuX9HxwEz4K+94CEKoc21y8zdGmpOY4TQRajXPXooteI6PAjS5DgEHVzQvTUfuBJJ7Ug74hBFijXtC3OKPx37sSQtK16UD462xzeHQV/jk5n1M4cWsNiR+rF0p4hQmgp6+ofENMpYezc4fSQEl1N461v1rXRFLRvXgyKECnirtXhz/uKjvHUkpVyDE7Q/axb+JH25n1NZYxfQvpEs3M+6+gf4IhcDXcHaToabG0lYewZqNklUFuLORG7KJgO37ylTcPcWRakDlPtVVkqnqo8zZqo8pOhRzVmnB1Ulpj4xFUIuMgj+K+OCKeVfKJapXm1oHxLhJgEKxjGE6uUl4m7v5MUNFjo1KEoHHi2rAOQ5/kJCR5dvsjUwOv/fu96asFow6aJ6mJ7tkcCkBBcAM77qPxCH2BYZQ/85+LB8DjSKYiaP+AsH7UrCJQbKDNLOLepRWpa8kzsGSCL5GO6H0xf9DglzKT+S/JfVkB0+avWqDWZCB69FkT5D0ntqt22aJ1zv8Yp64oaGlBSBaskvpne3xXJpFr3cor8gQLPKj5/oIqKr7BHqOT9hUkvLflr6K8Le+9v7KQ5YbwEAAQA= jsdroid";
    public static String DEFAULT_MODULUS = "00:ce:e2:16:54:49:36:6a:06:7b:58:49:1b:9b:86:" +
            "4e:da:c1:5d:03:17:22:f8:07:fa:ba:cf:dc:2c:91:" +
            "be:d0:17:63:59:53:9f:b9:7d:24:fe:16:6b:3f:b4:" +
            "13:83:5c:a5:24:75:bc:a3:e2:fe:73:78:b5:ab:78:" +
            "0a:84:22:83:d7:1b:2d:45:74:ad:f5:5b:eb:78:53:" +
            "97:04:24:7d:38:37:7b:58:ca:34:c4:87:fa:7a:0a:" +
            "9a:50:88:a7:74:b1:fa:91:d8:b0:16:87:33:f5:99:" +
            "93:e3:5f:41:87:37:c7:b6:8e:0f:94:5e:2b:2d:24:" +
            "b1:fb:1d:3f:8a:73:0b:ed:35:8a:45:10:e2:3b:48:" +
            "ed:49:12:b8:1f:35:bd:d0:5c:1d:04:38:e4:d2:08:" +
            "3c:3a:e2:b5:28:7a:3d:d7:a8:45:d0:84:63:4e:6a:" +
            "1a:dd:cc:cb:b5:cd:a1:0a:21:e0:bd:af:e0:33:01:" +
            "c7:47:7f:b9:97:0c:29:b2:f7:be:a0:58:d2:2a:89:" +
            "e8:7b:4d:12:99:ae:95:9e:da:a6:e3:1f:c5:e2:f0:" +
            "c6:c3:83:2f:f0:05:91:d3:0f:f3:82:54:1b:97:91:" +
            "9e:d4:45:83:38:b9:11:63:03:da:c5:45:52:0e:f2:" +
            "72:f6:7e:68:f5:5e:1c:49:86:06:b2:68:ec:64:ea:" +
            "d1:15";
    public static String DEFAULT_PRIVATE_EXPONENT = "00:87:32:15:42:96:ac:cf:d6:ec:89:ed:f6:dc:a5:" +
            "e2:a7:ef:34:df:1b:c5:5b:74:0a:02:ff:24:80:13:" +
            "04:47:d5:8c:85:9f:0e:d3:97:28:e1:28:a2:84:e1:" +
            "ae:de:c6:77:f6:8f:fa:48:1f:37:a9:49:d5:fa:55:" +
            "15:15:db:be:29:9f:c1:aa:4a:b2:c0:d9:10:ea:e8:" +
            "6f:7c:3f:b9:4f:20:95:a3:81:f5:05:79:bf:d2:f5:" +
            "83:91:29:02:45:97:95:16:23:16:35:74:19:c0:77:" +
            "73:a7:bf:c0:de:73:be:09:88:c0:f7:17:35:ce:8a:" +
            "78:0a:bf:52:58:39:10:ba:ec:48:f3:84:b4:7d:9f:" +
            "dd:66:74:ab:23:c4:0a:46:ab:b1:0f:3b:5d:10:f5:" +
            "f4:fb:d8:b6:7d:90:9b:3d:c4:d3:8c:41:08:a0:b0:" +
            "5a:89:7a:b1:5f:65:d1:68:77:84:f0:2c:cf:4e:60:" +
            "c5:3a:7c:e6:6f:5f:fe:ff:37:5f:c8:bf:4f:03:39:" +
            "d7:54:5f:00:c6:33:b7:05:01:8c:13:ac:94:c6:7c:" +
            "5e:98:cb:cd:20:4e:d2:2a:2b:5d:48:44:e6:e2:c2:" +
            "77:95:64:61:88:b6:4b:31:18:c1:75:5d:b1:be:58:" +
            "64:d1:84:21:de:b8:be:4e:59:4b:21:0b:b5:80:4c:" +
            "09";
    private static final int A_SYNC = 0x434e5953;
    private static final int A_CNXN = 0x4e584e43;
    private static final int A_OPEN = 0x4e45504f;
    private static final int A_OKAY = 0x59414b4f;
    private static final int A_CLSE = 0x45534c43;
    private static final int A_WRTE = 0x45545257;
    private static final int A_AUTH = 0x48545541;

    private static final int A_VERSION = 0x01000000;
    private static final int MAX_PAYLOAD = 4096;

    private static final int TRIED_NONE = 0;
    private static final int TRIED_SIGNED = 1;
    private static final int TRIED_PUBKEY = 2;

    private static final int ADB_AUTH_SIGNATURE = 2;
    private static final int ADB_AUTH_PUBLICKEY = 3;

    private static final int HEAD_LENGTH = 24;

    private static final byte[] EMP = {0x00, 0x01,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            0x00,
            0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e, 0x03, 0x02, 0x1a, 0x05, 0x00, 0x04, 0x14};

    private final BigInteger d;

    private final BigInteger m;

    private final byte[] k;

    private int auth = TRIED_NONE;

    public SimpleAdb() {
        this(DEFAULT_PUB, DEFAULT_MODULUS, DEFAULT_PRIVATE_EXPONENT);
    }


    /**
     * 1. generate adbkey
     * shell> adb keygen adbkey
     * 2. for pub (base64 + "\x00 " + host, base64 is enough)
     * shell> cat adbkey.pub
     * 3. for privateExponent and modulus
     * shell> openssl rsa -in adbkey -text -noout
     *
     * @param pub             pubkey, encode as base64
     * @param modulus         modulus, part of rsa key
     * @param privateExponent private exponent, rsa private key
     */
    public SimpleAdb(String pub, String modulus, String privateExponent) {
        this.k = formatBytes(pub);
        this.m = convert(modulus);
        this.d = convert(privateExponent);
    }

    /**
     * @param k pubkey
     * @param m modulus, part of rsa key
     * @param d private exponent, rsa private key
     */
    public SimpleAdb(byte[] k, byte[] m, byte[] d) {
        this.k = formatBytes(Base64.encode(k, Base64.NO_WRAP));
        this.m = new BigInteger(1, m);
        this.d = new BigInteger(1, d);
    }

    private static BigInteger convert(String s) {
        return new BigInteger(s.replaceAll("[ :\n]", ""), 0x10);
    }

    private static void send(OutputStream os, Message message) throws IOException {
        int sum = 0;
        if (message.data == null) {
            message.data = new byte[0];
        }
        for (byte b : message.data) {
            sum += (b & 0xff);
        }
        int length = message.data.length;
        ByteBuffer buffer = ByteBuffer.allocate(HEAD_LENGTH + length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(message.command);
        buffer.putInt(message.arg0);
        buffer.putInt(message.arg1);
        buffer.putInt(length);
        buffer.putInt(sum);
        buffer.putInt(~message.command);
        i("send, command: " + command(message.command) + ", length: " + length);
        if (length > 0) {
            buffer.put(message.data);
        }
        os.write(buffer.array());
        os.flush();
    }

    private static Message recv(InputStream is) throws IOException {
        byte[] head = new byte[HEAD_LENGTH];
        if (is.read(head) != head.length) {
            e("recv head, length too short");
            throw new AdbException();
        }
        i("recv, head: " + Arrays.toString(head));
        ByteBuffer buffer = ByteBuffer.wrap(head);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        Message message = new Message();
        message.command = buffer.getInt();
        message.arg0 = buffer.getInt();
        message.arg1 = buffer.getInt();
        message.length = buffer.getInt();
        message.check = buffer.getInt();
        message.magic = buffer.getInt();
        i("recv, command: " + command(message.command)
                + ", arg0: " + message.arg0 + ", arg1: " + message.arg1);
        if (message.length > 0) {
            byte[] body = new byte[message.length];
            if (is.read(body) != body.length) {
                e("recv data, length too short");
                throw new AdbException();
            }
            message.data = body;
            if (message.command == A_AUTH) {
                i("recv, data: " + Arrays.toString(body));
            } else {
                i("recv: data: " + new String(body, "UTF-8"));
            }
        }
        return message;
    }

    private static byte[] formatBytes(byte[] bytes) {
        return Arrays.copyOf(bytes, bytes.length + 1);
    }

    private static byte[] formatBytes(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return Arrays.copyOf(bytes, bytes.length + 1);
    }

    /**
     * 通过tcp端口连接adbd执行adb命令。
     * 注意：执行前需要用户同意usb调试授权，如果用户拒绝授权，将会卡住一直等待同意授权消息。
     *
     * @param port
     * @param command
     * @return
     * @throws IOException
     */
    public String exec(int port, String command) throws IOException {
        InetAddress localhost = InetAddress.getByAddress("localhost", new byte[]{127, 0, 0, 1});
        try (
                Socket socket = new Socket(localhost, port);
                OutputStream os = new BufferedOutputStream(socket.getOutputStream());
                InputStream is = new BufferedInputStream(socket.getInputStream())
        ) {
            return comm(os, is, command);
        }
    }

    private String comm(OutputStream os, InputStream is, String command) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Message message;
        //告诉adbd，本主机为jsdroid
        send(os, new Message(A_CNXN, A_VERSION, MAX_PAYLOAD, formatBytes("host::jsdroid")));
        message = recv(is);
        while (message.command != A_CNXN) {
            if (message.command != A_AUTH) {
                return null;
            } else if (auth == TRIED_NONE) {
                send(os, new Message(A_AUTH, ADB_AUTH_SIGNATURE, 0, auth(message.data)));
                message = recv(is);
                this.auth = TRIED_SIGNED;
            } else if (auth == TRIED_SIGNED) {
                //发送公钥public key
                send(os, new Message(A_AUTH, ADB_AUTH_PUBLICKEY, 0, this.k));
                message = recv(is);
                auth = TRIED_PUBKEY;
            } else if (auth == TRIED_PUBKEY) {
                return null;
            }
        }
        //发送shell指令
        send(os, new Message(A_OPEN, 1, 0, formatBytes("shell:" + command)));
        message = recv(is);
        if (message.command == A_OKAY) {
            try {
                readResult(os, is, baos);
            } catch (AdbException e) {
                // do nothing
            }
        } else if (message.command == A_CLSE) {
            send(os, new Message(A_CLSE, 1, message.arg0, new byte[0]));
        }
        return baos.toString("UTF-8");
    }

    private void readResult(OutputStream os, InputStream is, OutputStream bs) throws IOException {
        while (true) {
            Message message = recv(is);
            if (message.command == A_CLSE) {
                send(os, new Message(A_CLSE, 1, message.arg0, new byte[0]));
                break;
            } else if (message.command == A_WRTE) {
                if (message.length > 0) {
                    bs.write(message.data);
                }
                send(os, new Message(A_OKAY, 1, message.arg0, new byte[0]));
            }
        }
    }

    private byte[] auth(byte[] token) {
        byte[] bytes = Arrays.copyOf(EMP, 0x100);
        System.arraycopy(token, 0, bytes, 236, 20);
        BigInteger a = new BigInteger(bytes);
        BigInteger b = a.modPow(this.d, this.m);
        byte[] c = b.toByteArray();
        if (c.length > bytes.length) {
            int offset = c.length - bytes.length;
            System.arraycopy(c, offset, bytes, 0, bytes.length);
        } else {
            int offset = bytes.length - c.length;
            System.arraycopy(c, 0, bytes, offset, c.length);
        }
        return bytes;
    }

    private static String command(int command) {
        switch (command) {
            case A_SYNC:
                return "SYNC";
            case A_CNXN:
                return "CNXN";
            case A_OPEN:
                return "OPEN";
            case A_OKAY:
                return "OKAY";
            case A_CLSE:
                return "CLSE";
            case A_WRTE:
                return "WRTE";
            case A_AUTH:
                return "AUTH";
            default:
                return "XXXX";
        }
    }

    private static void i(String msg) {
        Log.i(TAG, msg);
    }

    private static void e(String msg) {
        Log.e(TAG, msg);
    }

    private static class Message {
        int command;
        int arg0;
        int arg1;
        int length;
        int check;
        int magic;
        byte[] data;

        Message() {

        }

        Message(int command, int arg0, int arg1, byte[] data) {
            this.command = command;
            this.arg0 = arg0;
            this.arg1 = arg1;
            this.data = data;
        }
    }

    static class AdbException extends IOException {
    }

    public static void main(String[] args) {
        System.out.println(new String(EMP));
    }

}