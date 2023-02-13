package com.religion.zhiyun.user.entity;

import com.baomidou.mybatisplus.annotation.*;

import com.gitee.sunchenbin.mybatis.actable.annotation.*;

import lombok.*;

import java.sql.Timestamp;
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
@Table(name = "RM_SYS_USER_INFO")
public class SysUserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private int userId;//主键ID
    
    @Column(comment = "登录名",defaultValue="",name = "LOGIN_NM")
    private String loginNm;

    @Column(comment = "用户密码",defaultValue="",name = "PASSWORDS")
    private String passwords;

    @Column(comment = "用户姓名",defaultValue="",name = "USER_NM")
    private String userNm;

    @Column(comment = "用户手机号",defaultValue="",name = "USER_MOBILE")
    private String userMobile;

    @Column(comment = "email",defaultValue="",name = "USER_EMAIL")
    private String userEmail;

    @Column(comment = "头像地址",defaultValue="",name = "USER_PHOTO_URL")
    private String userPhotoUrl;

    @Column(comment = "身份",defaultValue="" ,name = "IDENTITY")
    private String identity;

    @Column(comment = "工号",defaultValue="" ,name = "USER_NBR")
    private String userNbr;

    @Column(comment = "工号",defaultValue="" ,name = "OFC_ID")
    private String ofcId;

    @Column(comment = "创建时间",name = "CREATE_TIME")
    private Timestamp createTime;

    @Column(comment = "时间",name = "LAST_MODIFY_TIME")
    private Timestamp lastModifyTime;

    @Column(comment = "",name = "WEAK_PW_IND")
    private String weakPwInd;

    private String uId;

}
