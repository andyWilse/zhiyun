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

    @Column(name = "RELIGIOUS_SECT")
    private String religiousSect;//宗教

    @Column(name = "STAFF_PICTURE")
    private String staffPicture;//教职人员照片

    @Column(name = "STAFF_TELPHONE")
    private String staffTelphone;//电话号码

    @Column(name = "STAFF_STATUS")
    private String staffStatus;//状态  00-入职中；01-在职；02-已离职

    @Column(name = "STAFF_POST")
    private int staffPost;//职位

    @Column(name = "REL_VENUES_ID")
    private int relVenuesId;//关联场所

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;//创建时间

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private Timestamp lastModifyTime;//最后修改时间

}
