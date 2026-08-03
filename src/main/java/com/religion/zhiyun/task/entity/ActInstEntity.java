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
@Table(name = "TASK_ACT_INST")
public class ActInstEntity implements Serializable {

    @Id
    @Column(name = "ACT_ID")
    private int actId;//主键ID

    @Column(name = "ACT_CODE")
    private int actCode;//节点

    @Column(name = "ACT_RECEIVER")
    private String actReceiver;//接收人

    @Column(name = "ACT_RECEIVE_TM")
    private Timestamp  actReceiveTm;//接收时间

    @Column(name = "ACT_HANDLER")
    private String actHandler;//处理人

    @Column(name = "ACT_HANDLE_TM")
    private Timestamp actHandleTm;//处理时间

    @Column(name = "ACT_COMMENT")
    private String actComment;//意见

    @Column(name = "ACT_STATE")
    private String actState;//状态：01：未处理；02：已处理；03：回退；04：误报解除

    @Column(name = "ACT_INST_ID")
    private String actInstId;//流程id

    @Column(name = "ACT_TASK_ID")
    private String actTaskId;//流程任务id

    @Column(name = "ACT_MARK")
    private String actMark;//备注

    private String currentState;//当前处理状态
    private String actNode;//节点
    private String actHandleTime;//节点
}
