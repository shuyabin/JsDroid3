package com.jsdroid.ipc.call;

import com.jsdroid.ipc.annotations.Timeout;
import com.jsdroid.ipc.data.IpcService;
import com.jsdroid.ipc.data.DataInput;
import com.jsdroid.ipc.data.DataOutput;
import com.jsdroid.ipc.data.Input;
import com.jsdroid.ipc.data.IpcServiceManager;
import com.jsdroid.ipc.data.Output;
import com.jsdroid.ipc.service.IHeart;
import com.jsdroid.ipc.service.ISocket;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;


/**
 * 代理调用
 * Interface in = Proxy.createProxy
 * in.method();
 */
public class ServiceProxy {
    private IpcServiceManager serviceManager;
    //远程调用的socket
    private ISocket socket;
    //回调缓存
    private Map<Long, Object> resultMap;
    private Object resultLock;
    private ExecutorService threadPool;
    private Input input;
    private Output output;

    public ServiceProxy(ExecutorService threadPool, final ISocket socket) {
        this.serviceManager = new IpcServiceManager();
        this.threadPool = threadPool;
        this.socket = socket;
        this.resultMap = new HashMap<>();
        this.resultLock = new Object();
        addService("heart", new IHeart() {
            @Override
            public void onAddService(String serviceId, ServiceProxy serviceProxy) {

            }


            @Timeout(1000)
            @Override
            public boolean heart() {
                return true;
            }

            @Timeout(3000)
            @Override
            public boolean heart3000() {
                return true;
            }
        });
    }

    public void prepare() {
        try {
            output = new DataOutput(socket.getOutPutStream());
            input = new DataInput(socket.getInputStream());
        } catch (Exception e) {
        }
    }

    public void loop() {
        //读写
        try {

            while (true) {
                final Message message = input.read();
                switch (message.type) {
                    case Message.CALL:
                        //被调用
                        final CallData callData = (CallData) message.ipcData;
                        threadPool.execute(new Runnable() {
                            @Override
                            public void run() {
                                Object ret = null;
                                try {
                                    ret = call(callData);
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                                Result result = new Result();
                                result.id = callData.id;
                                result.data = ret;
                                message.type = Message.RESULT;
                                message.ipcData = null;
                                message.ipcData = result;
                                //发送结果
                                sendMessage(message);
                            }
                        });

                        break;
                    case Message.RESULT:
                        Result result = (Result) message.ipcData;
                        onResultMessage(result.id, result.data);
                        break;
                }
            }
        } catch (Exception e) {
        } finally {
            close();
        }
    }


    protected Object invoke(String name, Method method, Object args[]) throws InterruptedException {
        CallData callData = createCallInfo(name, method, args);
        Message message = new Message();
        message.type = Message.CALL;
        message.ipcData = callData;
        //发送callinfo
        sendMessage(message);
        int timeout = 10000;
        Timeout annotation = method.getAnnotation(Timeout.class);
        if (annotation != null) {
            timeout = annotation.value();
        }
        Object result = waitResult(callData.id, timeout);
        if (result == null) {
            return null;
        }
        //如果返回结果类型是IpcCall，那么需要createCall
        if (IpcService.class.isAssignableFrom(method.getReturnType())) {
            return getService(result.toString(), method.getReturnType());
        }
        return result;
    }

    //收到call命令，需要返回结果
    protected Object call(CallData callData) throws Exception {
        Object serviceImpl = serviceManager.getService(callData.name);
        Object result;
        //反射调用
        if (callData.paramTypes != null && callData.paramTypes.length > 0) {
            Class[] types = ReflectUtil.getTypes(callData.paramTypes);
            for (int i = 0; i < types.length; i++) {
                if (IpcService.class.isAssignableFrom(types[i])) {
                    //传过来一个hashcode，调用的时候，创建一个CallInfo即可调用
                    if (callData.params[i] != null) {
                        String serviceName = (String) callData.params[i];
                        Object service = getService(serviceName, types[i]);
                        callData.params[i] = service;
                    }
                }
            }
            Method method = ReflectUtil.getMethod(serviceImpl.getClass(), callData.methodName, types);
            method.setAccessible(true);

            Object[] params = callData.params;
            result = method.invoke(serviceImpl, params);
        } else {
            Method method = ReflectUtil.getMethod(serviceImpl.getClass(), callData.methodName);
            method.setAccessible(true);
            result = method.invoke(serviceImpl);
        }
        //如果运行结果是一个service
        if (result instanceof IpcService) {
            String serviceId = addService(result);
            return serviceId;
        }
        return result;
    }

    public <T> T getService(String serviceName, Class<T> serviceClass) {
        ClassLoader classLoader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        return (T) Proxy.newProxyInstance(classLoader, interfaces, new CallInvocationHandler(serviceName));
    }

    private class CallInvocationHandler implements InvocationHandler {
        private String name;

        public CallInvocationHandler(String name) {
            this.name = name;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return ServiceProxy.this.invoke(name, method, args);
        }
    }

    public String addService(Object service) {
        return addService(Integer.toHexString(service.hashCode()), service);
    }

    public String addService(String id, Object service) {
        if (service instanceof IpcService) {
            ((IpcService) service).onAddService(id, this);
        }
        return serviceManager.addService(id, service);
    }

    public void removeService(String id) {
        serviceManager.removeService(id);
    }

    public Object waitResult(long id, long timeout) throws InterruptedException {
        //最多等10秒
        long endtime = System.currentTimeMillis() + timeout;

        while (true) {
            synchronized (resultLock) {
                resultLock.wait(1000);
            }
            if (socket.isClosed()) {
                throw new InterruptedException("disconnect");
            }
            if (resultMap.containsKey(id)) {
                return resultMap.remove(id);
            } else if (System.currentTimeMillis() > endtime) {
                throw new InterruptedException("timeout:" + timeout);
            }
        }

    }


    //收到运行结果
    private void onResultMessage(long id, Object result) {
        resultMap.put(id, result);
        synchronized (resultLock) {
            resultLock.notifyAll();
        }
    }

    private Object sendLock = new Object();

    //发送数据
    private void sendMessage(final Message message) {
        execute(new Runnable() {
            @Override
            public void run() {
                synchronized (sendLock) {
                    try {
                        output.write(message);
                        output.flush();
                    } catch (Exception e) {
                        close();
                    }
                }
            }
        });


    }


    public CallData createCallInfo(String name, Method method, Object[] args) {
        CallData callData = new CallData();
        callData.id = callData.hashCode();
        callData.name = name;
        callData.params = args;
        callData.methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null) {
            callData.paramTypes = new String[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                callData.paramTypes[i] = parameterTypes[i].getName();
            }
        }
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof IpcService) {
                    String serviceId = addService(arg);
                    args[i] = serviceId;
                }
            }
        }

        return callData;
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
        if (input != null) {
            try {
                input.close();
            } catch (Exception e) {
            }
        }
        if (output != null) {
            try {
                output.close();
            } catch (Exception e) {
            }
        }
    }

    public void execute(Runnable runnable) {
        if (threadPool != null) {
            threadPool.execute(runnable);
        }
    }

}
