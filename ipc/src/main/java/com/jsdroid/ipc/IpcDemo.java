package com.jsdroid.ipc;

import com.jsdroid.ipc.call.ServiceProxy;
import com.jsdroid.ipc.data.IpcService;

import java.util.Arrays;

public class IpcDemo {
    public static class MyData {
        String name;
        byte[] data;


//        @Override
//        public void write(Output output) throws IOException {
//            output.write(name);
//            output.write(data);
//        }
//
//        @Override
//        public void read(Input input) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
//            name = input.read();
//            data = input.read();
//        }
    }

    interface IServiceIpcService extends IpcService {
        String hello();
    }

    interface IService extends IpcService {
        MyData getData();

        void sendData(MyData data);

        IServiceIpcService call();

        void clientCall(IClientCall clientCall);

        void showData(MyData myData);

        void hello();

    }

    interface IClientCall {
        void hello();

        IClientCall get();

    }

    static void server() {
        Ipc.startServer(9999, new Ipc.ServerListener() {
            @Override
            public void onStart(Ipc.Server server) {
                server.addService("service", new IService() {
                    @Override
                    public void onAddService(String serviceId, ServiceProxy serviceProxy) {

                    }


                    @Override
                    public MyData getData() {
                        return new MyData();
                    }

                    @Override
                    public void sendData(MyData data) {
                        System.out.println("service receive data:" + data);
                    }

                    @Override
                    public IServiceIpcService call() {
                        return new IServiceIpcService() {
                            @Override
                            public void onAddService(String serviceId, ServiceProxy serviceProxy) {

                            }

                            @Override
                            public String hello() {
                                return "im service call";
                            }
                        };
                    }

                    @Override
                    public void clientCall(IClientCall clientCall) {
                        System.out.println("client call:");
                        clientCall.hello();
                        clientCall.get().get().get().get().get().hello();
                    }

                    @Override
                    public void showData(MyData myData) {
                        System.out.println(myData.name);
                        System.out.println(Arrays.toString(myData.data));
                    }

                    @Override
                    public void hello() {
                        System.out.println("im service");
                    }
                });
            }

            @Override
            public void onClose(Ipc.Server server) {

            }

            @Override
            public void onOpenServerErr() {

            }
        });
    }


    static void client() {
        final Ipc.Client client = Ipc.connectServer("127.0.0.1", 9999);
        final IService service = client.getService("service", IService.class);
        System.out.println(service.getData());
        service.sendData(new MyData());
        System.out.println(service.call().hello());

        service.clientCall(new IClientCall() {
            @Override
            public void hello() {
                System.out.println("im client:" + hashCode());
            }

            @Override
            public IClientCall get() {
                return this;
            }
        });
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                server();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                client();
            }
        }).start();


    }
}
