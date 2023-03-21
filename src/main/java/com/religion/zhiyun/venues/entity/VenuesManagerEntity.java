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


    @Column(name = "CERT_NBR")
    private String certNbr;//'有效证件号码'
    @Column(name = "CERT_TYPE_CD")
    private String certTypeCd;//有效证件类型

    @Column(name = "RELIGIOUS_TYPE")
    private String religiousType;//'教派'
    @Column(name = "SEX")
    private String sex;//'性别'
    @Column(name = "RELIGIOUS_SECT")
    private String religiousSect;//'教别'
    @Column(name = "NATION")
    private String nation;//'民族'
    @Column(name = "NATIVE_PLACE")
    private String nativePlace;//'籍贯'
    @Column(name = "RELIGIOUS_GROUP")
    private String religiousGroup;//'所在团体'
    @Column(name = "GROUP_POST")
    private String groupPost;//'团体任职情况'
    @Column(name = "VENUES_POST")
    private String venuesPost;//'宗教场所任职情况'
    @Column(name = "BIRTHDAY")
    private String birthday;//'生日



    private String picturesPathRemove;
    private Object[] fileList;

}
