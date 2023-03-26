package com.religion.zhiyun.event.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_event_report_men")
public class EventReportMenEntity {
    @Id
    @Column(name = "MEN_ID")
    private int menId;//主键ID

    @Column(name = "REPORT_MEN")
    private String reportMen;

    @Column(name = "REPORT_TIME")
    private String reportTime;

    @Column(name = "REF_EVENT_ID")
    private String refEventId;

    @Column(name = "PROC_INST_ID")
    private String procInstId;

}
