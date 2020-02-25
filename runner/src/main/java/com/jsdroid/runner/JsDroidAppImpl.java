package com.jsdroid.runner;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.ipc.call.ServiceProxy;
import com.jsdroid.ipc.data.IpcService;

import java.util.HashMap;
import java.util.Map;

public class JsDroidAppImpl implements IJsDroidApp {

    private static class Single {
        static JsDroidAppImpl instance = new JsDroidAppImpl();
    }

    public static JsDroidAppImpl getInstance() {
        return Single.instance;
    }

    private final Object serviceLock = new Object();
    private JsDroidApplication application;
    private ServiceProxy serviceProxy;
    private Handler toastHandler;
    private Toast toast;
    private final Map<String, IpcService> serviceMap = new HashMap<>();


    private JsDroidAppImpl() {
        toastHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void toast(final String text) {
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(application, text, Toast.LENGTH_LONG);
                toast.show();

            }
        });
        JsDroidListener.getInstance().performToast(text);
    }

    @Override
    public String getPackage() {
        return application.getPackageName();
    }

    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }


    @Override
    public void print(String text) {
        JsDroidListener.getInstance().performPrint(text);
    }

    @Override
    public void onScriptStart() throws InterruptedException {
        JsDroidListener.getInstance().performScriptStart();
    }

    @Override
    public void onScriptStop(String result) {
        JsDroidListener.getInstance().performStop(result);
    }

    @Override
    public void onVolumeDown(boolean scriptRunning) {
        JsDroidListener.getInstance().performVolumeDown(scriptRunning);
    }

    @Override
    public IInput getInput() throws InterruptedException {
        return Input.getInstance();
    }

    @Override
    public void loadScript(String file) throws InterruptedException {
        JsDroidListener.getInstance().performScriptLoad(file);
    }

    @Override
    public String readConfig(String key, String defaultValue) throws InterruptedException {
        return application.readConfig(key, defaultValue);
    }


    @Override
    public void onAddService(String serviceId, ServiceProxy serviceProxy) {
        synchronized (serviceLock) {
            this.serviceProxy = serviceProxy;
            for (String key : serviceMap.keySet()) {
                serviceProxy.addService(key, serviceMap.get(key));
            }
        }
    }

    public void setApplication(JsDroidApplication application) {
        this.application = application;
    }

    public void addService(String name, IpcService service) {
        synchronized (serviceLock) {
            serviceMap.put(name, service);
            if (serviceProxy != null) {
                serviceProxy.addService(name, service);
            }
        }
    }

}
