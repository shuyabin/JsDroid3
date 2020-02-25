package com.jsdroid.runner;

import com.jsdroid.api.IJsDroidService;
import com.jsdroid.api.IJsDroidShell;
import com.jsdroid.ipc.Ipc;

public class JsDroidConnector implements Ipc.ClientListener {
    private static class Single {
        static JsDroidConnector instance = new JsDroidConnector();
    }

    public static JsDroidConnector getInstance() {
        return Single.instance;
    }

    private Ipc.Client client;
    private IJsDroidService jsDroidService;
    private IJsDroidShell jsDroidShell;

    private JsDroidConnector() {

    }

    public synchronized void connect() {
        if (this.client == null) {
            this.client = Ipc.connectServer("127.0.0.1", Config.JSDROID_SERVER_PORT, this);
        }
    }

    public void reconnect() {
        if (this.client != null) {
            this.client.reconnect();
        }
    }

    public synchronized void disconnect() {
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }
    }

    @Override
    public void onConnected(final Ipc.Client client) {
        client.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    jsDroidService = client.getService(IJsDroidService.SERVICE_NAME, IJsDroidService.class);
                    jsDroidShell = jsDroidService.getShell(JsDroidAppImpl.getInstance());
                    JsDroidListener.getInstance().performConnected();
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                    reconnect();
                }
            }
        });

    }

    @Override
    public void onConnectErr(Ipc.Client client, Throwable err) {

    }

    @Override
    public void onDisconnected(Ipc.Client client) {
        JsDroidListener.getInstance().performDisconnected();
    }

    public IJsDroidService getJsDroidService() {
        return jsDroidService;
    }

    public IJsDroidShell getJsDroidShell() {
        return jsDroidShell;
    }
}
