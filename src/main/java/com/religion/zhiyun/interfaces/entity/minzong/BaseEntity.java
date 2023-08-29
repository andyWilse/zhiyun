package com.religion.zhiyun.interfaces.entity.minzong;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {
    private int code;//状态码,必输
    private String data;//承载数据,非必输
    private String msg;//返回消息,必输
    private boolean success;//是否成功,必输
}
