package com.religion.zhiyun.interfaces.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author lrx
 * @description: TODO  视频或者图片下载工具类
 * @date 2022/10/27 17:00
 */
public class VideoDownload {
    /**
     * 保存到本地的工具类
     * @param args
     */
    public static void main(String[] args) {
        //String txUrl = "URl地址";
        String txUrl ="https://sample-videos.com/video123/flv/720/big_buck_bunny_720p_1mb.flv";

        // 生成视频名称
        String spName = System.currentTimeMillis() + ".mp4";
        // 保存到本地的地址 /Users/tp/shipi/ 因为我是mac本所以和win系统不一样
        // 所以使用win系统的自行更换
        // String bdPath = "/Users/tp/shipi/"+spName;
        String bdPath = "f://home//2023"+spName; // 保存到服务器的地址
        boolean downVideo = downVideo(txUrl, bdPath);
    }

    /**
     * 保存到本地的工具类
     */
    public  void play() {
        //String txUrl = "URl地址";
       // String txUrl ="https://sample-videos.com/video123/flv/720/big_buck_bunny_720p_1mb.flv";
        String txUrl ="http://172.20.23.35:2080/rtp/330300-01676409017759457282.flv";
        // 生成视频名称
        String spName = System.currentTimeMillis() + ".mp4";
        // 保存到本地的地址 /Users/tp/shipi/ 因为我是mac本所以和win系统不一样
        // 所以使用win系统的自行更换
        // String bdPath = "/Users/tp/shipi/"+spName;
       // String bdPath = "f://home//2023"+spName; // 保存到服务器的地址
        String bdPath = "//home//aa"+spName;
        boolean downVideo = downVideo(txUrl, bdPath);
    }


    /**
     * 下载视频
     * @param videoUrl 视频网络地址
     * @param downloadPath  视频保存地址
     */
    public static boolean downVideo(String videoUrl, String downloadPath) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        boolean re;
        try {
            System.out.println("a001...");
            URL url = new URL(videoUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=0-");
            connection.connect();
            System.out.println("a002...");
            if (connection.getResponseCode() / 100 != 2) {
                System.out.println("连接失败...");
                return false;
            }
            inputStream = connection.getInputStream();
            int downloaded = 0;
            int fileSize = connection.getContentLength();
            randomAccessFile = new RandomAccessFile(downloadPath, "rw");
            while (downloaded < fileSize) {
                byte[] buffer = null;
                if (fileSize - downloaded >= 1000000) {
                    buffer = new byte[1000000];
                } else {
                    buffer = new byte[fileSize - downloaded];
                }
                int read = -1;
                int currentDownload = 0;
                long startTime = System.currentTimeMillis();
                while (currentDownload < buffer.length) {
                    read = inputStream.read();
                    buffer[currentDownload++] = (byte) read;
                }
                long endTime = System.currentTimeMillis();
                double speed = 0.0;
                if (endTime - startTime > 0) {
                    speed = currentDownload / 1024.0 / ((double) (endTime - startTime) / 1000);
                }
                System.out.println("a003...");
                randomAccessFile.write(buffer);
                downloaded += currentDownload;
                System.out.println("a004...");
                randomAccessFile.seek(downloaded);
                System.out.printf(downloadPath+"下载了进度:%.2f%%,下载速度：%.1fkb/s(%.1fM/s)%n", downloaded * 1.0 / fileSize * 10000 / 100,
                        speed, speed / 1000);
            }
            re = true;
            return re;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            re = false;
            return re;
        } catch (IOException e) {
            e.printStackTrace();
            re = false;
            return re;
        } finally {
            try {
                connection.disconnect();
                inputStream.close();
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
