package com.jsdroid.ipc.data;

import com.jsdroid.ipc.call.ServiceProxy;

public interface IpcService {
    void onAddService(String serviceId, ServiceProxy serviceProxy);
}
