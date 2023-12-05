package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum CallEnums {

    callout("callout", "呼出事件"),
    alerting("alerting", "振铃事件"),
    answer("answer", "应答事件"),
    collectInfo("collectInfo", "放音收号结果事件"),
    disconnect("disconnect", "挂机事件"),
    fee("fee", "话单事件"),
    ;

    private String code;
    private String name;

    private CallEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
