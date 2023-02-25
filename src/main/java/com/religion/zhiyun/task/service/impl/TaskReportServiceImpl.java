package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.config.TestCommand;
import com.religion.zhiyun.task.dao.ActReProcdefMapper;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
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
    public AppResponse launch(TaskEntity taskEntity) {
        long code=ResultCode.FAILED.getCode();
        String message="上报任务发起失败";
        String procInstId="";
        //String loginNm = this.getLogin();
        String userMobile ="1112";

        try {
            Map<String, Object> nextHandler = this.getNextHandler(userMobile, message);
            if(null==nextHandler){
                message="下节点处理人信息丢失！";
                throw new RuntimeException(message);
            }
            String loginNm = (String) nextHandler.get("loginNm");
            List<String> userList = (List<String>) nextHandler.get("userList");
            Map<String, Object> variables = new HashMap<>();
            variables.put("handleList2",userList );

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

            //保存任务信息
            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
            taskEntity.setProcInstId(procInstId);
            taskEntity.setFlowType("01");
            taskInfoMapper.addTask(taskEntity);

            code=ResultCode.SUCCESS.getCode();
            message="任务id："+processInstanceId+" 发起申请，任务开始！";
            log.info(message);

        } catch (RuntimeException e) {
            code=ResultCode.FAILED.getCode();
            throw new RuntimeException(message) ;
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="未知错误，请联系管理员！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse report(String procInstId, String handleResults, String feedBack, String picture) {
        long code=ResultCode.FAILED.getCode();
        String message="上报任务上报失败！";
        String userMobile ="1112";
        userMobile ="1108";
        userMobile ="1105";
       // userMobile ="1103";
        //userMobile ="1101";
        try {
            Map<String, Object> nextHandler = this.getNextHandler(userMobile, message);
            if(null==nextHandler){
                message="下节点处理人信息丢失！";
                throw new RuntimeException(message);
            }
            String loginNm = (String) nextHandler.get("loginNm");
            String identity = (String) nextHandler.get("identity");
            List<String> userList = (List<String>) nextHandler.get("userList");

            //根据角色信息获取自己的待办 act_ru_task
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理自己的待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        Map<String, Object> variables = this.setFlag(identity, "go", userList, procInstId);
                        variables.put("isSuccess", true);
                        //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                        taskService.setVariableLocal(item.getId(),"isSuccess",false);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),"上报");
                        //完成此次审批。由下节点审批
                        taskService.complete(item.getId(), variables);
                    }
                }
            }
            log.info("任务id："+procInstId+" 上报");
            code= ResultCode.SUCCESS.getCode();
            message="上报流程上报成功！流程id(唯一标识)procInstId:"+ procInstId;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="上报流程上报失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture) {
        //String loginNm = this.getLogin();
        long code=ResultCode.FAILED.getCode();
        String message="任务处理";
        String userMobile ="1107";
        userMobile ="1101";
        userMobile ="1103";
        try {
            Map<String, Object> nextHandler = this.getNextHandler(userMobile, message);
            if(null==nextHandler){
                message="下节点处理人信息丢失！";
                throw new RuntimeException(message);
            }
            String loginNm = (String) nextHandler.get("loginNm");
            String identity = (String) nextHandler.get("identity");
            List<String> userList = (List<String>) nextHandler.get("userList");

            //根据角色信息获取自己的待办
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        Map<String, Object> variables = this.setFlag(identity, "end",userList,procInstId);
                        variables.put("nrOfCompletedInstances", 1);
                        variables.put("isSuccess", true);

                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),handleResults);
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id："+procInstId+" 已处理，流程结束！");

                        //更新处理结果
                        TaskEntity taskEntity=new TaskEntity();
                        taskEntity.setHandlePerson(loginNm);
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        taskEntity.setHandleTime(new Date());
                        taskEntity.setHandleResults(handleResults);
                        taskEntity.setProcInstId(procInstId);
                        taskInfoMapper.updateTask(taskEntity);
                    }
                }
            }

            code= ResultCode.SUCCESS.getCode();
            message="上报流程处理成功！流程id(唯一标识)procInstId:"+ procInstId;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="上报流程处理失败！";
            e.printStackTrace();
        }
        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return new AppResponse(code,message);
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
    public AppResponse getTaskNum() {
        AppResponse res=new AppResponse();
        Map<String, Object> map = null;
        long code=0l;
        String message="";
        try {
            //String login = this.getLogin();
            String login ="ab";
            map = taskInfoMapper.getTaskNum(login);
            code= ResultCode.SUCCESS.getCode();
            message="任务数量统计成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="任务数量统计失败！";
            e.printStackTrace();
        }
        Object[] objects = map.entrySet().toArray();
        res.setCode(code);
        res.setMessage(message);
        res.setResult(objects);

        return res;
    }

    @Override
    public AppResponse getUnTask() {
        List<TaskEntity> unfinishedTaskList= null;
        long code=0l;
        String message="";
        try {
            //String login = this.getLogin();
            String login ="ab";
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            HistoryService historyService = defaultProcessEngine.getHistoryService();
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> unfinishedList = historicProcessInstanceQuery.startedBy(login).unfinished().list();
            unfinishedTaskList = new ArrayList<>();
            if(null!=unfinishedList && unfinishedList.size()>0){
                for(int i=0;i<unfinishedList.size();i++){
                    HistoricProcessInstance historicProcessInstance = unfinishedList.get(i);
                    String superProcessInstanceId = historicProcessInstance.getId();
                    TaskEntity taskEntity = taskInfoMapper.queryByInstId(superProcessInstanceId);
                    unfinishedTaskList.add(taskEntity);
                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="查找未完成任务成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="查找未完成任务失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,unfinishedTaskList.toArray());
    }

    @Override
    public AppResponse getFinishTask() {
        List<TaskEntity> finishedTaskList= null;
        long code=0l;
        String message="";
        try {
            //String login = this.getLogin();
            String login ="ab";
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            HistoryService historyService = defaultProcessEngine.getHistoryService();
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> finishedList = historicProcessInstanceQuery.startedBy(login).finished().list();
            finishedTaskList = new ArrayList<>();
            if(null!=finishedList && finishedList.size()>0){
                for(int i=0;i<finishedList.size();i++){
                    HistoricProcessInstance historicProcessInstance = finishedList.get(i);
                    String superProcessInstanceId = historicProcessInstance.getId();
                    TaskEntity taskEntity = taskInfoMapper.queryByInstId(superProcessInstanceId);
                    finishedTaskList.add(taskEntity);
                }
            }
            code= ResultCode.SUCCESS.getCode();
            message="查找已完成任务成功！";
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="查找已完成任务失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,finishedTaskList.toArray());
    }

    /**
     * 流程走向定义
     * @param identity
     * @param flag
     * @param variable
     * @param procInstId
     * @return
     */
    public Map<String, Object>  setFlag(String identity,String flag,List<String> userList ,String procInstId) {
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

        Map<String, Object> variables = new HashMap<>();
        variables.put("flag"+flagNo, flag);
        variables.put("handleList"+assiNo, userList);

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

    /**
     * 获取下节点处理人
     * @param userMobile
     * @param message
     * @return
     */
    public Map<String, Object> getNextHandler(String userMobile,String message){
        List<Map<String, Object>> mapList=null;
        Map<String, Object> maps=new HashMap<>();

        /**根据手机号，获取信息**/
        SysUserEntity user = sysUserMapper.queryByTel(userMobile);
        if(null==user){
            message="用户信息失效，请重新登录！";
            throw new RuntimeException(message);
        }
        String identity = user.getIdentity();
        String loginNm = user.getLoginNm();
        Authentication.setAuthenticatedUserId(loginNm);

        maps.put("loginNm",loginNm);

        /**根据身份证，获取信息**/
        if(RoleEnums.ZU_YUAN.getCode().equals(identity)){
            mapList = sysUserMapper.getZuZhang(userMobile, loginNm);
        }else if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
            mapList = sysUserMapper.getJieGan(userMobile,loginNm);
        }else if(RoleEnums.JIE_GAN.getCode().equals(identity)){
            mapList = sysUserMapper.getJieWei(userMobile,loginNm);
        }else if(RoleEnums.JIE_WEI.getCode().equals(identity)){
            mapList = sysUserMapper.getQuGan(userMobile,loginNm);
        }else if(RoleEnums.QU_GAN.getCode().equals(identity)){
            mapList = sysUserMapper.getQuWei(userMobile,loginNm);
        }

        //添加岗位人员信息
        List<String> userList = new ArrayList();
        if(null!=mapList && mapList.size()>0){
            for(int i=0;i<mapList.size();i++){
                Map<String, Object> map = mapList.get(i);
                String nm = (String) map.get("loginNm");
                userList.add(nm);
            }
        }else if(RoleEnums.QU_WEI.getCode().equals(identity)){

        } else{
            message="未找到上报人信息，请确认是否存在！";
            throw new RuntimeException(message);
        }
        /*Map<String, Object> variables = new HashMap<>();
        variables.put("handleList",userList );*/
            /*//inputUser就是在bpmn中Assignee配置的参数
            Map<String, Object> variables = new HashMap<>();
            variables.put("assignee2", ofcId);*/

        maps.put("userList",userList);
        maps.put("message",message);
        maps.put("identity",identity);

        return maps;
    }



}
