package com.religion.zhiyun.utils.map;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;

@Slf4j
public class GeocoderLatitudeUtil {
    public static final String KEY_1 = "7d9fbeb43e975cd1e9477a7e5d5e192a";
    public static final String KEY = "sMXMph31FrAxhzEKxGiNvbIG5Ich3GLj";

    public static String getCoordinate(String Stringunitaddr) throws UnsupportedEncodingException {

        System.out.println("要查询的地名："+Stringunitaddr);

        //去掉中文地址中的空白字符
        String unitaddr=Stringunitaddr.replaceAll("\\s*", "");
        //将地址转换成utf-8的16进制
        String address = URLEncoder.encode(unitaddr, "UTF-8");

        //String url = "http://api.map.baidu.com/geocoder/v2/?output=json&ak=xxxxxx&address=" + unitaddr;
        //String url ="http://api.map.baidu.com/geocoder?address="+ address +"&output=json&key="+ KEY_1;
        //String url = "https://api.map.baidu.com/geocoding/v3/?address="+ address +"&output=json&ak="+KEY+"&callback=showLocation";
        String url = "https://api.map.baidu.com/geocoding/v3/?address="+ address +"&output=json&ak="+KEY;

        String json = loadJSON(url);
        System.out.println("开始执行百度接口："+url);
        System.out.println("百度返回的json："+json);
        if (json != null && !"".equals(json)) {
            //先把String对象转换成json对象
            JSONObject obj= JSONObject.parseObject(json);
            if ("0".equals(obj .getString("status"))) {
                //查询成功情况
                System.out.println("成功返回的信息："+obj);
                double lng = obj .getJSONObject("result").getJSONObject("location").getDouble("lng"); // 经度
                double lat = obj .getJSONObject("result").getJSONObject("location").getDouble("lat"); // 纬度
                DecimalFormat df = new DecimalFormat("#.######");
                System.out.println(df.format(lng) + "," + df.format(lat));

                return df.format(lng)+","+df.format(lat);

            }else {
                //查询失败情况
                if ("1".equals(obj .getString("status"))) {
                    //未找到地理位置信息
                    System.out.println("未找到地理位置返回的信息："+obj);
                    return '1'+","+'1';
                }else if("302".equals(obj .getString("status"))){
                    System.out.println("配额超限返回的信息："+obj);
                    //每天配额超标的情况
                    return '0'+","+'0';
                }
            }
        }

        return '1'+","+'1';
    }

    /**
     * 地图调用
     * @param url
     * @return
     */
    public static String loadJSON(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {} catch (IOException e) {}
        return json.toString();
    }
}
