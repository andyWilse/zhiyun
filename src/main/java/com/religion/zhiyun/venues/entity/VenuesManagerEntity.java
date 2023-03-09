package com.religion.zhiyun.venues.entity;

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
@Table(name = "rm_venues_manager_info")
public class VenuesManagerEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "MANAGER_ID")
    private int managerId;//主键ID

    @Column(name = "MANAGER_CN_NM")
    private String managerCnNm;//

    @Column(name = "MANAGER_EN_NM")
    private String managerEnNm;//

    @Column(name = "MANAGER_EMAIL")
    private String managerEmail;//

    @Column(name = "MANAGER_MOBILE")
    private String managerMobile;//

    @Column(name = "MANAGER_TEL")
    private String managerTel;//

    @Column(name = "MANAGER_TYPE_CD")
    private String managerTypeCd;//

    @Column(name = "MANAGER_PHOTO_PATH")
    private String managerPhotoPath;//

    @Column(name = "VALID_IND")
    private String validInd;//

    @Column(name = "PASSWORDS")
    private String passwords;//

    @Column(name = "PASSWORDS_ORIGIN")
    private String passwordsOrigin;//

    @Column(name = "CREATOR")
    private String creator;//

    @Column(name = "CREATE_TIME")
    private Date createTime;//

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//

    @Column(name = "LAST_MODIFY_TIME")
    private Date lastModifyTime;//
}
