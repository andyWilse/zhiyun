package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.task.service.TaskService;
import com.religion.zhiyun.utils.response.RespPageBean;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public String deployment(String taskKey) {
        //第一步
        DeploymentBuilder builder=  repositoryService.createDeployment();
        String filePath="";
        if(taskKey.equals(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode())){//上报
            filePath=TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath();
        }else if(taskKey.equals(TaskParamsEnum.ZY_ISSUED_TASK_KEY.getCode())){//下达
            filePath=TaskParamsEnum.ZY_ISSUED_TASK_KEY.getFilePath();
        }else if(taskKey.equals(TaskParamsEnum.ZY_FILING_TASK_KEY.getCode())){//活动备案、场所更新
            filePath=TaskParamsEnum.ZY_FILING_TASK_KEY.getFilePath();
        }
        builder.addClasspathResource(filePath);
        String id = builder.deploy().getId();

        repositoryService.setDeploymentKey(id,taskKey);
        String message="流程id："+id+",部署成功";
        log.info(message);
        return message;
    }

    @Override
    public RespPageBean getProcdef(Integer page,Integer size,String taskName) {
        List<ProcdefEntity> procdef = taskInfoMapper.getProcdef(page,size,taskName);

        return new RespPageBean(200l,procdef.toArray());
    }
}
