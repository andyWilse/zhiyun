package com.religion.zhiyun.utils.sms;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import org.springframework.http.HttpMethod;
import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class SendVerifyCode {
    public static String sendVerifyCode(String content,String mobiles) throws Exception {
        String message="";//返回信息
        String ymdHms = TimeTool.getYmdHms();//获取时间戳

        String smsUrl="http://api.sms1086.com/Api/verifycode.aspx";//请求url
        String username="zongjiao001";//用户名
        String password="zongjiao001";//密码
        password = DigestUtils.md5DigestAsHex((password+ymdHms).getBytes());//密码加密

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
                message="发送验证码成功";
            }else{
                message="发送验证码失败！"+decode;
                throw new RuntimeException(message);
            }
        }

        return message;
    }
}
