package com.religion.zhiyun.sys.menus.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_SYS_ROLE_PESN")
public class PesnEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ROLE_PMSN_REL_ID")
    private int rolePmsnRelId;//角色权限关系ID

    @Column(name = "PMSN_CD")
    private int pmsnCd;//权限代码

    @Column(name = "ROLE_ID")
    private int roleId;//角色ID

}
