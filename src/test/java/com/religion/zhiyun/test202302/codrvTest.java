package com.religion.zhiyun.test202302;

import com.alibaba.fastjson.JSONObject;
import com.religion.zhiyun.utils.RedisUtils;
import com.religion.zhiyun.utils.sms.SendVerifyCode;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class codrvTest {
    public static void main(String[] args) {
        try{
        JSONObject json = null;
        //随机生成验证码
        String code = String.valueOf(new Random().nextInt(999999));
        //将验证码通过榛子云接口发送至手机
        /*ZhenziSmsClient client = new ZhenziSmsClient(apiUrl, appId, appSecret);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("number", memPhone);
        //这个是短信模板的参数，从平台获取
        params.put("templateId", "8369");
        //这个参数就是短信模板上那两个参数
        String[] templateParams = new String[2];
        templateParams[0] = code;
        templateParams[1] = "2分钟";
        params.put("templateParams", templateParams);
        String result = client.send(params);*/

            String contents="【瓯海宗教智治】"+code+"(登录验证码，5分钟内有效)。请勿向任何人泄露，以免造成任何损失。";
            SendVerifyCode send=new SendVerifyCode();
            //String s = send.sendVerifyCode(contents, "18514260203");
            //json = JSONObject.parseObject(s);
        /*if (json.getIntValue("code")!=0){//发送短信失败
            return  false;
        }*/
        //将验证码存到redis中,同时存入创建时间（也可以传入session）
        //以json存放，这里使用的是阿里的fastjson
        json = new JSONObject();
            SendVerifyCode.sendVerifyCode(contents, "18514260203");
        json.put("memPhone","18514260203");
        json.put("code",code);
        json.put("createTime",System.currentTimeMillis());
        RedisUtils redisUtils=new RedisUtils();
        // 将认证码存入redis，有效时长5分钟
        redisUtils.set("verifyCode"+"18514260203",json,5L, TimeUnit.MINUTES);
      /*      AppRedisCacheManager a=new AppRedisCacheManager();
            a.setUnion("verifyCode"+"18514260203",json,5L, TimeUnit.MINUTES);

            Object o = a.get("verifyCode" + "18514260203");
            System.out.println(o);*/
        } catch (Exception e) {
        e.printStackTrace();
    }

    }
}
