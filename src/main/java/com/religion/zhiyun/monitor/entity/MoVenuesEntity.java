package com.religion.zhiyun.monitor.entity;

import lombok.*;

import javax.persistence.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoVenuesEntity {
    private String accessNumber;//监控设备接入编号
    private String monitorUrl;//监控地址
    private String state;//状态
    private int venuesId;//主键ID
    private String venuesName;// 场所名称
    private String venuesAddres;//场所地址
    private String responsiblePerson;//负责人
    private String liaisonMan;//工作联络员
    private String venuesStatus;//使用状态
    private String groupMembers;
    private String venuesPhone;// 场所电话
    private String organization;

    private String responsibleMobile;
    private String members;
    private String membersMobile;
    private String liaisonManMobile;
    private String[] monitors;

}
