package com.religion.zhiyun.back;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/start")
@RestController
public class ActivitiTestController {

    //private static final Logger logger = LoggerFactory.getLogger(ActivitiTestController.class);

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @RequestMapping("/test1")
    public void test1() {
        log.info("Start.........");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("teId");
        log.info("流程启动成功，流程id:{}", pi.getId());
    }


    @RequestMapping("/test2")
    public void test2() {
        String userId = "root";
        List<Task> resultTask = taskService.createTaskQuery().processDefinitionKey("test").taskCandidateOrAssigned(userId).list();
        log.info("任务列表：{}", resultTask);
    }

}

