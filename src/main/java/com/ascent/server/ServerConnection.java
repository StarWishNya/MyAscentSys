package com.ascent.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.io.InputStream;


public class ServerConnection {
    private static final int port ;

    static {
        try (InputStream input = ServerConnection.class.getClassLoader().getResourceAsStream("serverconfig.properties")) {
            if (input == null) {
                throw new RuntimeException("配置文件 serverconfig.properties 未找到！");
            }
            Properties properties = new Properties();
            properties.load(input);

            port = Integer.parseInt(properties.getProperty("server.port"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("加载服务器配置失败：" + e.getMessage());
        }
    }

    /**
     * 启动服务器
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器启动，监听端口：" + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("接收到新连接：" + socket.getRemoteSocketAddress());

                //为每个连接创建一个线程
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("服务器启动失败：" + e.getMessage());
        }
    }

    /**
     * 关闭服务器
     */
    public void stop() {
        System.out.println("服务器关闭");
    }
}
