package com.religion.zhiyun.sys.base.enums;

import lombok.Getter;

@Getter
public enum SysBaseEnum {
    SEND_MESSAGE_SWITCH("SEND_MESSAGE", "短信发送开关"),

    ;
    private String code;
    private String content;

    private SysBaseEnum(String code, String content) {
        this.code = code;
        this.content = content;
    }
}
