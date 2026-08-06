package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum TaskActEnums {
    AI_WARN_NODE_01("20010001", "预警平台","aiStart"),
    AI_WARN_NODE_02("20010002", "人工审核","review"),
    AI_WARN_NODE_03("20010003", "基层处理","manage"),
    AI_WARN_NODE_04("20010004", "评价结束","final"),
    AI_WARN_NODE_05("20010005", "评价退回","aiEnd"),
    AI_WARN_NODE_06("20010006", "退回","back"),
    AI_WARN_NODE_07("20010007", "误报解除","dismiss"),

    AI_NODE_STATE_00("00", "未处理",""),
    AI_NODE_STATE_01("01", "已处理",""),
    AI_NODE_STATE_02("02", "修改",""),
    AI_NODE_STATE_03("03", "删除",""),

    //AI事件状态
    AI_WARN_STATE_00("00", "已接收",""),
    AI_WARN_STATE_01("01", "已复核",""),
    AI_WARN_STATE_02("02", "已处置",""),
    AI_WARN_STATE_03("03", "已评价结束",""),
    AI_WARN_STATE_04("04", "已退回",""),
    AI_WARN_STATE_05("05", "误报解除",""),

    AI_ASS_STATE_01("01", "新增",""),
    AI_ASS_STATE_02("02", "修改",""),
    AI_ASS_STATE_03("03", "删除",""),
    ;

    private String code;
    private String chinaNm;
    private String englishNm;

    TaskActEnums(String code, String chinaNm, String englishNm) {
        this.code = code;
        this.chinaNm = chinaNm;
        this.englishNm = englishNm;
    }
}
