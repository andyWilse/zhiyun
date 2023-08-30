package com.religion.zhiyun.interfaces.entity.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiAuthorEntity {
    private String access_token;//访问token（重点关注）
    private String token_type;//token类型
    private String refresh_token;//刷新token
    private String expires_in;//过期时间（重点关注）
    private String scope;//范围
    private String license;//许可证
}
