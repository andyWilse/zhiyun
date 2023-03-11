package com.religion.zhiyun.task.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_FILING_INFO")
public class FilingEntity {
    @Id
    @Column(name = "FILING_ID")
    private int filingId;//主键ID

    @Column(name = "TASK_NAME")
    private String taskName;//任务名称

    @Column(name = "TASK_CONTENT")
    private String taskContent;//'任务内容'

    @Column(name = "TASK_TIME")
    private String taskTime;//'活动时间'

    @Column(name = "REL_VENUES_ID")
    private String relVenuesId;//'地址(关联场所主键ID）'

    @Column(name = "TASK_PICTURE")
    private String taskPicture;//'图片'

    @Column(name = "TASK_VIDEO")
    private String taskVideo;//'视频'

    @Column(name = "LAUNCH_PERSON")
    private String launchPerson;//'申请人'

    @Column(name = "LAUNCH_TIME")
    private Date launchTime;//'申请时间'

    @Column(name = "HANDLE_PERSON")
    private String handlePerson;//'处理人'

    @Column(name = "HANDLE_RESULTS")
    private String handleResults;//'处理结果'

    @Column(name = "HANDLE_TIME")
    private Date handleTime;//'处理时间'

    @Column(name = "PROC_INST_ID")
    private String procInstId;//'关联任务'

    @Column(name = "PART_NUM")
    private String partNum;//'参与人数'

    @Column(name = "RESPONSIBLE_PERSON")
    private String responsiblePerson;//'负责人'

    private String responsibleMobil;//'手机号'
    private String registerNbr;//'场所编号'

}
