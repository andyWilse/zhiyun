package com.religion.zhiyun.back;

public class Base64 {

    public static void main(String[] args) {
        byte[] bytes = "clientId:clientSecret".getBytes();
        String encoded = java.util.Base64.getEncoder().encodeToString(bytes);
        System.out.println(encoded);

        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        String data = encoder.encodeToString(bytes);
    }
}
