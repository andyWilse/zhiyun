package com.religion.zhiyun.utils.map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GetLngAndLagGaoDe {

    public static final String KEY = "8b2f90754bd887f21f58e3d562c0d3cc";
    public static final String URL = "https://restapi.amap.com/v3/geocode/geo?address=";

    public static String getLngAndLag(String address) throws UnsupportedEncodingException {
        address = address.trim();
        String url = URL + URLEncoder.encode(address, "utf-8") + "&output=JSON" + "&key="+ KEY;

        try {
            URL url2 = new URL(url);    // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();// 打开连接
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
            System.out.println("sb=="+sb.toString());
            JSONObject a = JSON.parseObject(sb.toString());
            JSONArray sddressArr = JSON.parseArray(a.get("geocodes").toString());
            JSONObject c = JSON.parseObject(sddressArr.get(0).toString());
            String location = c.get("location").toString();
            return location;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
        return null;
    }



    public static void main(String[] args) {
        try {
            getLngAndLag("江西省南昌市青山湖区");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
