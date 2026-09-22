package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum ActEnums {

    ACT_OPERATION_01("400101", "修改接收时间"),
    ACT_OPERATION_02("400102", "新增接收人"),
    ACT_OPERATION_03("400103", "删除接收人"),
    ACT_OPERATION_04("400104", "修改处理人"),
    ACT_OPERATION_05("400105", "修改处理时间"),
    ACT_OPERATION_06("400106", "删除节点"),

    ;

    private String code;
    private String name;

    private ActEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(String code) {
        for (ActEnums item : ActEnums.values()) {
            if (item.name().equals(code)) {
                return item.name;
            }
        }
        return code;
    }
}
