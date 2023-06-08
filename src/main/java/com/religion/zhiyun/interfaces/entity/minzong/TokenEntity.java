package com.religion.zhiyun.interfaces.entity.minzong;

import com.religion.zhiyun.interfaces.entity.minzong.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenEntity extends BaseEntity {
    //access_token，第三方客户需要记录此token，有效期内使用此token完成接口调用，无须重复申请token
    private String access_token;//必输
    private String token_type;//token类型,必输
    private String expires_in;//有效期：默认24小时,必输
}
