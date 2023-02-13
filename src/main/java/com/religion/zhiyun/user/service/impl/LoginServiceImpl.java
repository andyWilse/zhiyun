package com.religion.zhiyun.user.service.impl;

import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/*
extends ServiceImpl<UserLoginMapper, UserLogin> implements UserService
*/
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserMapper userMapper;
    //设置过期时间
    private static final long EXPIRE_DATE=30*60*100000;
    //token秘钥
    private static final String TOKEN_SECRET = "ZCEQIUBFKSJBFJH2020BQWE";

    @Override
    public SysUserEntity queryByName(String username) {
        return userMapper.queryByName(username);
    }

    public Object test(String username){
        return username;
    }

    public static String token (SysUserEntity userinfo){
        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("username",userinfo.getLoginNm())
                    .withClaim("password",userinfo.getPasswords())
                    .withClaim("userNbr",userinfo.getUserNbr())
                    .withClaim("identity",userinfo.getIdentity())
                    .withClaim("ofcId", userinfo.getOfcId())
                    .withClaim("userId",userinfo.getUserId())
                    .withClaim("expireAt",date)
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }
}
