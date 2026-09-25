package com.religion.zhiyun.color.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_THREE_COLOR")
public class ThreeColorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "CO_ID")
    private int coId;//主键ID

    @Column(name = "CO_VENUES_ID")
    private String coVenuesId;//关联场所ID

    @Column(name = "CO_COLOR")
    private String coColor;//三色要素颜色：数据字典-5001

    @Column(name = "CO_TYPE")
    private String coType;//三色要素类型：数据字典-5002

    @Column(name = "CO_OCCUR_TM")
    private Timestamp coOccurTm;//问题发生时间

    @Column(name = "CO_CONTENT")
    private String coContent;//问题描述

    @Column(name = "CO_STATE")
    private String coState;//进度：数据字典-7001

    @Column(name = "CO_PROGRESS")
    private String coProgress;//进度描述

    @Column(name = "CO_HANDLE_TM")
    private Timestamp coHandleTm;//完成时间

    @Column(name = "CO_REMARK")
    private String coRemark;//备注

    @Column(name = "CO_SHOW")
    private String coShow;//是否纳入统计（是否在终端展示）：0-否；1-是

    @Column(name = "CO_VALID")
    private String coValid;//是否删除：0-已删除；1-正常

    @Column(name = "CO_CREATOR")
    private String coCreator;//上传人

    @Column(name = "CO_CREATE_TM")
    private Timestamp coCreateTm;//上传时间

    @Column(name = "CO_MODIFIER")
    private String coModifier;//修改人

    @Column(name = "CO_MODIFY_TM")
    private Timestamp coModifyTm;//修改时间

    //场所名称
    private String venuesName;
    private String coColorNm;
    private String coTypeNm;
    private String coStateNm;
    private String occurTm;
    private String handleTm;
}
