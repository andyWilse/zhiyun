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
@Table(name = "RM_SYS_MENU_INFO")
public class MenuEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "MENU_ID")
    private int menuId;//主键ID

    @Column(name = "PMSN_CD")
    private String pmsnCd;//

    @Column(name = "PARENT_ID")
    private int parentId;//

    @Column(name = "MENU_PRT_IDS")
    private String menuPrtIds;//

    @Column(name = "MENU_NM")
    private String menuNm;//菜单名称

    @Column(name = "SORT_LVL")
    private int sortLvl;//

    @Column(name = "HREF")
    private String href;//

    @Column(name = "ICON_NM")
    private String iconNm;//

    @Column(name = "RESOURCE_TYPE")
    private String resourceType;//

    @Column(name = "APP_CODE")
    private String appCode;//

    @Column(name = "VALID_IND")
    private String validInd;//

}
