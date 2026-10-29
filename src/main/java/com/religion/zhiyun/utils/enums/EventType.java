package com.religion.zhiyun.utils.enums;

public enum EventType {

    EVENT_TYPE_01("01", "明火"),
    EVENT_TYPE_02("02", "超限"),
    EVENT_TYPE_03("03", "重点"),
    EVENT_TYPE_04("04", "聚集"),
    EVENT_TYPE_05("05", "设备报修"),
    EVENT_TYPE_06("06", "画面异常"),

    ;
    private String code;
    private String message;

    private EventType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(String code) {
        for (EventType item : EventType.values()) {
            if (item.code.equals(code)) {
                return item.message;
            }
        }
        return code;
    }
}
