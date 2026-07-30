package com.religion.zhiyun.user.entity;

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
@Table(name = "rm_user_venues_map")
public class UserVenuesEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "UV_ID")
    private int uvId;

    @Column(name = "UV_USER_ID")
    private int uvUserId;

    @Column(name = "UV_VENUES_ID")
    private int uvVenuesId;

    @Column(name = "UV_VALID_IND")
    private String uvValidInd;

    @Column(name = "UV_CREATE_TIME")
    private Timestamp uvCreateTime;//创建时间

    @Column(name = "UV_MODIFY_TIME")
    private Timestamp uvModifyTime;//最后修改时间

    @Column(name = "UV_MARK")
    private String uvMark;



}
