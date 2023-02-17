package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskIssuedService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.TokenUtils;
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
public class TaskIssuedServiceImpl implements TaskIssuedService {

    @Autowired
    private TaskService taskService;

    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Override
    public Object deployment() {
        return null;
    }

    @Override
    @Transactional
    public Object launch(TaskEntity taskEntity) {
        //String loginNm = this.getLogin();
        String loginNm ="ab3";

        Authentication.setAuthenticatedUserId(loginNm);
        Map<String, Object> variables = new HashMap<>();

        List<String> userList = new ArrayList();
        userList.add("ab1");
        userList.add("ab2");
        variables.put("handleList",userList );

        /**start**/
        //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_ISSUED_TASK_KEY.getCode(),variables);
        String processInstanceId = processInstance.getProcessInstanceId();
        /**end**/

        //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
        TaskQuery taskQuery = taskService.createTaskQuery();
        Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
        //taskService.setAssignee("assignee2",userNbr);
        taskService.complete(tmp.getId(),variables);

        taskEntity.setLaunchPerson(loginNm);
        taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
        taskEntity.setProcInstId(tmp.getProcessInstanceId());
        taskEntity.setFlowType("02");
        //保存任务信息
        taskInfoMapper.addTask(taskEntity);

        log.info("任务id："+processInstanceId+" 发起申请，任务开始！");



        return "流程id(唯一标识)procInstId:"+tmp.getProcessInstanceId();
    }

    @Override
    @Transactional
    public Object handle(String procInstId) {
        //String loginNm = this.getLogin();
        String loginNm ="ab";
        //处理待办
        List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                if (item.getAssignee().equals(loginNm)) {
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("nrOfCompletedInstances", 1);
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
        }
        //更新处理结果
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setHandlePerson(loginNm);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        taskEntity.setHandleTime(new Date());
        taskEntity.setHandleResults("处理完成");
        taskEntity.setProcInstId(procInstId);
        taskInfoMapper.updateTask(taskEntity);

        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return null;
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
