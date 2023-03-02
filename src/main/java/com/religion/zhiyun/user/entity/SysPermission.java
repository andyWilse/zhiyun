package com.religion.zhiyun.user.entity;

import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_sys_perm")
public class SysPermission {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "PERM_ID")
    private int permId;//ID

    @Column(comment = "'权限名称'",name = "PERM_NM")
    private String permNm;

    @Column(comment = "'权限英文名称'",name = "PERM_ENM")
    private String permEnm;

    @Column(comment = "'直接上级权限代码'",name = "PERM_PRT_CD")
    private String permPrtCd;

    @Column(comment = "'上级权限代码集'",name = "PERM_PRT_CDS")
    private String permPrtCds;

    @Column(comment = "'叶子节点标识'",name = "LEAF_IND")
    private String leafInd;

    @Column(comment = "'有效标识'",name = "VALID_IND")
    private String validInd;

}
