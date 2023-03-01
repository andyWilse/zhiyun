package com.religion.zhiyun.sys.menus.entity;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_SYS_USER_POST_REL")
public class PostEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "USER_POST_REL_ID")
    private int userPostRelId;//用户菜单关系ID

    @Column(name = "USER_ID")
    private String userId;//用户

    @Column(name = "POST_CD")
    private String postCd;//菜单

}
