package com.religion.zhiyun.utils.Tool;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Authtool {
    private static final String TOKEN_SECRET = "ZCEQIUBFKSJBFJH2020BQWE";

    public static boolean AuthPermission(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            //验证JWT
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
