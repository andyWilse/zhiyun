package com.religion.zhiyun.test202302;

import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class saltTest {
    public static void main(String[] args) {
        String name="zuzhang1";
        String salts="10000006";

        ByteSource admin = ByteSource.Util.bytes(name+salts);
        String hashAlgorithmName = "MD5";//加密方式
        String password = "1";//密码原值
        char[]  crdentials=(char[])(password != null ? password.toCharArray() : null);
        //Object salt = "YWRtaW5udWxs";//盐值
        Object salt=ByteSource.Util.bytes(name+salts);
        int hashIterations = 1024;//加密1024次
        Hash result = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);

        String aa= String.valueOf(result);
        String passwords ="bd0ee48d106fc1f2d6ccc54c89d827c0";
        byte[] bytes1 = passwords.getBytes();
        System.out.println(aa.equals(passwords));

        String pa=String.valueOf(result);
        System.out.println("pa:"+pa);

       // ByteSource a=

        //1 YWRtaW5udWxs 1024
    }
}
