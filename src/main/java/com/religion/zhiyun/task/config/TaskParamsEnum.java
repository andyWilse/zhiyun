package com.religion.zhiyun.task.config;

import lombok.Getter;

@Getter
public enum TaskParamsEnum {

    ZY_REPORT_TASK_KEY("reportTaskKey", "事件上报任务主键"),
    ZY_REPORT_TASK_PATH("process/eventReportTask.bpmn", "事件上报任务流程"),

    ;

    private String code;
    private String name;

    private TaskParamsEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
