package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum ProcessState {
    MAS_STATE_00("00", "失败"),
    MAS_STATE_01("01", "成功"),
    MAS_STATE_02("02", "流程已发起"),
    MAS_STATE_03("03", "重复预警"),
    ;

    private String code;
    private String name;

    private ProcessState(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(String code) {
        for (ProcessState item : ProcessState.values()) {
            if (item.name().equals(code)) {
                return item.name;
            }
        }
        return code;
    }
}
