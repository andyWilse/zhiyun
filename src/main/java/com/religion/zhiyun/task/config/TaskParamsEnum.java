package com.religion.zhiyun.task.config;

import lombok.Getter;

@Getter
public enum TaskParamsEnum {

    ZY_REPORT_TASK_KEY("reportTaskKey","process/eventReportTask.bpmn", "上报任务流程"),
    ZY_ISSUED_TASK_KEY("issuedTaskKey","process/issuedTask.bpmn", "下达任务流程"),
    ZY_FILING_TASK_KEY("filingTaskKey","process/filingTask.bpmn", "活动备案/场所更新任务流程"),

    ;

    private String code;
    private String filePath;
    private String name;

    private TaskParamsEnum(String code,String filePath, String name) {
        this.code = code;
        this.filePath=filePath;
        this.name = name;
    }
}
