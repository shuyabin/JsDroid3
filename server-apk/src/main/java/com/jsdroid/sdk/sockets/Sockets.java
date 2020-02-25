package com.jsdroid.sdk.sockets;

public class Sockets {
    private static class Single {
        static Sockets sockets = new Sockets();
    }

    public static Sockets getInstance() {

        return Single.sockets;
    }

    private Sockets() {

    }
}
