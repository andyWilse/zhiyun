package com.religion.zhiyun.record.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_operate_record_info")
public class RecordEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "RECORD_ID")
    private int recordId;//主键ID

    @Column(name = "OPERATOR")
    private String operator;//操作人

    @Column(name = "OPERATE_CONTENT")
    private String operateContent;//操作内容

    @Column(name = "OPERATE_DETAIL")
    private String operateDetail;//详情描述

    @Column(name = "OPERATE_TIME")
    private Date operateTime;//操作时间

    @Column(name = "OPERATE_REF")
    private String operateRef;//关联

    @Column(name = "OPERATE_TYPE")
    private String operateType;//操作类型

    private String operateTm;

}
