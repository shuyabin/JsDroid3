package com.jsdroid.sdk.https;

public class Https {
    private static class Single {
        static Https single = new Https();
    }

    public static Https getInstance() {

        return Single.single;
    }

    private Https() {

    }
}
