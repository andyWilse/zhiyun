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
@Table(name = "USER_ACTION_LOG")
public class UseractionLogEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;//主键ID

    @Column(name = "CREATE_TIME")
    private Date createTime;//创建时间：yyyy-MM-dd HH:mm:ss

    @Column(name = "USER_ID")
    private String userId;//用户标识：用户id,原则上为浙政钉id或浙里办id

    @Column(name = "USER_ROLE")
    private String userRole;//用户类型:目前限定为群众、企业、政府工作人员、第三方

    @Column(name = "AREA_CODE")
    private String areaCode;//地区编码:前六位行政区划代码

    //字典定义为：1-登录2-离开3-办事开始4-办事结束5-进入某功能模块
    @Column(name = "ACTION_TYPE")
    private int actionType;//操作类型:字典值1010

    /*当actionType为3-办事开始、 4-办事结束时，actionId用来对某一次办事进行唯一标识，一般为事件的id;
    当actionType为5-进入某功能 模块时，actionld为该功能模块 的名称，可以中文表示*/
    @Column(name = "ACTION_ID")
    private String actionId;//操作标识

    //当actionType为3-办事开始、 4-办事结束时，用eventType对事件类型进行唯一标识，一般为中文
    @Column(name = "EVENT_TYPE")
    private String eventType;//事件类型

    @Column(name = "PROCESSING_TIME")
    private int processingTime;//办理时长:当actionType为4-办事结束时，需要记录总办理时长，单位为秒

    @Column(name = "ACTION_TIME")
    private String actionTime;//操作时间:yyyy-MM-dd HH:mm:ss

    @Column(name = "ACTION_DURATION")
    private int actionDuration;//操作时长：处理用户操作的时长，即后端接口接受到请求到返回的用时，单位为毫秒

    @Column(name = "ACTION_STATUS")
    private int actionStatus;//操作状态：应用系统处理用户操作的结果状态，字典定义为：0-成功1- 失败

    @Column(name = "APP_CODE")
    private String appCode;//应用编码:应用系统在IRS上注册后的应用编码

    private Date overTime;
    private Date startTime;

    public UseractionLogEntity(String userId, int actionType, int actionStatus, Date overTime, Date startTime) {
        this.userId = userId;
        this.actionType = actionType;
        this.actionStatus = actionStatus;
        this.overTime = overTime;
        this.startTime = startTime;
    }

}
