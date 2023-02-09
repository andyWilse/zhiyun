package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.task.config.TestCommand;
import com.religion.zhiyun.task.service.WarnTaskService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Slf4j
@Service
public class WarnTaskServiceImpl implements WarnTaskService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    ManagementService managementService;

    @Override
    public Object deployment() {
        //第一步
        DeploymentBuilder builder=  repositoryService.createDeployment();
        builder.addClasspathResource("processes/zuYuan.bpmn");
        String id = builder.deploy().getId();
        repositoryService.setDeploymentKey(id,"zyTask");
        log.info("流程id："+id);
        return id;
    }

    @Override
    public Object launch() {
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String nbr = entity.getUserNbr();
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        String userId = sysUserEntity.getUserId()+"";
        String ofcId = sysUserEntity.getOfcId();
        Authentication.setAuthenticatedUserId(userId);

        //inputUser就是在bpmn中Assignee配置的参数
        Map<String, Object> variables = new HashMap<>();
        variables.put("assignee1", ofcId);

        /**start**/
        //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        ProcessInstance processInstance =runtimeService.startProcessInstanceByKey("zyTask",variables);
        String processInstanceId = processInstance.getProcessInstanceId();
        /**end**/

        TaskQuery taskQuery = taskService.createTaskQuery();
        Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
        //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
        taskService.complete(tmp.getId(),variables);

        return sysUserEntity.getLoginNm();
    }

    @Override
    public Object report() {
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String nbr = entity.getUserNbr();
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        String userId = sysUserEntity.getUserId()+"";
        String ofcId = sysUserEntity.getOfcId();
        String identity = sysUserEntity.getIdentity();

        //根据角色信息获取自己的待办 act_ru_task
        List<Task> T = taskService.createTaskQuery().taskAssignee(userId).list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = this.setFlag(identity, "go",ofcId);
                variables.put("isSuccess", true);
                //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                taskService.setVariableLocal(item.getId(),"isSuccess",false);
                //增加审批备注
                taskService.addComment(item.getId(),item.getProcessInstanceId(),"上报");
                //getNode("1f81d0a0-a829-11ed-ba49-d8f883ed6283","",item);
                //完成此次审批。由下节点审批
                taskService.complete(item.getId(), variables);
            }
        }
        return sysUserEntity.getLoginNm();
    }

    @Override
    public Object approve() {
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String nbr = entity.getUserNbr();
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        String userId = sysUserEntity.getUserId()+"";
        String ofcId = sysUserEntity.getOfcId();
        String identity = sysUserEntity.getIdentity();
        //根据角色信息获取自己的待办
        List<Task> T = taskService.createTaskQuery().taskAssignee(userId).list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = this.setFlag(identity, "end",ofcId);
                variables.put("isSuccess", true);

                //设置本地参数。在myListener1监听中获取。
                taskService.setVariableLocal(item.getId(),"isSuccess",true);
                //增加审批备注
                taskService.addComment(item.getId(),item.getProcessInstanceId(),"同意");
                //完成此次审批。如果下节点为endEvent。结束流程
                taskService.complete(item.getId(), variables);
            }
        }
        return sysUserEntity.getLoginNm();
    }

    @Override
    public Object getUnTask() {
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String nbr = entity.getUserNbr();
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        String userId = sysUserEntity.getUserId()+"";

        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> unfinishedTaskList = historicProcessInstanceQuery.startedBy(userId).unfinished().list();
        return unfinishedTaskList;
    }

    @Override
    public Object getFinishTask() {
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String nbr = entity.getUserNbr();
        SysUserEntity sysUserEntity = sysUserMapper.queryByNbr(nbr);
        String userId = sysUserEntity.getUserId()+"";

        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> finishedTaskList = historicProcessInstanceQuery.startedBy(userId).finished().list();

        return finishedTaskList;
    }
    public Map<String, Object>  setFlag(String identity,String flag,String ofcId) {
        Map<String, Object> variables = new HashMap<>();
        if("10000006".equals(identity)){
            variables.put("flag3", "end");
            variables.put("assignee2", ofcId);
        }else if("10000005".equals(identity)){
            variables.put("flag4", "end");
            variables.put("assignee3", ofcId);
        }else if("10000004".equals(identity)){
            variables.put("flag5", "end");
            variables.put("assignee4", ofcId);
        }else if("10000003".equals(identity)){
            variables.put("flag6", "end");
            variables.put("assignee5", ofcId);
        }else if("10000002".equals(identity)){
            variables.put("flag7", "end");
            variables.put("assignee6", ofcId);

        }
        return variables;
    }


    public void getNode(String actId,String key,Task task) {
        String taskId = "";
        String tagNode = "jieWeiFlow";
        //获取当前任务id
        //Task task = taskService.createTaskQuery().processInstanceId(actId).singleResult();
        System.out.println("任务id：" + task.getId());
        taskId = task.getId();

        //获取目标
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
        /*for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                //获取用户任务
                UserTask userTask = (UserTask) flowElement;//节点转换
                if (userTask.getName().equals(key)) {
                    tagNode = userTask.getId();
                    break;
                }


            }
        }*/
        //跳转
        managementService.executeCommand(new TestCommand(taskId, tagNode));
    }

    /**
     *  设置下一个节点的审核人
     * @param processId:流程实例ID
     * @param Assignee:审核人ID
     *
     */
    public void setFirstTaskAssignee(String processId,String Assignee){
        //查询当前所有待执行的节点
        List<Task> tasks = taskService.createTaskQuery().list();
        for (Task t:tasks) {
            //判断当前实例待执行的节点
            if (t.getProcessInstanceId().equals(processId)){
                //设置审核人  （参数：节点ID，审核人ID）
                taskService.setAssignee(t.getId(),Assignee);
            }
        }
    }

}
