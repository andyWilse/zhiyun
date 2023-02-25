package com.religion.zhiyun.test202302;

import com.alibaba.fastjson.JSONObject;
import com.religion.zhiyun.utils.RedisUtils;

public class getRedis {
    public static void main(String[] args) {
        String username="";
        String code="";
        RedisUtils redisUtils=new RedisUtils();
        JSONObject Vcode = (JSONObject)redisUtils.get("verifyCode"+username);
        System.out.println("memPhone:"+username);
        System.out.println(Vcode);
        JSONObject verifyCode = (JSONObject)redisUtils.get("verifyCode"+username);
        System.out.println("传入的验证码是："+code);
        if(verifyCode.get("code").equals(code)){
            System.out.println("验证码正确");
        }

    }
}
