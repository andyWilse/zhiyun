package com.religion.zhiyun.test202302;

import org.springframework.util.DigestUtils;

public class Md5 {
    public static void main(String[] args) {
        String pwd = "wzsmzj@2023";
        // 基于spring框架中的DigestUtils工具类进行密码加密
        String hashedPwd1 = DigestUtils.md5DigestAsHex((pwd).getBytes());
        System.out.println(hashedPwd1);
    }
}
