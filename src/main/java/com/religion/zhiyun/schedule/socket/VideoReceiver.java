package com.religion.zhiyun.schedule.socket;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;

@Service
public class VideoReceiver {
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream fileOutputStream;

    public void receiveVideo(String fileName) {
        try {
            inputStream = clientSocket.getInputStream();
            fileOutputStream = new FileOutputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("视频数据接收完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}