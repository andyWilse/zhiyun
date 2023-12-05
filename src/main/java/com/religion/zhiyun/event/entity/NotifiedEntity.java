package com.religion.zhiyun.event.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_event_notified_info")
public class NotifiedEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "NOTIFIED_ID")
    private int notifiedId;//主键ID

    @Column(name = "NOTIFIED_USER")
    private String notifiedUser;//被通知人(用户)

    @Column(name = "NOTIFIED_FLAG")
    private String notifiedFlag;//状态:00-未通知；01-已通知；02-一键上报；03-误报解除

    @Column(name = "NOTIFIED_TIME")
    private Date notifiedTime;//通知时间

    @Column(name = "REPORT_TIME")
    private String reportTime;//上报时间

    @Column(name = "REF_EVENT_ID")
    private int refEventId;//关联事件

    @Column(name = "NOTIFIED_MANAGER")
    private String notifiedManager;//被通知人

    @Column(name = "EVENT_TYPE")
    private String eventType;//预警类型

}
