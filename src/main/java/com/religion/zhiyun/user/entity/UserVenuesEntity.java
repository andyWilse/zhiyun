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

    @Column(name = "USER_ID")
    private int userId;

    @Column(name = "VENUES_ID")
    private int venuesId;

    @Column(name = "VALID_IND")
    private String validInd;

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;//创建时间

    @Column(name = "LAST_MODIFY_TIME")
    private Timestamp lastModifyTime;//最后修改时间

    @Column(name = "MARK")
    private String mark;



}
