package com.religion.zhiyun.venues.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

@Table(name = "RM_VENUES_INFO")

public  class VenuesEntity implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "VENUES_ID")
    private int venuesId;//主键ID

    @Column(name = "VENUES_NAME")
    private String venuesName;// 场所名称

    @Column(name = "RELIGIOUS_SECT")
    private String religiousSect;// 教派类别

    @Column(name = "REGISTER_NBR")
    private String registerNbr;// 登记证号

    @Column(name = "VENUES_PHONE")
    private String venuesPhone;// 场所电话

    @Column(name = "ORGANIZATION")
    private String organization;// 所属机构

    @Column(name = "VENUES_ADDRES")
    private String venuesAddres;//场所地址

    @Column(name = "PICTURES_PATH")
    private String picturesPath;//照片地址

    @Column(name = "RESPONSIBLE_PERSON")
    private String responsiblePerson;//负责人

    @Column(name = "LIAISON_MAN")
    private String liaisonMan;//工作联络员

    @Column(name = "BRIEF_INTRODUCTION")
    private String briefIntroduction;//简介

    @Column(name = "VENUES_STATUS")
    private String venuesStatus;//使用状态

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private String createTime;//创建时间

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private String lastModifyTime;//最后修改时间

    @Column(name = "LONGITUDE")
    private String longitude;//经度

    @Column(name = "LATITUDE")
    private String Latitude;//纬度

    @Column(name = "GROUP_MEMBERS")
    private String groupMembers;

    @Column(name = "PROVINCE")
    private String province;
    @Column(name = "CITY")
    private String city;
    @Column(name = "AREA")
    private String area;
    @Column(name = "TOWN")
    private String town;

    @Column(name = "VENUES_STAFF")
    private String venuesStaff;

    private String venuesStaffId;

    private String dictCd;

    private String picturesPathRemove;
    private Object[] fileList;


    private String latitudes;//纬度
}
