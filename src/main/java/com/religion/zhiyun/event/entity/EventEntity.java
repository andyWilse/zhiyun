package com.religion.zhiyun.event.entity;

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
@Table(name = "rm_event_info")
public class EventEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private int eventId;//主键ID

    @Column(name = "ACCESS_NUMBER")
    private String accessNumber;//设备标识

    @Column(name = "DEVICE_NAME")
    private String deviceName;//设备名称

    @Column(name = "DEVICE_TYPE")
    private String deviceType;//设备类型

    @Column(name = "WARN_TIME")
    private String warnTime;//预警时间

    @Column(name = "REL_VENUES_ID")
    private int relVenuesId;//地点   关联场所主键id

    @Column(name = "RESPONSIBLE_PERSON")
    private String responsiblePerson;//责任人

    @Column(name = "EVENT_TYPE")
    private String eventType;//事件类型  00-火灾预警；01-人脸识别；02-任务预警；03-人流聚集

    @Column(name = "OID")
    private String oId;//对象id

    @Column(name = "RAWDATA")
    private String rawData;//原始数据

    @Column(name = "EXT")
    private String ext;//扩展信息

    @Column(name = "STREAM_ID")
    private String stream_Id;//数据流标识

    @Column(name = "EVENT_DATA")
    private String eventData;//消息内容

    @Column(name = "STREAM_UNIT")
    private String streamUnit;//数据单位

    @Column(name = "EVENT_LEVEL")
    private String eventLevel;//事件级别

    @Column(name = "LOCATION")
    private String location;//设备位置

    @Column(name = "EVENT_STATE")
    private String eventState;//事件状态   00-待查实；01-已完成；02-误报

    @Column(name = "HANDLE_RESULTS")
    private String handleTesults;//处理结果

    @Column(name = "HANDLE_TIME")
    private String handleTime;//处理时间



}
