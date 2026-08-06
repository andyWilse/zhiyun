package com.religion.zhiyun.utils.enums;

import lombok.Getter;

@Getter
public enum OperaEnums {

    venue_add("100701", "场所新增"),
    venue_update("100702", "场所修改"),
    venue_delete("100703", "场所删除"),

    staff_delete("100706", "教职人员删除"),

    venue_manager_add("100711", "场所负责人新增"),
    venue_manager_update("100712", "场所负责人修改"),
    venue_manager_delete("100713", "场所负责人删除"),

    user_add("100721", "用户新增"),
    user_update("100722", "用户修改"),
    user_delete("100723", "用户删除"),
    user_password_update("100724", "用户修改密码"),
    user_grand_update("100725", "用户权限修改"),

    news_add("100731", "宣传新增"),
    news_update("100732", "宣传修改"),
    news_delete("100733", "宣传删除"),

    user_venue_delete("100741", "场所三人驻堂成员去除"),
    user_venue_add("100742", "场所三人驻堂成员添加"),

    venue_user_delete("100751", "三人驻堂成员关联场所去除"),
    venue_user_add("100752", "三人驻堂成员加入场所"),

    act_add("100761", "流程节点新增"),
    act_update("100762", "流程节点修改"),
    act_delete("100763", "流程节点删除"),

    ass_add("100766", "预警接收人新增"),
    ass_update("100767", "预警接收人修改"),
    ass_delete("100768", "预警接收人删除"),

    ;
    private String code;
    private String name;

    private OperaEnums(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
