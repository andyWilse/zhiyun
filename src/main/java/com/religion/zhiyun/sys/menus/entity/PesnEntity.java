package com.religion.zhiyun.sys.menus.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_SYS_ROLE_PESN")
public class PesnEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ROLE_PMSN_REL_ID")
    private int rolePmsnRelId;//角色权限关系ID

    @Column(name = "PMSN_CD")
    private String pmsnCd;//权限代码

    @Column(name = "ROLE_ID")
    private String roleId;//角色ID

    @Column(name = "VALID_IND")
    private String validInd;//0否1是

}
