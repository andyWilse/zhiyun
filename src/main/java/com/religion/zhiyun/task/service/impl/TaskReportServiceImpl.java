package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.config.TestCommand;
import com.religion.zhiyun.task.dao.ActReProcdefMapper;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.CommentEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate stringRedisTemplate;

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
    public AppResponse launch(TaskEntity taskEntity,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="上报任务发起";
        String procInstId="";
        try {
            //数据校验
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            if(null==sysUserEntity){
                throw new RuntimeException("用户已过期，请重新登录！");
            }
            String area = taskEntity.getArea();
            String town = taskEntity.getTown();
            String province = taskEntity.getProvince();
            String areaUs =sysUserEntity.getArea();
            if(null!=areaUs && !areaUs.isEmpty()){
                if(!areaUs.equals(area)){
                    throw new RuntimeException("请先选择用户管辖区！");
                }
            }
            String townUs =sysUserEntity.getTown();
            if(null!=townUs && !townUs.isEmpty()){
                if(!townUs.equals(town)){
                    throw new RuntimeException("请先选择用户管辖街道！");
                }
            }
            String relVenuesId = taskEntity.getRelVenuesId();
            if(null!=relVenuesId && !relVenuesId.isEmpty()){
                String[] relVenues = relVenuesId.split(",");
                for(int i=0;i<relVenues.length;i++){
                    String relVenue = relVenues[i];
                    //任务处理
                    Map<String, Object> nextHandler = this.getNextHandler(loginNm, message,relVenue);
                    if(null==nextHandler){
                        throw new RuntimeException("下节点处理人信息丢失！");
                    }
                    List<String> userList = (List<String>) nextHandler.get("userList");
                    Map<String, Object> variables = new HashMap<>();
                    if(null!=userList && userList.size()>0){
                        variables.put("handleList2",userList );
                    }else{
                        throw new RuntimeException("无相关处理人，请重新确认！");
                    }

                    /**start**/
                    //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
                    ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
                    RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
                    ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode(),variables);
                    String processInstanceId = processInstance.getProcessInstanceId();
                    /**end**/
                    //taskService.setAssignee("assignee1",loginNm);
                    //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
                    TaskQuery taskQuery = taskService.createTaskQuery();
                    Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
                    procInstId=tmp.getProcessInstanceId();
                    taskService.complete(tmp.getId(),variables);
                    //发起人
                    taskInfoMapper.updateHiActinst(loginNm,procInstId);

                    //保存任务信息
                    taskEntity.setRelVenuesId(relVenue);
                    taskEntity.setLaunchPerson(loginNm);
                    taskEntity.setLaunchTime(TimeTool.getYmdHms());
                    taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
                    taskEntity.setProcInstId(procInstId);
                    taskEntity.setFlowType("01");
                    taskInfoMapper.addTask(taskEntity);
                }
            }else{
                throw new RuntimeException("场所信息为空，请先选择场所！");
            }
            code=ResultCode.SUCCESS.getCode();
            message="任务发起申请，任务开始！";
            log.info(message);

        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e){
            message="未知错误，请联系管理员！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse report(String procInstId, String handleResults, String feedBack, String picture,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="上报任务上报失败！";
        String loginNm ="";
        try {
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            Map<String, Object> nextHandler = this.getNextHandler(loginNm, message,"");
            if(null==nextHandler){
                message="下节点处理人信息丢失！";
                throw new RuntimeException(message);
            }
            //String loginNm = (String) nextHandler.get("loginNm");
            String identity = (String) nextHandler.get("identity");
            List<String> userList = (List<String>) nextHandler.get("userList");

            //根据角色信息获取自己的待办 act_ru_task
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理自己的待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        flag=false;
                        Map<String, Object> variables = this.setFlag(identity, "go", userList, procInstId);
                        variables.put("isSuccess", true);
                        //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                        taskService.setVariableLocal(item.getId(),"isSuccess",false);
                        //增加审批备注
                        CommentEntity en =new CommentEntity();
                        en.setFeedBack(feedBack);
                        en.setHandleResults(handleResults);
                        en.setPicture(picture);
                        taskService.addComment(item.getId(),item.getProcessInstanceId(), JsonUtils.beanToJson(en));
                        //完成此次审批。由下节点审批
                        taskService.complete(item.getId(), variables);
                    }
                }
                //任务已被处理
                if(flag){
                    throw new RuntimeException("任务已被他人上报！");
                }
            }else{
                throw new RuntimeException("任务已被他人处理，流程已结束！！");
            }
            log.info("任务id："+procInstId+" 上报");
            code= ResultCode.SUCCESS.getCode();
            message="上报流程上报成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="上报流程上报失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="任务处理";
        String loginNm ="";
        try {
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);

            /**根据手机号，获取信息**/
            SysUserEntity user = sysUserMapper.queryByName(loginNm);
            if(null==user){
                message="用户信息失效，请重新登录！";
                throw new RuntimeException(message);
            }
            String identity = user.getIdentity();
            //根据角色信息获取自己的待办
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        flag=false;
                        Map<String, Object> variables = this.setFlag(identity, "end",null,procInstId);
                        variables.put("nrOfCompletedInstances", 1);
                        variables.put("isSuccess", true);

                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //增加审批备注
                        CommentEntity en =new CommentEntity();
                        en.setFeedBack(feedBack);
                        en.setHandleResults(handleResults);
                        en.setPicture(picture);
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),JsonUtils.beanToJson(en));
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id："+procInstId+" 已处理，流程结束！");

                        //更新处理结果
                        TaskEntity taskEntity=new TaskEntity();
                        taskEntity.setHandlePerson(loginNm);
                        taskEntity.setHandleTime(TimeTool.getYmdHms());
                        taskEntity.setHandleResults(handleResults);
                        taskEntity.setProcInstId(procInstId);
                        taskInfoMapper.updateTask(taskEntity);
                    }
                }
                //任务已被处理
                if(flag){
                    throw new RuntimeException("任务已被他人处理，流程已结束！");
                }
            }else{
                throw new RuntimeException("任务已被他人处理，流程已结束！");
            }

            code= ResultCode.SUCCESS.getCode();
            message="上报流程处理成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="上报流程处理失败！";
            e.printStackTrace();
        }
        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return new AppResponse(code,message);
    }

    @Override
    public AppResponse getTaskNum(String token) {
        AppResponse res=new AppResponse();
        Map<String, Object> map = null;
        long code=ResultCode.FAILED.getCode();
        String message="任务数量统计";
        try {
            String login = this.getLogin(token);
            map = taskInfoMapper.getTaskNum(login);

            code= ResultCode.SUCCESS.getCode();
            message="任务数量统计成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
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
    public AppResponse getUnTask(Integer page, Integer size,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="查找未完成任务！";
        List<Map<String,Object>> unfinishedTaskList=new ArrayList<>();
        Long totals=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String loginNm = this.getLogin(token);
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            HistoryService historyService = defaultProcessEngine.getHistoryService();

            List<String> idList= new ArrayList<>();
            //发起人
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> unfinishedListStart = historicProcessInstanceQuery.startedBy(loginNm).unfinished().list();
            if(null!=unfinishedListStart && unfinishedListStart.size()>0){
                for(int i=0;i<unfinishedListStart.size();i++){
                    HistoricProcessInstance historicProcessInstance = unfinishedListStart.get(i);
                    String superProcessInstanceId = historicProcessInstance.getId();
                    idList.add(superProcessInstanceId);
                }
            }
            //处理人
            HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
            List<HistoricTaskInstance> unfinishedList  =historicTaskInstanceQuery.taskAssignee(loginNm).unfinished().list();
            if(null!=unfinishedList && unfinishedList.size()>0){
                for(int i=0;i<unfinishedList.size();i++){
                    HistoricTaskInstance historicTaskInstance = unfinishedList.get(i);
                    String superProcessInstanceId = historicTaskInstance.getProcessInstanceId();
                    idList.add(superProcessInstanceId);
                }
            }
            //封装
            int total = idList.size();
            totals=new Long(total);
            if(null!=idList && idList.size()>0){
                unfinishedTaskList= taskInfoMapper.queryByInstId(page,size,idList);
            }
            code= ResultCode.SUCCESS.getCode();
            message="查找未完成任务成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message="查找未完成任务失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,totals,unfinishedTaskList.toArray());
    }

    @Override
    public AppResponse getFinishTask(Integer page, Integer size,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="查找已完成任务！";
        List<Map<String,Object>> finishedTaskList= new ArrayList<>();
        Long totals=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String loginNm = this.getLogin(token);

            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            HistoryService historyService = defaultProcessEngine.getHistoryService();
            List<String> idList= new ArrayList<>();
            //发起人
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> finishedListStart = historicProcessInstanceQuery.startedBy(loginNm).finished().list();
            if(null!=finishedListStart && finishedListStart.size()>0){
                for(int i=0;i<finishedListStart.size();i++){
                    HistoricProcessInstance historicProcessInstance = finishedListStart.get(i);
                    String superProcessInstanceId = historicProcessInstance.getId();
                    idList.add(superProcessInstanceId);
                }
            }
            //处理人
            HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
            List<HistoricTaskInstance> finishedList  =historicTaskInstanceQuery.taskAssignee(loginNm).finished().list();
            if(null!=finishedList && finishedList.size()>0){
                for(int i=0;i<finishedList.size();i++){
                    HistoricTaskInstance historicTaskInstance = finishedList.get(i);
                    String superProcessInstanceId = historicTaskInstance.getProcessInstanceId();
                    idList.add(superProcessInstanceId);
                }
            }
            //封装
            int total = idList.size();
            totals=new Long(total);
            if(null!=idList && idList.size()>0){
                finishedTaskList = taskInfoMapper.queryByInstId(page,size,idList);
            }
            code= ResultCode.SUCCESS.getCode();
            message="查找已完成任务成功！";
        } catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="查找已完成任务失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,totals,finishedTaskList.toArray());
    }

    /**
     * 流程走向定义
     * @param identity
     * @param flag
     * @param userList
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
        if("go".equals(flag)){
            if(null!=userList && userList.size()>0){
                variables.put("handleList"+assiNo, userList);
            }else{
                throw new RuntimeException("无相关处理人，请重新确认！");
            }
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


    public List<Map<String,Object>> queryTasks(){
        return actReProcdefMapper.queryTasks();
    }

    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        //SysUserEntity entity = TokenUtils.getToken();
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        //String loginNm = entity.getLoginNm();
        return loginNm;
    }

    /**
     * 获取下节点处理人
     * @param loginNm
     * @param message
     * @return
     */
    public Map<String, Object> getNextHandler(String loginNm,String message,String venusId){
        List<Map<String, Object>> mapList=new ArrayList<>();
        Map<String, Object> maps=new HashMap<>();

        /**根据手机号，获取信息**/
        SysUserEntity user = sysUserMapper.queryByName(loginNm);
        if(null==user){
            message="用户信息失效，请重新登录！";
            throw new RuntimeException(message);
        }
        String identity = user.getIdentity();
        String userMobile = user.getUserMobile();
        Authentication.setAuthenticatedUserId(loginNm);

        //maps.put("loginNm",loginNm);

        /**根据身份证，获取信息**/
        if(RoleEnums.ZU_YUAN.getCode().equals(identity)){
            if(venusId==null || venusId.isEmpty()){
                throw new RuntimeException("请先选择场所！");
            }
            mapList = sysUserMapper.getZuZhang(userMobile, "",venusId);
        }else if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
            mapList = sysUserMapper.getJieGan(userMobile,"");
        }else if(RoleEnums.JIE_GAN.getCode().equals(identity)){
            mapList = sysUserMapper.getJieWei(userMobile,"");
        }else if(RoleEnums.JIE_WEI.getCode().equals(identity)){
            mapList = sysUserMapper.getQuGan(userMobile,"");
        }else if(RoleEnums.QU_GAN.getCode().equals(identity)){
            mapList = sysUserMapper.getQuWei(userMobile,"");
        }

        //添加岗位人员信息
        List<String> userList = new ArrayList();
        if(null!=mapList && mapList.size()>0){
            for(int i=0;i<mapList.size();i++){
                Map<String, Object> map = mapList.get(i);
                //String nm = (String) map.get("loginNm");
                String userMo = (String) map.get("userMobile");
                userList.add(userMo);
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
