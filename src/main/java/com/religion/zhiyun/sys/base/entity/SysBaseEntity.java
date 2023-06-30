package com.religion.zhiyun.sys.base.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_SYS_BASE_INFO")
public class SysBaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "sysId")
    private int SYS_ID;//主键ID

    @Column(name = "openState")
    private String OPEN_STATE;//0-关；1-开

    @Column(name = "sysEnNm")
    private String SYS_EN_NM;//功能编码

    @Column(name = "sysCnNm")
    private String SYS_CN_NM;//功能描述
}
