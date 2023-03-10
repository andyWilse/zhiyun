package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.config.TestCommand;
import com.religion.zhiyun.task.dao.ActReProcdefMapper;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
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
        //?????????
        DeploymentBuilder builder=  repositoryService.createDeployment();
        builder.addClasspathResource(TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath());
        String id = builder.deploy().getId();
        repositoryService.setDeploymentKey(id,TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode());
        log.info(TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath()+"??????id???"+id+",????????????");
        return id;
    }

    @Override
    @Transactional
    public AppResponse launch(TaskEntity taskEntity,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="??????????????????";
        String procInstId="";
        try {
            String loginNm = this.getLogin(token);
            Map<String, Object> nextHandler = this.getNextHandler(loginNm, message);
            if(null==nextHandler){
                throw new RuntimeException("?????????????????????????????????");
            }
            List<String> userList = (List<String>) nextHandler.get("userList");
            Map<String, Object> variables = new HashMap<>();
            variables.put("handleList2",userList );

            /**start**/
            //???????????????myProcess_2?????????????????????????????????bpmn??????xml??????????????????????????????
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode(),variables);
            String processInstanceId = processInstance.getProcessInstanceId();
            /**end**/
            //???????????????????????????????????????????????????act_ru_task??????????????????????????????????????????
            TaskQuery taskQuery = taskService.createTaskQuery();
            Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
            procInstId=tmp.getProcessInstanceId();
            //taskService.setAssignee("assignee2",userNbr);
            taskService.complete(tmp.getId(),variables);

            //??????????????????
            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setLaunchTime(new Date());
            taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
            taskEntity.setProcInstId(procInstId);
            taskEntity.setFlowType("01");
            taskInfoMapper.addTask(taskEntity);

            code=ResultCode.SUCCESS.getCode();
            message="??????id???"+processInstanceId+" ??????????????????????????????";
            log.info(message);

        } catch (RuntimeException e) {
            code=ResultCode.FAILED.getCode();
            message=e.getMessage();
            //throw new RuntimeException(message) ;
        }catch (Exception e){
            code=ResultCode.FAILED.getCode();
            message="????????????????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse report(String procInstId, String handleResults, String feedBack, String picture,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="???????????????????????????";
        String loginNm ="";
        try {
            loginNm = this.getLogin(token);
            Map<String, Object> nextHandler = this.getNextHandler(loginNm, message);
            if(null==nextHandler){
                message="?????????????????????????????????";
                throw new RuntimeException(message);
            }
            //String loginNm = (String) nextHandler.get("loginNm");
            String identity = (String) nextHandler.get("identity");
            List<String> userList = (List<String>) nextHandler.get("userList");

            //??????????????????????????????????????? act_ru_task
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //?????????????????????
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        Map<String, Object> variables = this.setFlag(identity, "go", userList, procInstId);
                        variables.put("isSuccess", true);
                        //????????????????????????myListener1????????????????????????????????????????????????
                        taskService.setVariableLocal(item.getId(),"isSuccess",false);
                        //??????????????????
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),"??????");
                        //???????????????????????????????????????
                        taskService.complete(item.getId(), variables);
                    }
                }
            }
            log.info("??????id???"+procInstId+" ??????");
            code= ResultCode.SUCCESS.getCode();
            message="?????????????????????????????????id(????????????)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message);
        }catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="???????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="????????????";
        String loginNm ="";
        try {
            loginNm = this.getLogin(token);
            Map<String, Object> nextHandler = this.getNextHandler(loginNm, message);
            if(null==nextHandler){
                message="?????????????????????????????????";
                throw new RuntimeException(message);
            }
            //String loginNm = (String) nextHandler.get("loginNm");
            String identity = (String) nextHandler.get("identity");
            List<String> userList = (List<String>) nextHandler.get("userList");

            //???????????????????????????????????????
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //????????????
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        Map<String, Object> variables = this.setFlag(identity, "end",userList,procInstId);
                        variables.put("nrOfCompletedInstances", 1);
                        variables.put("isSuccess", true);

                        //????????????????????????myListener1??????????????????
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //??????????????????
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),handleResults);
                        //???????????????????????????????????????endEvent???????????????
                        taskService.complete(item.getId(), variables);
                        log.info("??????id???"+procInstId+" ???????????????????????????");

                        //??????????????????
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
            message="?????????????????????????????????id(????????????)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="???????????????????????????";
            e.printStackTrace();
        }
        log.info("??????id???"+procInstId+" ???????????????????????????");
        return new AppResponse(code,message);
    }

    @Override
    public RespPageBean getTasking(Integer page, Integer size,String taskName, String taskContent,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "?????????????????????";
        List<TaskEntity> taskEntities = null;
        try {
            if(page<1){
                page=1;
            } else if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String loginNm = this.getLogin(token);
            taskEntities = taskInfoMapper.queryTasks(page, size, taskName, taskContent,loginNm);
            code= ResultCode.SUCCESS.getCode();
            message= "???????????????????????????";
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RespPageBean(code,message,taskEntities.toArray());
    }

    @Override
    public AppResponse getTaskNum(String token) {
        AppResponse res=new AppResponse();
        Map<String, Object> map = null;
        long code=ResultCode.FAILED.getCode();
        String message="??????????????????";
        try {
            String login = this.getLogin(token);
            map = taskInfoMapper.getTaskNum(login);

            code= ResultCode.SUCCESS.getCode();
            message="???????????????????????????";
        }catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="???????????????????????????";
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
        String message="????????????????????????";
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
            //?????????
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> unfinishedListStart = historicProcessInstanceQuery.startedBy(loginNm).unfinished().list();
            if(null!=unfinishedListStart && unfinishedListStart.size()>0){
                for(int i=0;i<unfinishedListStart.size();i++){
                    HistoricProcessInstance historicProcessInstance = unfinishedListStart.get(i);
                    String superProcessInstanceId = historicProcessInstance.getId();
                    idList.add(superProcessInstanceId);
                }
            }
            //?????????
            HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
            List<HistoricTaskInstance> unfinishedList  =historicTaskInstanceQuery.taskAssignee(loginNm).unfinished().list();
            if(null!=unfinishedList && unfinishedList.size()>0){
                for(int i=0;i<unfinishedList.size();i++){
                    HistoricTaskInstance historicTaskInstance = unfinishedList.get(i);
                    String superProcessInstanceId = historicTaskInstance.getProcessInstanceId();
                    idList.add(superProcessInstanceId);
                }
            }
            //??????
            int total = idList.size();
            totals=new Long(total);
            if(null!=idList && idList.size()>0){
                unfinishedTaskList= taskInfoMapper.queryByInstId(page,size,idList);
            }
            code= ResultCode.SUCCESS.getCode();
            message="??????????????????????????????";
        }catch (RuntimeException r){
            message=r.getMessage();
           // throw new RuntimeException(message) ;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="??????????????????????????????";
            e.printStackTrace();
        }

        return new AppResponse(code,message,totals,unfinishedTaskList.toArray());
    }

    @Override
    public AppResponse getFinishTask(Integer page, Integer size,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="????????????????????????";
        List<Map<String,Object>> finishedTaskList= new ArrayList<>();
        Long totals=0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String loginNm = this.getLogin(token);
           /* SysUserEntity sysUserEntity = sysUserMapper.queryByTel(mobil);
            if(null==sysUserEntity){
                throw new RuntimeException("??????????????????");
            }
            String loginNm = sysUserEntity.getLoginNm();*/
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            HistoryService historyService = defaultProcessEngine.getHistoryService();
            List<String> idList= new ArrayList<>();
            //?????????
            HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
            List<HistoricProcessInstance> finishedListStart = historicProcessInstanceQuery.startedBy(loginNm).finished().list();
            if(null!=finishedListStart && finishedListStart.size()>0){
                for(int i=0;i<finishedListStart.size();i++){
                    HistoricProcessInstance historicProcessInstance = finishedListStart.get(i);
                    String superProcessInstanceId = historicProcessInstance.getId();
                    idList.add(superProcessInstanceId);
                }
            }
            //?????????
            HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
            List<HistoricTaskInstance> finishedList  =historicTaskInstanceQuery.taskAssignee(loginNm).finished().list();
            if(null!=finishedList && finishedList.size()>0){
                for(int i=0;i<finishedList.size();i++){
                    HistoricTaskInstance historicTaskInstance = finishedList.get(i);
                    String superProcessInstanceId = historicTaskInstance.getProcessInstanceId();
                    idList.add(superProcessInstanceId);
                }
            }
            //??????
            int total = idList.size();
            totals=new Long(total);
            if(null!=idList && idList.size()>0){
                finishedTaskList = taskInfoMapper.queryByInstId(page,size,idList);
            }
            code= ResultCode.SUCCESS.getCode();
            message="??????????????????????????????";
        } catch (RuntimeException r){
            message=r.getMessage();
            //throw new RuntimeException(message) ;
        }catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="??????????????????????????????";
            e.printStackTrace();
        }
        return new AppResponse(code,message,totals,finishedTaskList.toArray());
    }

    /**
     * ??????????????????
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
        variables.put("handleList"+assiNo, userList);
        return variables;
    }

    public void getNode(String actId,String key,Task task) {
        String taskId = "";
        String tagNode = "jieWeiFlow";
        //??????????????????id
        //Task task = taskService.createTaskQuery().processInstanceId(actId).singleResult();
        System.out.println("??????id???" + task.getId());
        taskId = task.getId();

        //????????????
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
        /*for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                //??????????????????
                UserTask userTask = (UserTask) flowElement;//????????????
                if (userTask.getName().equals(key)) {
                    tagNode = userTask.getId();
                    break;
                }
            }
        }*/
        //??????
        managementService.executeCommand(new TestCommand(taskId, tagNode));
    }

    /**
     *  ?????????????????????????????????
     * @param processId:????????????ID
     * @param Assignee:?????????ID
     *
     */
    public void setFirstTaskAssignee(String processId,String Assignee){
        //????????????????????????????????????
        List<Task> tasks = taskService.createTaskQuery().list();
        for (Task t:tasks) {
            //????????????????????????????????????
            if (t.getProcessInstanceId().equals(processId)){
                //???????????????  ??????????????????ID????????????ID???
                taskService.setAssignee(t.getId(),Assignee);
            }
        }
    }


    public List<Map<String,Object>> queryTasks(){
        return actReProcdefMapper.queryTasks();
    }

    /**
     * ???????????????
     * @return
     */
    public String getLogin(String token){
        //SysUserEntity entity = TokenUtils.getToken();
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("?????????????????????????????????");
        }
        //String loginNm = entity.getLoginNm();
        return loginNm;
    }

    /**
     * ????????????????????????
     * @param loginNm
     * @param message
     * @return
     */
    public Map<String, Object> getNextHandler(String loginNm,String message){
        List<Map<String, Object>> mapList=null;
        Map<String, Object> maps=new HashMap<>();

        /**??????????????????????????????**/
        SysUserEntity user = sysUserMapper.queryByName(loginNm);
        if(null==user){
            message="???????????????????????????????????????";
            throw new RuntimeException(message);
        }
        String identity = user.getIdentity();
        String userMobile = user.getUserMobile();
        Authentication.setAuthenticatedUserId(loginNm);

        //maps.put("loginNm",loginNm);

        /**??????????????????????????????**/
        if(RoleEnums.ZU_YUAN.getCode().equals(identity)){
            mapList = sysUserMapper.getZuZhang(userMobile, "");
        }else if(RoleEnums.ZU_ZHANG.getCode().equals(identity)){
            mapList = sysUserMapper.getJieGan(userMobile,"");
        }else if(RoleEnums.JIE_GAN.getCode().equals(identity)){
            mapList = sysUserMapper.getJieWei(userMobile,"");
        }else if(RoleEnums.JIE_WEI.getCode().equals(identity)){
            mapList = sysUserMapper.getQuGan(userMobile,"");
        }else if(RoleEnums.QU_GAN.getCode().equals(identity)){
            mapList = sysUserMapper.getQuWei(userMobile,"");
        }

        //????????????????????????
        List<String> userList = new ArrayList();
        if(null!=mapList && mapList.size()>0){
            for(int i=0;i<mapList.size();i++){
                Map<String, Object> map = mapList.get(i);
                String nm = (String) map.get("loginNm");
                userList.add(nm);
            }
        }else if(RoleEnums.QU_WEI.getCode().equals(identity)){

        } else{
            message="???????????????????????????????????????????????????";
            throw new RuntimeException(message);
        }
        /*Map<String, Object> variables = new HashMap<>();
        variables.put("handleList",userList );*/
            /*//inputUser?????????bpmn???Assignee???????????????
            Map<String, Object> variables = new HashMap<>();
            variables.put("assignee2", ofcId);*/

        maps.put("userList",userList);
        maps.put("message",message);
        maps.put("identity",identity);

        return maps;
    }



}
