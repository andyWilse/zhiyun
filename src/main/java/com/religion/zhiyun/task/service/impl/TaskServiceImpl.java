package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.task.service.TaskService;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public AppResponse deployment(String taskKey) {
        long code= ResultCode.FAILED.getCode();
        String message= "流程部署";

        try {
            //第一步
            DeploymentBuilder builder=  repositoryService.createDeployment();
            String filePath="";
            if(taskKey.equals(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode())){//上报
                filePath=TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath();
            }else if(taskKey.equals(TaskParamsEnum.ZY_ISSUED_TASK_KEY.getCode())){//下达
                filePath=TaskParamsEnum.ZY_ISSUED_TASK_KEY.getFilePath();
            }else if(taskKey.equals(TaskParamsEnum.ZY_FILING_TASK_KEY.getCode())){//活动备案、场所更新
                filePath=TaskParamsEnum.ZY_FILING_TASK_KEY.getFilePath();
            }else{
                throw new RuntimeException("流程key值在系统不存在，请确认后重新填写");
            }
            builder.addClasspathResource(filePath);
            String id = builder.deploy().getId();

            repositoryService.setDeploymentKey(id,taskKey);
            message = "流程id："+id+",部署成功";
            log.info(message);
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    public RespPageBean getProcdef(Integer page,Integer size,String taskName) {
        List<ProcdefEntity> procdef = taskInfoMapper.getProcdef(page,size,taskName);

        return new RespPageBean(200l,procdef.toArray());
    }

    @Override
    public PageResponse getMonitorTask(String taskId) {
        long code= ResultCode.FAILED.getCode();
        String message= "报修设备任务信息获取失败！";
        List<Map<String, Object>> monitorTask =new ArrayList<>();
        try {
            monitorTask = taskInfoMapper.getMonitorTask(taskId);
            message = "报修设备任务信息获取成功";
            log.info(message);
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message,monitorTask.toArray());
    }

    @Override
    public AppResponse getTasksGather(int num, String dateType) {
        long code =ResultCode.FAILED.getCode();
        String message="统计任务数量数据失败！";
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            if ("month".equals(dateType)){
                list=taskInfoMapper.getTaskMonth(num);
            }else if ("week".equals(dateType)){
                list=taskInfoMapper.getTaskWeekGather(num);
            }else if ("day".equals(dateType)){
                list=taskInfoMapper.getTaskDayGather(num);
            }
            code= ResultCode.SUCCESS.getCode();
            message="统计任务数量数据成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="统计任务数量数据失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,list.toArray());
    }
}
