package com.religion.zhiyun.utils.sms.http;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HttpClientUtil {
    public static String doPost(String pathUrl, String data){
        OutputStreamWriter out = null;
        BufferedReader br = null;
        String result = "";
        try {
            URL url = new URL(pathUrl);
            //打开和url之间的连接
            log.info("打开和url之间的连接");
            System.out.println("打开和url之间的连接s");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //请求方法为"POST"
            conn.setRequestMethod("POST");
            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true, 默认情况下是false;
            conn.setDoOutput(true);
            // Post请求不能使用缓存
            conn.setUseCaches(false);
            //设置请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");  //维持长链接
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.connect();
            //调用，获取输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            //发送请求参数
            System.out.println("发送请求参数s"+data);
            log.info("发送请求参数"+data);
            out.write(data);
            out.flush();
            //获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            //构造一个字符流缓存
            br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            while ((str = br.readLine()) != null){
                result += str;
            }
            System.out.println("发送请求参数s"+result);
            log.info("发送请求参数"+result);
            //关闭流
            is.close();
            //断开连接，disconnect是在底层tcp socket链接空闲时才切断，如果正在被其他线程使用就不切断。
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
