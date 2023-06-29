package com.religion.zhiyun.utils.sms;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import org.springframework.http.HttpMethod;
import org.springframework.util.DigestUtils;

import java.net.URLDecoder;
import java.util.Map;

public class SendMassage {

    public static String sendSms(String content,String mobiles){
        //String contents="【云监控中心】尊敬的消防智慧云用户，"+content+"，请及时妥善处理";//短信内容
        //String contents="【云监控中心】尊敬的消防智慧云用户，您位于浙江省温州市龙湾区浙南云谷F幢203室的203室，发生温感火警，请及时妥善处理";
        //String content= URLEncoder.encode(contents, "GB2312");

        String message="";//返回信息
        String ymdHms = TimeTool.getYmdHms();//获取时间戳

        String smsUrl="http://api.sms1086.com/api/Sendutf8.aspx";//请求url
        String username="zongjiao001";//用户名
        String password="zongjiao001";//密码
        password = DigestUtils.md5DigestAsHex((password+ymdHms).getBytes());//密码加密
        try {
            //参数封装
            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.addParam("username",username);
            params.addParam("password",password);
            params.addParam("mobiles",mobiles);
            params.addParam("content",content);
            params.addParam("timestamp", ymdHms);
            HttpService service=new HttpService(smsUrl);

            //发送请求
            String response = service.service(smsUrl, params);

            //String response ="result=8&balance=96&linkid=2FB29BED699045F6BA7FC1579EBE037B&description=%e5%8f%91%e9%80%81%e7%9f%ad%e4%bf%a1%e6%88%90%e5%8a%9f";
            //结果解析
            if(null!=response && ""!=response){
                String responseData = response.replace("=", ":").replace("&", ",");
                Map<String, Object> map = JsonUtils.jsonToMap("{"+responseData+"}");
                Double result = (Double) map.get("result");
                String linkId = (String) map.get("linkid");
                Double balance = (Double) map.get("balance");
                String description = (String) map.get("description");

                //获取码值
                Double doubleValueObject = new Double(result);
                int code=doubleValueObject.intValue();
                //解码
                String decode = URLDecoder.decode(description, "GB2312");
                if(0==code){
                    message="发送短信成功";
                }else{
                    message="发送短信失败！"+decode;
                }
            }

        } catch (Exception e) {
            message="发送短信失败！"+e.getMessage();
            e.printStackTrace();
        }

        return message;
    }
}
