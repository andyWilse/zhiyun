package com.religion.zhiyun.test202302;

import com.religion.zhiyun.utils.sms.SendMassage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;

@SpringBootTest
public class sendMSM {
    @Test
    void contextLoads() throws UnsupportedEncodingException {
        SendMassage a=new SendMassage();
        String contents="【云监控中心】尊敬的消防智慧云用户，请及时妥善处理";
        a.sendSms(contents,"18514260203");

        //String md5Password = DigestUtils.md5DigestAsHex("1234562013-01-01 12:00:00".getBytes());
        //System.out.println(md5Password);

       /* String msg="【瓯海公司】我爱你";
        String encode = URLEncoder.encode("【瓯海公司】我爱你", "GB2312");
        //先把字符串按gb2312转成byte数组
        byte[] bytes = msg.getBytes("gb2312");
        StringBuilder gbString = new StringBuilder();

        for (byte b : bytes)
        {
            // 再用Integer中的方法，把每个byte转换成16进制输出
            String temp = Integer.toHexString(b);
            //判断进行截取
            if(temp.length()>=8){
                temp = temp.substring(6, 8);
            }
            gbString.append("%" + temp);
        }
        System.out.println(encode);
        System.out.println(gbString.toString());*/

/*String url="%e7%ad%be%e5%90%8d%e4%b8%8d%e6%ad%a3%e7%a1%ae%ef%bc%8c%e8%af%b7%e4%bf%ae%e6%94%b9%e5%90%8e%e5%8f%91%e9%80%81%ef%bc%81%e7%ad%be%e5%90%8d%e6%a0%bc%e5%bc%8f%ef%bc%9a%e4%be%8b%e5%a6%82%e3%80%90%e6%9f%90%e6%9f%90%e5%85%ac%e5%8f%b8%e3%80%91%ef%bc%8c%e5%ad%97%e6%95%b03-8%e4%b8%aa%e5%ad%97%ef%bc%8c%e6%9c%89%e9%97%ae%e9%a2%98%e8%af%b7%e8%81%94%e7%b3%bb%e5%ae%a2%e6%9c%8d%e4%b8%ad%e5%bf%83%e3%80%82";
url="%c7%a9%c3%fb%b2%bb%d5%fd%c8%b7%a3%ac%c7%eb%d0%de%b8%c4%ba%f3%b7%a2%cb%cd%a3%a1%c7%a9%c3%fb%b8%f1%ca%bd%a3%ba%c0%fd%c8%e7%a1%be%c4%b3%c4%b3%b9%ab%cb%be%a1%bf%a3%ac%d7%d6%ca%fd3-8%b8%f6%d7%d6%a3%ac%d3%d0%ce%ca%cc%e2%c7%eb%c1%aa%cf%b5%bf%cd%b7%fe%d6%d0%d0%c4%a1%a3";
String decode = URLDecoder.decode(url, "UTF-8");
        System.out.println(decode);*/

       /* Date date = new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format1 = format.format(date);
        System.out.println(format1);*/

        /*String response ="result=0&balance=96&linkid=2FB29BED699045F6BA7FC1579EBE037B&description=%e5%8f%91%e9%80%81%e7%9f%ad%e4%bf%a1%e6%88%90%e5%8a%9f";
        String responseData = response.replace("&", ",").replace("=", ":");
        Map<String, Object> map = JsonUtils.jsonToMap("{"+responseData+"}");
        Double result = (Double) map.get("result");
        String balance = (String) map.get("balance");
        Double linkid = (Double) map.get("linkid");
        String description = (String) map.get("description");*/


    }
}
