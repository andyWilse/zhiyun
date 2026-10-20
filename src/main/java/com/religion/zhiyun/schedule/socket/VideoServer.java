package com.religion.zhiyun.schedule.socket;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Service
public class VideoServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Socket服务器已启动，等待客户端连接...");
            clientSocket = serverSocket.accept();
            System.out.println("客户端已连接！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
            clientSocket.close();
            System.out.println("Socket服务器已停止！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
