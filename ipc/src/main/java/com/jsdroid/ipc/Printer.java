package com.jsdroid.ipc;

public class Printer {

    public static void print(Object text) {
        printLog(text + "");
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        String at = stackTrace[1].getClassName() + "(" + stackTrace[1].getFileName() + ":" + stackTrace[1].getLineNumber() + ")";
        printLog("\n\tat:" + at);
    }

    public synchronized static void printError(Throwable err) {
        err.printStackTrace();
    }


    public synchronized static void printLog(String text) {
        System.out.println(text);
    }
}
