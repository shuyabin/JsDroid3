package com.jsdroid.sdk.logs;

public class Logs {
    private static class Single {
        public static Logs https = new Logs();
    }

    public static Logs getInstance() {


        return Single.https;
    }

    private Logs() {

    }
}
