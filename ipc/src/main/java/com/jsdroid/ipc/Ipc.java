package com.jsdroid.ipc;

import com.jsdroid.ipc.call.ServiceProxy;
import com.jsdroid.ipc.service.IServer;
import com.jsdroid.ipc.service.ISocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Ipc {
    public interface ServerListener {
        void onStart(Server server);

        void onClose(Server server);

        void onOpenServerErr();
    }

    public interface ClientListener {
        void onConnected(Client client);

        void onConnectErr(Client client, Throwable err);

        void onDisconnected(Client client);
    }

    public static class Server implements Runnable {
        private ServerListener serverListener;
        private Object mLock = new Object();
        private Map<String, Object> serviceMap;
        private ExecutorService threadPool;
        private IServer server;
        private int port;

        public void execute(Runnable runnable) {
            threadPool.execute(runnable);
        }

        public Server(int port, ServerListener serverListener) {
            this.port = port;
            this.serverListener = serverListener;
            serviceMap = new HashMap<>();
            threadPool = Executors.newCachedThreadPool();
//            threadPool.execute(this);
            run();
        }

        @Override
        public void run() {
            try {
                server = new SocketServerImpl(port);
                if (serverListener != null) {
                    serverListener.onStart(this);
                }
                for (; ; ) {
                    final ISocket accept = server.accept();
                    final ServiceProxy serviceProxy = new ServiceProxy(threadPool, accept);
                    synchronized (mLock) {
                        for (String name : serviceMap.keySet()) {
                            serviceProxy.addService(name, serviceMap.get(name));
                        }
                    }
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            serviceProxy.prepare();
                            serviceProxy.loop();
                        }
                    });
                }
            } catch (Exception e) {
                Printer.print(e.getMessage());
                Printer.printError(e);
            } finally {
                shutdown();
                if (serverListener != null) {
                    serverListener.onClose(this);
                }
                if (server == null) {
                    if (serverListener != null) {
                        serverListener.onOpenServerErr();
                    }
                }
            }
        }

        public void addService(String name, Object impl) {
            synchronized (mLock) {
                serviceMap.put(name, impl);
            }
        }


        public void shutdown() {
            try {
                server.close();
            } catch (Exception e) {
            }
            threadPool.shutdown();
        }

    }

    public static class Client implements Runnable {
        private ExecutorService threadPool;
        private ServiceProxy serviceProxy;
        private ISocket socket;
        private ClientListener clientListener;
        private boolean close;


        public Client(ISocket socket, ClientListener clientListener) {
            this.socket = socket;
            this.clientListener = clientListener;
            threadPool = Executors.newCachedThreadPool();
            serviceProxy = new ServiceProxy(threadPool, socket);
            threadPool.execute(this);
        }

        public <T> T getService(String name, Class<T> serviceClass) {
            return serviceProxy.getService(name, serviceClass);
        }

        public void addService(String name, Object impl) {
            if (serviceProxy != null) {
                serviceProxy.addService(name, impl);
            }
        }

        public ServiceProxy getServiceProxy() {
            return serviceProxy;
        }

        public void execute(Runnable runnable) {
            threadPool.execute(runnable);
        }

        private void close(final Runnable runnable) {
            execute(new Runnable() {
                @Override
                public void run() {
                    serviceProxy.close();
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });

        }

        public void close() {
            close = true;
            close(new Runnable() {
                @Override
                public void run() {
                    threadPool.shutdown();
                }
            });
        }

        public void reconnect() {
            close(null);
        }

        public ISocket getSocket() {
            return socket;
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    socket.connect();
                    serviceProxy.prepare();
                    if (clientListener != null) {
                        clientListener.onConnected(Client.this);
                    }
                    serviceProxy.loop();
                } catch (Throwable e) {
                    if (clientListener != null) {
                        clientListener.onConnectErr(this, e);
                    }
                } finally {
                    if (clientListener != null) {
                        clientListener.onDisconnected(this);
                    }
                }
                if (close) {
                    break;
                }
                //等待2秒,启动服务需要点时间
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

        }
    }

    public static Client connectServer(String host, int port) {
        return connectServer(host, port, null);
    }

    static class SocketImpl implements ISocket {
        private Socket socket;
        private String host;
        private int port;

        public SocketImpl(String host, int port) {
            this.host = host;
            this.port = port;
        }


        @Override
        public void setAddress(String ip, int port) throws IOException {
            this.host = ip;
            this.port = port;
            close();
        }

        @Override
        public void connect() throws IOException {
            if (socket != null) {
                socket.close();
            }
            socket = new Socket(host, port);
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public boolean isClosed() {
            if (socket == null) {
                return true;
            }
            return socket.isClosed();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutPutStream() throws IOException {
            return socket.getOutputStream();
        }

    }


    public static Client connectServer(String host, int port, ClientListener clientListener) {
        return new Client(new SocketImpl(host, port), clientListener);
    }

    public static Server startServer(int port, ServerListener serverListener) {
        return new Server(port, serverListener);
    }

    static class SocketServerImpl implements IServer {
        ServerSocket serverSocket;

        public SocketServerImpl(int port) throws IOException {
            serverSocket = new ServerSocket(port);
        }

        @Override
        public ISocket accept() throws IOException {
            final Socket accept = serverSocket.accept();
            return new ISocket() {

                @Override
                public void setAddress(String ip, int port) throws IOException {

                }

                @Override
                public void connect() throws IOException {

                }

                @Override
                public void close() throws IOException {
                    accept.close();
                }

                @Override
                public boolean isClosed() {
                    return accept.isClosed();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return accept.getInputStream();
                }

                @Override
                public OutputStream getOutPutStream() throws IOException {
                    return accept.getOutputStream();
                }
            };
        }

        @Override
        public void close() throws IOException {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

//    public interface Hello {
//        void call(User user);
//    }
//
//    public interface User extends Call {
//        String getData();
//    }
//
//    public static void main(String[] args) {
//        Ipc.startServer(9800, new ServerListener() {
//            @Override
//            public void onStart(Server server) {
//                server.addService("call", new Hello() {
//                    @Override
//                    public void call(User user) {
//                        System.out.println("call," + user.getData());
//                    }
//                });
//            }
//
//            @Override
//            public void onClose(Server server) {
//
//            }
//        });
//        Ipc.connectServer("127.0.0.1", 9800, new ClientListener() {
//            @Override
//            public void onConnected(final Client client) {
//                client.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Hello call = client.getService("call", Hello.class);
//                        call.call(new User() {
//                            @Override
//                            public String getData() {
//                                return "wocao";
//                            }
//                        });
//                    }
//                });
//            }
//
//            @Override
//            public void onConnectErr(Client client, Throwable err) {
//
//            }
//
//            @Override
//            public void onDisconnected(Client client) {
//
//            }
//        });
//    }
}
