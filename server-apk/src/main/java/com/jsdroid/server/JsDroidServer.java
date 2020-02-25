package com.jsdroid.server;

import android.util.Log;

import com.jsdroid.api.IJsDroidServer;
import com.jsdroid.api.IJsDroidService;
import com.jsdroid.app_hidden_api.InputUtil;
import com.jsdroid.ipc.Ipc;
import com.jsdroid.sdk.inputs.Inputs;
import com.jsdroid.sdk.system.Loop;

public class JsDroidServer implements IJsDroidServer {
    @Override
    public void onServerStart(final Ipc.Server server) {
        Log.d("JsDroid", "server start.");
        server.execute(new Runnable() {
            @Override
            public void run() {
                Loop.loop(new Runnable() {
                    @Override
                    public void run() {
                        Inputs.getInstance().closeInputMethod();
                    }
                });
            }
        });
        server.addService(IJsDroidService.SERVICE_NAME, new JsDroidService());
    }

}
