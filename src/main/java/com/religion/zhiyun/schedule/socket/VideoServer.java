package com.religion.zhiyun.schedule.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
@Service
public class VideoServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            log.info("Socket服务器已启动，等待客户端连接...");
            clientSocket = serverSocket.accept();
            log.info("客户端已连接！");
        } catch (IOException e) {
            log.info("端口"+port+"监听失败"+e.getMessage());
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
            clientSocket.close();
            log.info("Socket服务器监听已停止！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
