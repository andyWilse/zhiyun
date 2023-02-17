package com.religion.zhiyun.group.entity;

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
@Table(name = "rm_group_info")
public class GroupEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "GROUP_ID")
    private int groupId;//主键ID

    @Column(name = "GROUP_CD")
    private String groupCD;//编号

    @Column(name = "GROUP_NAME")
    private String groupName;//名字

    @Column(name = "GROUP_PHONE")
    private String groupPhone;//团体电话

    @Column(name = "GROUP_ADDRESS")
    private String groupAddress;//地址

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;//创建时间

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private Timestamp lastModifyTime;//最后修改时间





}
