package com.religion.zhiyun.monitor.entity;


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
@Table(name = "rm_monitor_manage_info")
public class MonitorEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "MONITOR_ID")
    private int monitorId;//主键ID

    @Column(name = "ACCESS_NUMBER")
    private String accessNumber;//监控设备接入编号

    @Column(name = "MONITOR_URL")
    private String monitorUrl;//监控地址

    @Column(name = "STATE")
    private String state;//状态

    @Column(name = "FUNCTION_TYPE")
    private String functionType;//功能类别

    @Column(name = "REL_VENUES_ID")
    private int relVenuesId;//场所

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private String createTime;//创建时间

    @Column(name = "LAST_MODIFIER")
    private String lastModifier;//最后修改人

    @Column(name = "LAST_MODIFY_TIME")
    private String lastModifyTime;//最后修改时间


    private String venuesAddres;//场所地址




}
