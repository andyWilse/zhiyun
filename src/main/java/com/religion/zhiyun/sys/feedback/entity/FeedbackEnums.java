package com.religion.zhiyun.sys.feedback.entity;

import lombok.Getter;

@Getter
public enum FeedbackEnums {
    FEEDBACK_MY("01", "使用反馈"),
    AXIS_UPDATE("02", "坐标修改"),

    ;

    private String code;
    private String message;

    private FeedbackEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
