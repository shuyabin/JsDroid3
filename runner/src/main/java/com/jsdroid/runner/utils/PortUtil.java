package com.jsdroid.runner.utils;

import java.net.Socket;

public class PortUtil {
    public static boolean checkPort(int port) {
        try {
            new Socket("127.0.0.1", port).close();
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }


}
