package com.religion.zhiyun.sys.log.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "APP_METRIC_LOG")
public class AppmetricLogEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;//主键ID

    @Column(name = "CREATE_TIME")
    private Date createTime;//创建时间：yyyy-MM-dd HH:mm:ss

    @Column(name = "NAME")
    private String name;//指标名

    @Column(name = "VALUE")
    private String value;//指标值

    @Column(name = "APP_CODE")
    private String appCode;//应用编码:应用系统在IRS上注册后的应用编码

    public AppmetricLogEntity(Date createTime, String name, String value, String appCode) {
        this.createTime = createTime;
        this.name = name;
        this.value = value;
        this.appCode = appCode;
    }
}
