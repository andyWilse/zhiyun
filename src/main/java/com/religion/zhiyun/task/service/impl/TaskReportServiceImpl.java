package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.config.TestCommand;
import com.religion.zhiyun.task.dao.ActReProcdefMapper;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.RespPageBean;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class TaskReportServiceImpl implements TaskReportService {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ActReProcdefMapper actReProcdefMapper;

    @Autowired
    ManagementService managementService;
    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Override
    public Object deployment() {
        //第一步
        DeploymentBuilder builder=  repositoryService.createDeployment();
        builder.addClasspathResource(TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath());
        String id = builder.deploy().getId();
        repositoryService.setDeploymentKey(id,TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode());
        log.info(TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath()+"流程id："+id+",部署成功");
        return id;
    }

    @Override
    @Transactional
    public Object launch(TaskEntity taskEntity) {
        String ofcId ="";
        String procInstId="";
        try {
            //String loginNm = this.getLogin();
            String loginNm ="ab";
            Authentication.setAuthenticatedUserId(loginNm);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            ofcId = sysUserEntity.getOfcId();
            //inputUser就是在bpmn中Assignee配置的参数
            Map<String, Object> variables = new HashMap<>();
            variables.put("assignee2", ofcId);
            /**start**/
            //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode(),variables);
            String processInstanceId = processInstance.getProcessInstanceId();
            /**end**/
            //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
            TaskQuery taskQuery = taskService.createTaskQuery();
            Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
            procInstId=tmp.getProcessInstanceId();
            //taskService.setAssignee("assignee2",userNbr);
            taskService.complete(tmp.getId(),variables);

            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
            taskEntity.setProcInstId(procInstId);
            taskEntity.setFlowType("01");
            //保存任务信息
            taskInfoMapper.addTask(taskEntity);

            log.info("任务id："+processInstanceId+" 发起申请，任务开始！");
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("未知错误，请联系管理员！") ;
        }

        return "流程id(唯一标识)procInstId:"+procInstId;
    }

    @Override
    @Transactional
    public Object report(String procInstId) {
       // String loginNm = this.getLogin();
        String loginNm ="ab1";
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
        String ofcId = sysUserEntity.getOfcId();
        String identity = sysUserEntity.getIdentity();

        //根据角色信息获取自己的待办 act_ru_task
        //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
        //处理自己的待办
        List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = this.setFlag(identity, "go",ofcId,procInstId);
                variables.put("isSuccess", true);
                //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                taskService.setVariableLocal(item.getId(),"isSuccess",false);
                //增加审批备注
                taskService.addComment(item.getId(),item.getProcessInstanceId(),"上报");
                //完成此次审批。由下节点审批
                taskService.complete(item.getId(), variables);
            }
        }
        log.info("任务id："+procInstId+" 上报");
        return loginNm;
    }

    @Override
    @Transactional
    public Object handle(String procInstId,String handleResults) {
        //String loginNm = this.getLogin();
        String loginNm ="ab2";
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
        String ofcId = sysUserEntity.getOfcId();
        String identity = sysUserEntity.getIdentity();

        //根据角色信息获取自己的待办
        //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
        //处理待办
        List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
        if(!ObjectUtils.isEmpty(T)) {
            for (Task item : T) {
                Map<String, Object> variables = this.setFlag(identity, "end",ofcId,procInstId);
                variables.put("isSuccess", true);

                //设置本地参数。在myListener1监听中获取。
                taskService.setVariableLocal(item.getId(),"isSuccess",true);
                //增加审批备注
                taskService.addComment(item.getId(),item.getProcessInstanceId(),handleResults);
                //完成此次审批。如果下节点为endEvent。结束流程
                taskService.complete(item.getId(), variables);

                log.info("任务id："+procInstId+" 已处理，流程结束！");
            }
        }
        //更新处理结果
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setHandlePerson(loginNm);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        taskEntity.setHandleTime(new Date());
        taskEntity.setHandleResults(handleResults);
        taskEntity.setProcInstId(procInstId);
        taskInfoMapper.updateTask(taskEntity);

        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return loginNm;
    }

    @Override
    public RespPageBean getTasking(Integer page, Integer size,String taskName, String taskContent) {
        SysUserEntity entity = TokenUtils.getToken();
        if(null==entity){
            throw new RuntimeException("登录人信息丢失，请登陆后重试！");
        }
        String loginNm = entity.getLoginNm();
        List<TaskEntity> taskEntities = taskInfoMapper.queryTasks(page, size, taskName, taskContent,loginNm);

        return new RespPageBean(200l,taskEntities.toArray());
    }

    @Override
    public String getTaskNum() {
        String login = this.getLogin();
        Map<String, Object> map = taskInfoMapper.getTaskNum(login);
        String sum = JsonUtils.objectTOJSONString(map);
        return sum;
    }

    @Override
    public Object getUnTask() {
        String login = this.getLogin();
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> unfinishedList = historicProcessInstanceQuery.startedBy(login).unfinished().list();
        List<TaskEntity> unfinishedTaskList=new ArrayList<>();
        if(null!=unfinishedList && unfinishedList.size()>0){
            for(int i=0;i<unfinishedList.size();i++){
                HistoricProcessInstance historicProcessInstance = unfinishedList.get(i);
                String superProcessInstanceId = historicProcessInstance.getId();
                TaskEntity taskEntity = taskInfoMapper.queryByInstId(superProcessInstanceId);
                unfinishedTaskList.add(taskEntity);
            }
        }

        return unfinishedTaskList;
    }

    @Override
    public Object getFinishTask() {
        String login = this.getLogin();
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = defaultProcessEngine.getHistoryService();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        List<HistoricProcessInstance> finishedList = historicProcessInstanceQuery.startedBy(login).finished().list();
        List<TaskEntity> finishedTaskList=new ArrayList<>();
        if(null!=finishedList && finishedList.size()>0){
            for(int i=0;i<finishedList.size();i++){
                HistoricProcessInstance historicProcessInstance = finishedList.get(i);
                String superProcessInstanceId = historicProcessInstance.getId();
                TaskEntity taskEntity = taskInfoMapper.queryByInstId(superProcessInstanceId);
                finishedTaskList.add(taskEntity);
            }
        }
        return finishedTaskList;
    }

    /**
     * 流程走向定义
     * @param identity
     * @param flag
     * @param ofcId
     * @param procInstId
     * @return
     */
    public Map<String, Object>  setFlag(String identity,String flag,String ofcId,String procInstId) {
        String identi = sysUserMapper.queryStarter(procInstId);
        int no=0;
        if("10000007".equals(identi)){
            no=2;
        }else if("10000006".equals(identi)){
            no=1;
        } else if("10000005".equals(identi)){
            no=0;
        }else if("10000004".equals(identi)){
            no=-1;
        }else if("10000003".equals(identi)){
            no=-2;
        }else if("10000002".equals(identi)){
            no=-3;
        }

        int flagNo=0;
        int assiNo=0;
        Map<String, Object> variables = new HashMap<>();
        if("10000006".equals(identity) ){
            flagNo=no;
            assiNo=no+1;
        }else if("10000005".equals(identity)){
            flagNo=no+1;
            assiNo=no+2;
        }else if("10000004".equals(identity)){
            flagNo=no+2;
            assiNo=no+3;
        }else if("10000003".equals(identity)){
            flagNo=no+3;
            assiNo=no+4;
        }else if("10000002".equals(identity)){
            flagNo=no+4;
            assiNo=no+5;
        } else if("10000001".equals(identity)){
            flagNo=no+5;
            assiNo=no+6;
        }

        variables.put("flag"+flagNo, flag);
        variables.put("assignee"+assiNo, ofcId);

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


    public List<Map<String,Object>> queryTasks(){
        return actReProcdefMapper.queryTasks();
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
