package com.religion.zhiyun.interfaces.entity.minzong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorEntity{

    private String authorizeCode;//授权码，十分钟有效期,必输
    private String state;//请求和回调的状态值,必输


}
