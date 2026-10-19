package com.religion.zhiyun.interfaces.entity.mas;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_MAS_INFO")
public class MasEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "MAS_ID")
    private int masId;//主键ID

    @Column(name = "MAS_DEVICE_ID")
    private String masDeviceId;//'抓拍设备编码'

    @Column(name = "MAS_MONITOR_TIME")
    private Timestamp masMonitorTime;//抓拍时间

    @Column(name = "MAS_ACCT_TIME")
    private Timestamp masAcctTime;//'接收时间'

    @Column(name = "MAS_STATE")
    private String masState;//状态：1-已接收；2-已推送AI

    @Column(name = "MAS_EVENT_ID")
    private String masEventId;//关联事件ID

    @Column(name = "MAS_REMARK")
    private String masRemark;//'备注'

}
