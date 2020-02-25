package com.jsdroid.sdk.shells;

public class Shells {
    private static class Single {
        static Shells https = new Shells();
    }

    public static Shells getInstance() {

        return Single.https;
    }

    private Shells() {

    }
}
