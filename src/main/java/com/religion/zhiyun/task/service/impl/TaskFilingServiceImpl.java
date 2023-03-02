package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.FilingEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskFilingService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.TokenUtils;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class TaskFilingServiceImpl implements TaskFilingService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;


    @Override
    @Transactional
    public Object launch(TaskEntity taskEntity) {
        //String loginNm = this.getLogin();
        String loginNm ="ab";

        Authentication.setAuthenticatedUserId(loginNm);
        Map<String, Object> variables = new HashMap<>();
        //根据登录人获取街镇干事 ab
        String ganShi="bb2";
        variables.put("assignee",ganShi );

        /**start**/
        //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_FILING_TASK_KEY.getCode(),variables);
        String processInstanceId = processInstance.getProcessInstanceId();
        /**end**/

        //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
        taskService.complete(tmp.getId(),variables);

        taskEntity.setLaunchPerson(loginNm);
        taskEntity.setTaskType(TaskParamsEnum.ZY_FILING_TASK_KEY.getName());
        taskEntity.setProcInstId(tmp.getProcessInstanceId());
        taskEntity.setEmergencyLevel("02");//普通
        //保存任务信息
        taskInfoMapper.addTask(taskEntity);
        log.info("任务id："+processInstanceId+" 发起申请，任务开始！");

        //保存备案信息
        if("03".equals(taskEntity.getFlowType())){
            taskInfoMapper.addFill(taskEntity);
        }

        return "流程id(唯一标识)procInstId:"+tmp.getProcessInstanceId();
    }

    @Override
    @Transactional
    public Object handle(TaskEntity taskEntity) {
        //String loginNm = this.getLogin();
        String loginNm ="bb2";

        String procInstId = taskEntity.getProcInstId();
        String flowType = taskEntity.getFlowType();
        //处理待办
        List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("isSuccess", true);
                //设置本地参数。在myListener1监听中获取。
                taskService.setVariableLocal(item.getId(), "isSuccess", true);
                //增加审批备注
                taskService.addComment(item.getId(), item.getProcessInstanceId(), "已处理");
                //完成此次审批。如果下节点为endEvent。结束流程
                taskService.complete(item.getId(), variables);
                log.info("任务id：" + procInstId + " 已处理，流程结束！");
            }
        }
        //流程更新处理结果
        taskEntity.setHandlePerson(loginNm);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        taskEntity.setHandleTime(new Date());
        taskInfoMapper.updateTask(taskEntity);
        log.info("任务id："+procInstId+" 已处理，数据更新！");

        //更新场所信息
        if("04".equals(flowType)){
            VenuesEntity venues=rmVenuesInfoMapper.getVenueByID(taskEntity.getRelVenuesId());
            venues.setResponsiblePerson(taskEntity.getResponsiblePerson());
            rmVenuesInfoMapper.update(venues);
            log.info("场所更新成功！");
        }else if("03".equals(flowType)){
            //保存备案信息
            taskInfoMapper.updateFill(taskEntity);
            log.info("活动备案成功！");
        }

        return "任务结束！";
    }

    /**
     * 获取登录人
     * @return
     */
    public String getLogin(){
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String loginNm = entity.getLoginNm();
        return loginNm;
    }
}
