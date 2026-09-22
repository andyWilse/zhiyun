package com.religion.zhiyun.task.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "TASK_ACT_INST_HIS")
public class ActInstHisEntity implements Serializable {

    @Id
    @Column(name = "HIS_ID")
    private int hisId;//主键ID

    @Column(name = "HIS_ACT_ID")
    private int hisActId;//关联主键ID

    @Column(name = "HIS_ACT_NEW")
    private String hisActNew;//修改后内容

    @Column(name = "HIS_ACT_OLD")
    private String hisActOld;//修改前内容

    @Column(name = "HIS_ACT_TYPE")
    private String hisActType;//修改类型

    @Column(name = "HIS_ACT_MODIFIER")
    private String hisActModifier;//修改人

    @Column(name = "HIS_ACT_MODIFY_TM")
    private Timestamp hisActModifyTm;//修改时间


}
