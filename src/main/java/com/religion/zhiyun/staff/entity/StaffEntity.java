package com.religion.zhiyun.staff.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_staff_info")
public class StaffEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "STAFF_ID")
    private int staffId;//主键ID

    @Column(name = "STAFF_CD")
    private String staffCd;//人员编号

    @Column(name = "STAFF_CN_NM")
    private String staffName;//中文名称

    @Column(name = "CERT_TYPE_CD")
    private String certTypeCd;//有效证件类型

    @Column(name = "CERT_NBR")
    private String certNbr;//有效证件号码

    @Column(name = "SEX")
    private String sex;//性别

    @Column(name = "RELIGIOUS_SECT")
    private String religiousSect;//教别

    @Column(name = "RELIGIOUS_TYPE")
    private String religiousType;//教派

    @Column(name = "NATION")
    private String nation;//民族

    @Column(name = "NATIVE_PLACE")
    private String nativePlace;//籍贯

    @Column(name = "RELIGIOUS_GROUP")
    private String religiousGroup;//所在团体

    @Column(name = "GROUP_POST")
    private String groupPost;//所在团体任职情况

    @Column(name = "RELIGIOUS_VENUES")
    private String religiousVenues;//所在场所

    @Column(name = "VENUES_POST")
    private String venuesPost;//所在场所任职情况

    @Column(name = "BIRTHDAY")
    private Timestamp birthday;//生日

    @Column(name = "MAILING_ADDRESS")
    private String mailingAddress;//通信地址

    @Column(name = "REGISTERED_RESIDENCE")
    private String registeredResidence;//户籍所在地

    @Column(name = "STAFF_PICTURE")
    private String staffPicture;//教职人员照片

    @Column(name = "STAFF_TELPHONE")
    private String staffTelphone;//电话号码

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

    @Column(name = "STAFF_STATUS")
    private String staffStatus;//状态  00-入职中；01-在职；02-已离职

    @Column(name = "STAFF_POST")
    private String staffPost;//教职身份

    @Column(name = "REL_VENUES_ID")
    private int relVenuesId;//关联场所

    @Column(name = "POSTTION")
    private String posttion;//职位

    @Column(name = "CERTIFICATE_UNIT")
    private String certificateUnit;//证书颁发单位

    @Column(name = "CERTIFICATE_TIME")
    private Timestamp certificateTime;//证书颁发时间

    @Column(name = "CERTIFICATE_NUMBER")
    private String certificateNumber;//证书编号

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;//创建时间

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private Timestamp lastModifyTime;//最后修改时间

    @Column(name = "PASSWORDS")
    private String passwords;//加密密码


}
