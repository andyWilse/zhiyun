package com.religion.zhiyun.back;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j

class ActivitiApplicationTests {

 /*   @Autowired
    private ProcessEngine processEngine;

    *//**
     * 流程定义的部署
     *//*
    @Test
    public void createDeploy() {
        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("diagram/holiday.bpmn")//添加bpmn资源
                .addClasspathResource("diagram/holiday.png")
                .name("请假申请单流程")
                .deploy();

        log.info("流程部署id:" + deployment.getName());
        log.info("流程部署名称:" + deployment.getId());
    }*/
}
