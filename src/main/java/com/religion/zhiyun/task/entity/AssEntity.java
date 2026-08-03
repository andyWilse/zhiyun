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
@Table(name = "TASK_ACT_ASSIGNEE")
public class AssEntity implements Serializable {
    @Id
    @Column(name = "ASS_ID")
    private int assId;//主键ID

    @Column(name = "ASS_ACT_ID")
    private int assActId;//流程处理记录ID

    @Column(name = "ASS_ASSIGNEE")
    private String assAssignee;//接收人

    @Column(name = "ASS_MODIFY_TM")
    private Timestamp assModifyTm;//修改时间

    @Column(name = "ASS_STATE")
    private String assState;//状态：00：删除；01：新增；02：修改

    @Column(name = "ASS_MARK")
    private String assMark;//备注


}
