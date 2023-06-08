package com.religion.zhiyun.interfaces.entity.xueliang;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessToken {

    private String access_token;//用于后续请求校验的 token
    private String refresh_token;//请求过期重新刷新请求 token 时使用(暂未使用)
    private String token_type;//oauth2 的 type，bearer 是没有加密的
    private String expires_in;//从现在算起的过期时间
    private String invalid_token;//如果请求时 token 已经过期会返回

}
