package com.religion.zhiyun.interfaces.entity.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiImageEntity {
    private int code;//状态码,必输
    private String msg;//返回消息,必输

    private String data;//承载数据,非必输
    private String downloadUrl;
}
