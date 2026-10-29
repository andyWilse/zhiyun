package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum GeneralEnum {
    STATE_0("0", "否",""),
    STATE_1("1", "是",""),
    ;

    private String code;
    private String name;
    private String mark;

     GeneralEnum(String code, String name, String mark) {
        this.code = code;
        this.name = name;
        this.mark = mark;
    }

    public static String getName(String code) {
        for (ActEnums item : ActEnums.values()) {
            if (item.getCode().equals(code)) {
                return item.getName();
            }
        }
        return code;
    }
}
