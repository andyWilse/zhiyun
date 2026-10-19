package com.religion.zhiyun.https;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapTest {
    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("a",1);
        map.put("d","sdsdf");

        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String name = entry.getKey();
            Object value = entry.getValue();
            String aa="";
            if(value instanceof String){
                aa=(String) value;
            }else if(value instanceof Integer){
                aa=String.valueOf(value);
            }


            System.out.println(aa);


            //query.append("&").append(name).append("=").append(URLEncoder.encode(value, charset));
        }
    }
}
