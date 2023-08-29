package com.religion.zhiyun;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Inlist {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String aa="5b6b7b0ee8b66c19c674495a21e8c2d4";
        String encode = URLEncoder.encode(aa, "UTF-8");
        String decode =URLDecoder.decode(encode,"UTF-8");
        System.out.println(encode);
        System.out.println(decode);
    }
}
