package com.religion.zhiyun.test202302;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.sms.sm.MessageSend;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Md5 {
    public static void main(String[] args) {
        /*String pwd = "wzsmzj@2023";
        // 基于spring框架中的DigestUtils工具类进行密码加密
        String hashedPwd1 = DigestUtils.md5DigestAsHex((pwd).getBytes());
        System.out.println(hashedPwd1);*/

       /* long thisTime = System.currentTimeMillis() / 1000;

        long ssTime = thisTime / 60 % 60;

        System.out.println(thisTime);
        System.out.println(ssTime);
        System.out.println(thisTime-ssTime);*/


       /* String un="test";
        String ps="123";
        *//*String ti="1596254400000";*//*
        long ti =1596254400000l;

        String s = DigestUtils.md5DigestAsHex((ps).getBytes());
        System.out.println(s);
        String s0 = DigestUtils.md5DigestAsHex((un+ti+s).getBytes());
        System.out.println(s0);


        String ymdHms = TimeTool.getYmdHms();
        System.out.println(ymdHms);

        long l = System.currentTimeMillis();
        System.out.println(l);*/

    }
}
