package com.religion.zhiyun.userLogs.entity;


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
@Table(name = "rm_user_logs_info")
public class LogsEntity  implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;//主键ID

    @Column(name = "USER_NAME")
    private String userName;//用户名

    @Column(name = "USER_NBR")
    private int userNbr;//用户编号

    @Column(name = "INTERFACE_CODE")
    private String interfaceCode;//接口

    @Column(name = "INTERFACE_NAME")
    private String interfaceName;//接口名称

    @Column(name = "REQUEST_PARAM")
    private String requestParam;//请求参数

    @Column(name = "RESPONSE_RESULT")
    private String responseResult;//响应结果

    @Column(name = "FK_DATE")
    private Timestamp fkDate;//调用接口时间



}
