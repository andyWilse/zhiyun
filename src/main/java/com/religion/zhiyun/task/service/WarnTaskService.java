package com.religion.zhiyun.task.service;

public interface WarnTaskService {
    /** 流程部署。部署一次就可以了。**/
    public Object deployment();

    /** 提交申请(发起) **/
    public Object launch();

    /** 提交申请(上报) **/
    public Object report();

    /** 流程结束**/
    public Object approve();

    /** 查询用户未完成的历史记录**/
    public Object getUnTask();

    /** 查询完成的历史记录 **/
    public Object getFinishTask();
}
