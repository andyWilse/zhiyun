package com.religion.zhiyun.monitor.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "rm_monitor_stat")
public class MonitorStatEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;//主键ID

    @Column(name = "CODE")
    private String code;//状态码

    @Column(name = "MSG")
    private String msg;//信息

    @Column(name = "DATA")
    private String data;//结果

    @Column(name = "CHANNEL_ID")
    private String channelId;//通道编码

    @Column(name = "RECORD_DATE")
    private String recordDate;//记录时间

}
