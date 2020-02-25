package com.jsdroid.server;

import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.IJsDroidService;
import com.jsdroid.api.IJsDroidShell;
import com.jsdroid.ipc.call.ServiceProxy;
import com.jsdroid.sdk.apps.Apps;

import java.util.HashMap;
import java.util.Map;

public class JsDroidService implements IJsDroidService {

    private Map<String, JsDroidShell> appMap = new HashMap<>();

    @Override
    public IJsDroidShell getShell(IJsDroidApp app) throws InterruptedException {
        try {
            String pkg = app.getPackage();
            Apps.putApp(pkg, app);
            if (appMap.containsKey(pkg)) {
                JsDroidShell jsDroidShell = appMap.get(pkg);
                jsDroidShell.setApp(app);
                return jsDroidShell;
            } else {
                JsDroidShell jsDroidShell = createJsDroidShell(app);
                appMap.put(pkg, jsDroidShell);
                return jsDroidShell;
            }
        } catch (Exception e) {
            throw new InterruptedException();
        }

    }

    private JsDroidShell createJsDroidShell(IJsDroidApp app) throws InterruptedException {
        return new JsDroidShell(this, app);
    }

    @Override
    public void ping() throws InterruptedException {

    }

    @Override
    public void onAddService(String serviceId, ServiceProxy serviceProxy) {
        //在这里初始化系统服务
    }
}
