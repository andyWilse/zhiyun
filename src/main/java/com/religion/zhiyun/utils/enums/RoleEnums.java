package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum RoleEnums {
    ZU_YUAN("10000007", "三人驻堂组员"),
    ZU_ZHANG("10000006", "三人驻堂组长"),
    JIE_GAN("10000005", "街镇干事"),
    JIE_WEI("10000004", "街镇委员"),
    QU_GAN("10000003", "区干事"),
    QU_WEI("10000002", "区委员"),
    ADMIN("10000001", "超级管理员"),
    ;

    private String code;
    private String name;

    private RoleEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
