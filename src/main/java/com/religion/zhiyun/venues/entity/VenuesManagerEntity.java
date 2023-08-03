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

    @Column(name = "MAILING_ADDRESS")
    private String mailingAddress;//通信地址

    @Column(name = "REGISTERED_RESIDENCE")
    private String registeredResidence;//户籍所在地

    @Column(name = "POLITICAL")
    private String political;//政治安排

    @Column(name = "SOCIETY_CULTURE")
    private String societyCulture;//社会文化程度

    @Column(name = "SOCIETY_SCHOOL")
    private String societySchool;//社会毕业院校

    @Column(name = "RELIGIOUS_CULTURE")
    private String religiousCulture;//宗教文化程度

    @Column(name = "RELIGIOUS_SCHOOL")
    private String religiousSchool;//宗教毕业院校

    @Column(name = "COMPANY")
    private String company;//所在单位

    @Column(name = "STAFF_POST")
    private String staffPost;//教职身份

    @Column(name = "POSTTION")
    private String posttion;//职位

    @Column(name = "CERTIFICATE_UNIT")
    private String certificateUnit;//证书颁发单位

    @Column(name = "CERTIFICATE_TIME")
    private String certificateTime;//证书颁发时间

    @Column(name = "CERTIFICATE_NUMBER")
    private String certificateNumber;//证书编号


    private String picturesPathRemove;
    private Object[] fileList;
    private String originMobile;

}
