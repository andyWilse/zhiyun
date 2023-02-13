package com.religion.zhiyun.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import com.religion.zhiyun.user.entity.SysUserEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class TokenUtils {
    //token秘钥
    private static final String TOKEN_SECRET = "ZCEQIUBFKSJBFJH2020BQWE";

    public static SysUserEntity getToken() {
        /*String token = request.getHeader(jwtHeader);
        if (Strings.isNullOrEmpty(token)) {
            return ReturnMsg.defaultSuccessResult();
        }*/
        SysUserEntity enti=new SysUserEntity();
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("x-token");
            DecodedJWT verify = jwtVerifier.verify(token);
            String username = verify.getClaim("username").asString();
            String identity = verify.getClaim("identity").asString();
            String userNbr = verify.getClaim("userNbr").asString();
            String ofcId = verify.getClaim("ofcId").asString();
            String userId = verify.getClaim("userId").asString();

            //Long expireAt = verify.getClaim("expireAt").asLong();
            enti.setLoginNm(username);
            enti.setIdentity(identity);
            enti.setUserNbr(userNbr);
            enti.setOfcId(ofcId);
            enti.setUId(userId);

            //token参数不对
            /*if (!Strings.isNullOrEmpty(username)
                    && !Strings.isNullOrEmpty(identity)
                    && expireAt != null
                    && expireAt > System.currentTimeMillis()) {
                Optional<BuildingManagerBO> bm = buildingManagerService.findByUsername(username);
                LoginUserInfoVO loginUserInfoVO = bm.map(bo -> new LoginUserInfoVO(token, role, bo.getUserId(), bo.getUsername(), bo.getLicence(),
                        bo.getBuildCount())).orElse(null);
                return ReturnMsg.wrapSuccessfulResult(loginUserInfoVO);
            }*/
        } catch (JWTVerificationException ignore) {
            //验证失败
        }
        /*return ReturnMsg.defaultSuccessResult();*/
        return enti;
    }
}
