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
    private String masState;//状态：00-失败；01-成功；02-发起流程。

    @Column(name = "MAS_VENUE_ID")
    private Integer masVenueId;//关联场所ID

    @Column(name = "MAS_EVENT_ID")
    private Integer masEventId;//关联事件ID

    @Column(name = "MAS_REMARK")
    private String masRemark;//'备注'

}
