package com.jsdroid.ipc.data;

import java.util.HashMap;
import java.util.Map;

//该类存了大量的Service：虚拟接口，每个接口的实例都会有个缓存，每实例化一个接口，都会增加系统内存，Proxy断开后，由系统回收这些内存
public class IpcServiceManager {
    private Map<String, Object> serviceMap;

    public IpcServiceManager() {
        serviceMap = new HashMap<>();
    }

    public Object getService(String id) {
        return serviceMap.get(id);
    }

    public String addService(String id, Object service) {
        serviceMap.put(id, service);
        return id;
    }


    public void removeService(String id) {
        serviceMap.remove(id);
    }
}
