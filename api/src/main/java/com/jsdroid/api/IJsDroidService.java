package com.jsdroid.api;

import com.jsdroid.ipc.annotations.Timeout;
import com.jsdroid.ipc.data.IpcService;

public interface IJsDroidService extends IpcService {
    String SERVICE_NAME = "jsdroid_service";

    @Timeout(3000)
    IJsDroidShell getShell(IJsDroidApp app) throws InterruptedException;

    @Timeout(1000)
    void ping() throws InterruptedException;
}
