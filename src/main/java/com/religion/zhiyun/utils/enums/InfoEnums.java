package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum InfoEnums {

    USER_FIND("/user/find/", "用户查询"),
    USER_ADD("/user/add/", "用户新增"),
    USER_UPDATE("/user/update/", "用户修改"),
    USER_DELETE("/user/delete/", "用户删除"),

    ;

    private String code;
    private String name;

    private InfoEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
