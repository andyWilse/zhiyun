package com.religion.zhiyun.task.service.impl;

import cn.hutool.core.date.DateTime;
import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.entity.NotifiedEntity;
import com.religion.zhiyun.interfaces.entity.huawei.FeeInfo;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.sys.base.dao.SysBaseMapper;
import com.religion.zhiyun.sys.base.enums.SysBaseEnum;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskActAssigneeMapper;
import com.religion.zhiyun.task.dao.TaskActInstMapper;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.ActInstEntity;
import com.religion.zhiyun.task.entity.AssEntity;
import com.religion.zhiyun.task.entity.CommentEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskAiWarnService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.TaskActEnums;
import com.religion.zhiyun.utils.enums.CallEnums;
import com.religion.zhiyun.utils.enums.EventParamCode;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.sms.call.VoiceCall;
import com.religion.zhiyun.utils.sms.sm.MessageSend;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Slf4j
@Service
public class TaskAiWarnServiceImpl implements TaskAiWarnService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TaskService taskService;
    @Autowired
    TaskInfoMapper taskInfoMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RmStaffInfoMapper rmStaffInfoMapper;
    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;
    @Autowired
    private EventNotifiedMapper eventNotifiedMapper;
    @Autowired
    SysBaseMapper sysBaseMapper;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskActInstMapper taskActInstMapper;
    @Autowired
    private TaskActAssigneeMapper taskActAssigneeMapper;

    @Override
    public AppResponse launch(TaskEntity taskEntity, List<String> userList, String loginNm) {
        String procInstId="";
        try {
            Authentication.setAuthenticatedUserId(loginNm);
            Map<String, Object> variables = new HashMap<>();
            //inputUser就是在bpmn中Assignee配置的参数
            if(null!=userList && userList.size()>0){
                variables.put("reviewer",userList );
            }else{
                throw new RuntimeException("未找到人工审核相关人员，请重新确认！");
            }
            /**start**/
            //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_WARN_TASK_KEY.getCode(),variables);
            String processInstanceId = processInstance.getProcessInstanceId();
            /**end**/
            //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
            TaskQuery taskQuery = taskService.createTaskQuery();
            Task tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
            procInstId=tmp.getProcessInstanceId();
            //发起人
            taskInfoMapper.updateHiActinst(loginNm,procInstId);

            //1.保存任务信息
            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setLaunchTime(TimeTool.getYmdHms());
            taskEntity.setProcInstId(procInstId);
            taskEntity.setHandleResults(TaskActEnums.AI_WARN_STATE_00.getCode());
            taskEntity.setTaskType(TaskParamsEnum.TASK_FLOW_TYPE_05.getName());
            taskEntity.setFlowType(TaskParamsEnum.TASK_FLOW_TYPE_05.getCode());
            taskInfoMapper.addTask(taskEntity);
            //2.保存节点信息
            //2.1.保存本节点信息
            Boolean nowFlag = this.saveActNode(
                    TaskActEnums.AI_WARN_NODE_01.getCode(),
                    loginNm,
                    new DateTime(),
                    loginNm,
                    new DateTime(),
                    "",
                    TaskActEnums.AI_NODE_STATE_01.getCode(),
                    procInstId,
                    "",
                    "",
                    null
            );
            if(nowFlag){
                throw new RuntimeException("节点信息保存错误，请联系管理员！");
            }
            //2.2.保存下节点信息
            Boolean nextFlag = this.saveActNode(
                    TaskActEnums.AI_WARN_NODE_02.getCode(),
                    JsonUtils.listTOJson(userList),
                    new DateTime(),
                    "",
                    null,
                    "",
                    TaskActEnums.AI_NODE_STATE_00.getCode(),
                    procInstId,
                    "",
                    "",
                    userList
            );
            if(nextFlag){
                throw new RuntimeException("节点信息保存错误，请联系管理员！");
            }

            log.info("任务id："+processInstanceId+" 发起申请，任务开始！");

        } catch (RuntimeException r) {
            r.printStackTrace();
            return new AppResponse(ResultCode.FAILED.getCode(),"AI预警任务发起失败！");
        }catch (Exception e) {
            e.printStackTrace();
        }

        return new AppResponse(ResultCode.SUCCESS.getCode(),"AI预警任务发起成功！","流程id(唯一标识)procInstId:"+procInstId);

    }

    @Override
    public AppResponse review(String review,String procInstId, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="预警流程人工审核处理失败！";
        //审核通过，通知下一岗人员并继续流程
        try {
            //1.通知
            List<String> userList = this.addNotifiedParty(procInstId);
            //2.继续流程
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            //处理自己的待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                String taskId="";
                Boolean flag=true;
                for (Task item : T) {
                    taskId=item.getId();
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        flag=false;
                        Map<String, Object> variables = new HashMap<>();

                        variables.put("taskCompleted", 1);
                        variables.put("isSuccess", true);
                        variables.put("reviewFlag","go" );
                        variables.put("managerList", userList);

                        //设置本地参数。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                        //taskService.setVariableLocal(item.getId(),"isSuccess",false);
                        //增加审批备注
                        CommentEntity en =new CommentEntity();
                        en.setReview(review);
                        taskService.addComment(item.getId(),item.getProcessInstanceId(), JsonUtils.beanToJson(en));
                        //完成此次审批。由下节点审批
                        taskService.complete(item.getId(), variables);
                    }
                }

                //任务已被处理
                if(flag){
                    throw new RuntimeException("流程异常，请联系管理员！");
                }

                //3.更新系统数据
                //3.1.参数封装
                Map<String, Object> map=new HashMap<>();
                map.put("procInstId",procInstId);
                map.put("loginNm",loginNm);
                //任务
                map.put("taskResult",TaskActEnums.AI_WARN_STATE_01.getCode());
                //更新节点
                map.put("curState",TaskActEnums.AI_NODE_STATE_01.getCode());
                map.put("taskId",taskId);
                map.put("lastState",TaskActEnums.AI_NODE_STATE_00.getCode());
                map.put("actComment",review);
                //新增节点
                map.put("actReceiver","");
                map.put("nextNode",TaskActEnums.AI_WARN_NODE_03.getCode());
                map.put("userList",JsonUtils.listTOJson(userList));
                map.put("nextState",TaskActEnums.AI_NODE_STATE_00.getCode());

                //3.2.更新
                this.updateAiWarnInfo(map);

            }else{
                throw new RuntimeException("流程异常，请联系管理员！！！");
            }
            code=ResultCode.SUCCESS.getCode();
            message="预警流程人工审核处理成功！";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return new AppResponse(ResultCode.FAILED.getCode(),r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(ResultCode.FAILED.getCode(),e.getMessage());

        }
        return new AppResponse(code,message);

    }
    /**
     * 预警通知保存
     * @param procInstId
     *
     *ai预警推送，监管人员按紧急普通区分，紧急的默认推送所有监管人员，普通的默认推送给街镇、三人驻堂
     *教职人员，按预警的类别进行推送，比如火警可以推送教职人员，但是人脸不一定推送，具体推送什么类型的等跟业主进行确认
     * @return
     */
    public List<String> addNotifiedParty(String procInstId) {

        /*** 1.参数获取 ***/
        List<Map<String, Object>> eventVe = rmEventInfoMapper.getEventVe(procInstId);
        if(eventVe.size()<1 || eventVe.size()>1){
            throw new RuntimeException("预警信息异常：“+eventId+”，请联系管理员！");
        }

        Map<String, Object> mapCall = eventVe.get(0);
        String eventType = (String) mapCall.get("eventType");
        Integer relVenuesId = (Integer) mapCall.get("venuesId");
        String emergencyLevel = (String) mapCall.get("eventLevel");
        String venuesAddres = (String) mapCall.get("venuesAddres");
        String venuesName = (String) mapCall.get("venuesNm");
        Integer eventId = (Integer) mapCall.get("eventId");
        String message = EventParamCode.getMessage(eventType);
        //短信模板
        String contents="【智云科技】您好！位于"+venuesAddres+"的"+venuesName+",触发“"+message+"”预警，请您立刻前去处理！";
        if(EventParamCode.EVENT_TYPE_04.getCode().equals(eventType)){
            contents="【智云科技】您好！位于"+venuesAddres+"的"+venuesName+",发现“集聚”活动，请您前往现场核实活动内容！";
        }else if(EventParamCode.EVENT_TYPE_06.getCode().equals(eventType)) {
            contents = "【智云科技】您好！位于" + venuesAddres + "的" + venuesName + ",发现摄像头“画面异常”，疑似摄像头被移动位置或遮挡，请您立即前往现场核实！";
        }
        /*** 2.处理人员查询 ***/
        List<Map<String, Object>> userList = new ArrayList<>();
        //获取通知对象
        if ("01".equals(emergencyLevel) || EventParamCode.EVENT_TYPE_01.getCode().equals(eventType)) {
            //根据场所获取场所有相关人员
            userList = sysUserMapper.getAllByVenues(relVenuesId);
        } else {
            //根据场所获取场所三人驻堂、街干
            userList = sysUserMapper.getJgByVenues(relVenuesId);
        }

        /*** 3.预警通知 ***/
        NotifiedEntity notifiedEntity = new NotifiedEntity();
        List<String> userNextList = new ArrayList<>();//节点处理人员
        boolean tmFlag = GeneTool.calendarCompare("08:30", "17:30");
        //3.1.监管人员处理
        String user = "";//监管
        if (null != userList && userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                Map<String, Object> map = userList.get(i);
                String userMobile = (String) map.get("userMobile");
                userNextList.add(userMobile);//下节点流程处理人员
                user = user + userMobile + ",";//通知人员
            }
            notifiedEntity.setNotifiedUser(user);
            System.out.println(contents + ":共发送" + (userList.size()) + "条短信");
        } else {
            throw new RuntimeException("该场所内尚未添加相关成员！");
        }

        //3.2.管理人员
        String manager = "";// 管理
        if (EventParamCode.EVENT_TYPE_01.getCode().equals(eventType)) {
            //根据场所获取场所相关的管理人员
            manager = rmStaffInfoMapper.getManagerByVenuesId(relVenuesId);
            if (null != manager && !manager.isEmpty()) {
                notifiedEntity.setNotifiedManager(manager);
                //短信通知
                String[] split = manager.split(",");
                for (int i = 0; i < split.length; i++) {
                    String managerMobile = split[i];
                    userNextList.add(managerMobile);//下节点流程处理人员
                }
                System.out.println(contents + ":共发送" + (split.length) + "条短信");
            } else {
                throw new RuntimeException("该场所内尚未添加职员信息！");
            }
        }

        //查询开关，通知
        String openFlag = sysBaseMapper.getOpenState(SysBaseEnum.SEND_MESSAGE_SWITCH.getCode());
        if ("1".equals(openFlag)) {//1-开；0-关 （短信开关）
            //3.1.1.电话通知
            if (tmFlag && EventParamCode.EVENT_TYPE_01.getCode().equals(eventType)) {
                mapCall.put("phone", user + "," + manager);
                String sessionId = VoiceCall.voiceCall(mapCall);
                //保存数据
                FeeInfo feeInfo = new FeeInfo();
                feeInfo.setSessionId(sessionId);
                feeInfo.setEventType(CallEnums.fee.getCode());
                feeInfo.setRefEventId(String.valueOf(eventId));
                eventNotifiedMapper.addCall(feeInfo);

            }
            //3.1.2.短信通知
            MessageSend.sendSmsMass(contents, userNextList);
        }

        /*** 4.保存通知 ***/
        String event = (String) mapCall.get("event");
        notifiedEntity.setEventType(event);//内容
        notifiedEntity.setRefEventId(eventId);
        notifiedEntity.setNotifiedFlag(EventParamCode.NOTIFIED_FLAG_03.getCode());
        notifiedEntity.setNotifiedTime(new Date());
        eventNotifiedMapper.addNotified(notifiedEntity);
        
        return userNextList;
    }

    @Override
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="基层干部反馈处置流程失败！";
        String loginNm ="";
        try {
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);

            //根据角色信息获取自己的待办
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        //1.反馈处置完继续流程
                        flag=false;
                        Map<String, Object> variables = new HashMap<String, Object>();
                        variables.put("taskCompleted", 1);
                        variables.put("isSuccess", true);
                        //获取下节点处置人
                        List<String> finalList = sysUserMapper.getReview(RoleEnums.FINAL_REVIEW.getCode(), RoleEnums.OF_ID_2.getCode());
                        if(finalList.size()<1 || null==finalList){
                            throw new RuntimeException("未找到评审岗处理人，请联系管理员！");
                        }
                        variables.put("finaler", finalList);
                        variables.put("manageFlag", "go");

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

                        //2.更新处理结果
                        /*Map<String, Object> taskEven = taskInfoMapper.getEvTaDetail(procInstId);
                        int backFlag=0;
                        String currentState= TaskActEnums.AI_NODE_STATE_00.getCode();
                        String actState= TaskActEnums.AI_NODE_STATE_01.getCode();
                        if(null!=taskEven){
                            backFlag = taskEven.get("backFlag")==null?0:(int) taskEven.get("backFlag");
                            if(1==backFlag){
                                currentState= TaskActEnums.AI_WARN_STATE_02.getCode();
                                actState= TaskActEnums.AI_WARN_STATE_02.getCode();
                            }
                        }*/
                        String currentState= TaskActEnums.AI_NODE_STATE_00.getCode();
                        String actState= TaskActEnums.AI_NODE_STATE_01.getCode();

                        //2.1参数封装
                        Map<String, Object> map=new HashMap<>();
                        map.put("procInstId",procInstId);
                        map.put("loginNm",loginNm);
                        //2.2.1任务
                        map.put("taskResult",TaskActEnums.AI_WARN_STATE_02.getCode());

                        //2.2.2更新节点
                        map.put("curState",actState);
                        map.put("taskId",item.getId());
                        map.put("lastState",currentState);
                        map.put("actComment",handleResults);
                        //2.2.3新增节点
                        map.put("nextNode",TaskActEnums.AI_WARN_NODE_04.getCode());
                        map.put("userList",JsonUtils.listTOJson(finalList));
                        map.put("nextState",TaskActEnums.AI_NODE_STATE_00.getCode());
                        map.put("actReceiver","");

                        //2.2.更新系统数据
                        this.updateAiWarnInfo(map);
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
            message="基层反馈处理成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }
        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return new AppResponse(code,message);
    }


    @Override
    public AppResponse evaluate(String procInstId,  String evaluation, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="终审用户评价通过流程失败！";
        try {
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);

            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        //1.反馈处置完继续流程
                        flag=false;
                        Map<String, Object> variables = new HashMap<String, Object>();
                        variables.put("taskCompleted", 1);
                        variables.put("isSuccess", true);
                        variables.put("finalFlag", "end");
                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //增加审批备注
                        CommentEntity en =new CommentEntity();
                        en.setEvaluation(evaluation);
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),JsonUtils.beanToJson(en));
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id："+procInstId+" 已处理，流程结束！");

                        //2.更新处理结果
                        //2.1参数封装
                        Map<String, Object> map=new HashMap<>();
                        map.put("procInstId",procInstId);
                        map.put("loginNm",loginNm);
                        //2.2.1.任务
                        map.put("taskResult",TaskActEnums.AI_WARN_STATE_03.getCode());//评价通过
                        //2.2.2.预警
                        Map<String, Object> taskEven = taskInfoMapper.getEvTaDetail(procInstId);
                        Integer eventId =0;
                        if(null!=taskEven) {
                            eventId = (Integer) taskEven.get("eventId");
                        }else{
                            throw new RuntimeException("预警信息丢失，请联系管理员！");
                        }

                        map.put("eventId",String.valueOf(eventId));
                        map.put("eventState",EventParamCode.EVENT_STATE_01.getCode());
                        map.put("eventResult",EventParamCode.EVENT_HANDLE_1.getCode());

                        //更新本节点信息
                        map.put("curState",TaskActEnums.AI_NODE_STATE_01.getCode());
                        map.put("taskId",item.getId());
                        map.put("lastState",TaskActEnums.AI_NODE_STATE_00.getCode());
                        map.put("actComment",evaluation);
                        //新增下节点信息
                        map.put("nextNode",TaskActEnums.AI_WARN_NODE_05.getCode());
                        map.put("userList","");
                        map.put("nextState",TaskActEnums.AI_NODE_STATE_01.getCode());

                        //修改
                        AppResponse updateResponse = this.updateAiWarnInfo(map);

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
            message="终审用户评价通过处理成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }
        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return new AppResponse(code,message);
    }


    @Override
    public AppResponse backup(String procInstId, String evaluation, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="终审用户回退流程失败！";
        try {
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);

            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                //获取上节点处理人
                List<String> userList = taskInfoMapper.getNodeHandler(procInstId, TaskActEnums.AI_WARN_NODE_03.getEnglishNm());
                String taskId="";
                Boolean flag=true;
                for (Task item : T) {
                    taskId=item.getId();
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        //1.反馈处置完继续流程
                        flag=false;

                        //退回处理
                        this.revoke(procInstId,loginNm,"manage",evaluation);
                        log.info("任务id："+procInstId+" 已退回！");
                    }
                }


                //3.更新处理结果
                //3.1.参数封装
                Map<String, Object> map=new HashMap<>();
                map.put("procInstId",procInstId);
                map.put("loginNm",loginNm);
                //任务
                map.put("backFlag",1);
                map.put("taskResult",TaskActEnums.AI_WARN_STATE_04.getCode());
                //更新节点
                map.put("curState",TaskActEnums.AI_NODE_STATE_01.getCode());
                map.put("taskId",taskId);
                map.put("lastState",TaskActEnums.AI_NODE_STATE_00.getCode());
                map.put("actComment",evaluation);
                //新增节点
                map.put("actReceiver","");
                map.put("nextNode",TaskActEnums.AI_WARN_NODE_03.getCode());
                map.put("userList",JsonUtils.listTOJson(userList));
                map.put("nextState",TaskActEnums.AI_NODE_STATE_00.getCode());

                AppResponse updateResponse = this.updateAiWarnInfo(map);


                //任务已被处理
                if(flag){
                    throw new RuntimeException("任务已被他人处理，流程已结束！");
                }
            }else{
                throw new RuntimeException("任务已被他人处理，流程已结束！");
            }

            code= ResultCode.SUCCESS.getCode();
            message="终审用户回退流程处理成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }
        log.info("任务id："+procInstId+" 已处理，数据更新！");
        return new AppResponse(code,message);    }


    @Override
    public AppResponse dismissAI(String procInstId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="误报解除失败！";
        try {
            String loginNm = this.getLogin(token);
            //查询任务，如果存在做结束处理
            //List<TaskEntity> taskEven = taskInfoMapper.getTaskByEventId(eventId);
            Map<String, Object> taskEven = taskInfoMapper.getEvTaDetail(procInstId);
            if(null!=taskEven){
                int backFlag = taskEven.get("backFlag")==null?0:(int) taskEven.get("backFlag");
                Integer eventId = (Integer) taskEven.get("eventId");
                Map<String, Object> map =new HashMap<>();
                map.put("procInstId",procInstId);
                map.put("handleResults","1");
                map.put("feedBack","误报解除");
                map.put("picture","");
                map.put("eventSta", EventParamCode.NOTIFIED_FLAG_04.getCode());

                String taskId="";

                if(1==backFlag){
                    //1.1回退结束流程
                    String revoke = this.revoke(procInstId, loginNm, "aiEnd", "回退流程后进行误报解除");
                    taskId=revoke;
                }else{
                    //获取岗位
                    Task task = taskService.createTaskQuery().processInstanceId(procInstId).taskAssignee(loginNm).singleResult();
                    if (task == null) {
                        throw new Exception("流程未启动或已执行完成，无法撤回");
                    }
                    taskId=task.getId();
                    Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
                    String operation = execution.getActivityId();
                    if("review".equals(operation)){
                        map.put("operation","reviewFlag");
                    }else if("manage".equals(operation)){
                        map.put("operation","manageFlag");
                    }else{
                        throw new RuntimeException("未找到流程节点，请联系管理员！("+operation+")");
                    }
                    //2.结束流程
                    AppResponse endResponse = this.endTask(map, loginNm);
                    if(endResponse.getCode()==ResultCode.FAILED.getCode()){
                        return new AppResponse(code,endResponse.getMessage());
                    }

                }

                //2.修改业务表信息
                map.put("loginNm",loginNm);
                map.put("procInstId",procInstId);
                //2.2.1.任务
                map.put("taskResult",TaskActEnums.AI_WARN_STATE_05.getCode());
                //2.2.2.预警
                map.put("eventId",String.valueOf(eventId));
                map.put("eventState",EventParamCode.EVENT_STATE_04.getCode());
                map.put("eventResult",EventParamCode.EVENT_HANDLE_1.getCode());
                //2.2.2更新节点
                map.put("curState",TaskActEnums.AI_NODE_STATE_01.getCode());
                map.put("taskId",taskId);
                map.put("lastState",TaskActEnums.AI_NODE_STATE_00.getCode());
                map.put("actComment","误报解除");
                //2.2.3新增节点
                map.put("nextNode",TaskActEnums.AI_WARN_NODE_07.getCode());
                map.put("userList","");
                map.put("nextState",TaskActEnums.AI_NODE_STATE_01.getCode());
                map.put("actReceiver","误报解除");

                //修改
                AppResponse updateResponse = this.updateAiWarnInfo(map);

                if(updateResponse.getCode()==ResultCode.FAILED.getCode()){
                    return new AppResponse(code,updateResponse.getMessage());
                }
            }


            code= ResultCode.SUCCESS.getCode();
            message="误报解除成功";
        } catch (RuntimeException r) {
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }
        return new AppResponse(code,message);
    }

    /**
     * 结束流程
     * @param map
     * @param loginNm
     * @return
     */
    public AppResponse endTask(Map<String, Object> map, String loginNm) {
        String procInstId = null;
        long code= ResultCode.FAILED.getCode();
        String message="流程结束失败！";
        try {
            procInstId = (String)map.get("procInstId");
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        flag=false;
                        Map<String, Object> variables =new HashMap<>();
                        variables.put("taskCompleted", 1);
                        variables.put("isSuccess", true);
                        String operation=(String)map.get("operation");
                        variables.put(operation,"end" );
                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),this.getComment(map));
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id："+procInstId+" 已处理，流程结束！");
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
            message="流程成功结束！";
        }catch (RuntimeException r) {
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }
        return new AppResponse(code,message);
    }


        /**
         * 误报解除后上报
         * 更新预警任务及信息
         * @param map
         * @return
         */
    public AppResponse updateAiWarnInfo(Map<String, Object> map) {
        long code=ResultCode.FAILED.getCode();
        String message="更新预警任务及信息";
        try {
            String loginNm = map.get("loginNm") == null ? "" : (String) map.get("loginNm");
            String procInstId = map.get("procInstId") == null ? "" : (String) map.get("procInstId");
            String curState = map.get("curState") == null ? "" : (String) map.get("curState");


            //1.更新任务处理结果
            String taskResult = map.get("taskResult") == null ? "" : (String) map.get("taskResult");
            Integer backFlag= map.get("backFlag") == null ? 0 : (Integer) map.get("backFlag");

            TaskEntity taskEntity=new TaskEntity();
            taskEntity.setHandleResults(taskResult);
            taskEntity.setProcInstId(procInstId);
            taskEntity.setBackFlag(backFlag);
            taskInfoMapper.updateTask(taskEntity);

            //2.修改事件表
            //修改事件表:0:否；1：是
            if(backFlag!=1 &&
                    (TaskActEnums.AI_WARN_STATE_03.getCode().equals(taskResult)
                    || TaskActEnums.AI_WARN_STATE_05.getCode().equals(taskResult))
            ){
                String eventId = map.get("eventId") == null ? "" : (String) map.get("eventId");
                String eventState= map.get("eventState") == null ? "" :(String)map.get("eventState");
                String eventResult= map.get("eventResult") == null ? "" : (String) map.get("eventResult");
                //更新预警事件表
                EventEntity ev=new EventEntity();
                ev.setEventId(Integer.parseInt(eventId));
                ev.setEventState(eventState);
                ev.setHandleResults(eventResult);
                ev.setHandleTime(TimeTool.getYmdHms());
                rmEventInfoMapper.updateEventState(ev);
    /*            //修改事件表
                if(EventParamCode.EVENT_STATE_04.getCode().equals(eventSta)){
                    eventState= EventParamCode.EVENT_STATE_04.getCode();
                    notice = EventParamCode.NOTIFIED_FLAG_04.getCode();
                }if(EventParamCode.EVENT_STATE_05.getCode().equals(eventSta)){
                    eventState= EventParamCode.EVENT_STATE_05.getCode();
                    notice = EventParamCode.NOTIFIED_FLAG_05.getCode();
                }else{
                    eventState= EventParamCode.EVENT_STATE_01.getCode();
                    notice = EventParamCode.NOTIFIED_FLAG_01.getCode();
                }*/

                //4.更新通知
                eventNotifiedMapper.updateNotifiedFlag(eventId,TimeTool.getYmdHms(),eventState);

            }

            //3.1.更新本节点信息
            String taskId = map.get("taskId") == null ? "" : (String) map.get("taskId");
            String lastState = map.get("lastState") == null ? "" : (String) map.get("lastState");
            String actComment = map.get("actComment") == null ? "" : (String) map.get("actComment");
            Boolean nowFlag = this.updateActNode(loginNm,
                    new DateTime(),
                    "",
                    curState,
                    "",
                    taskId,
                    lastState
            );
            if(nowFlag){
                throw new RuntimeException("流程处理失败，请联系管理员！");
            }

            //3.2.保存下节点信息
            String nextNode = map.get("nextNode") == null ? "" : (String) map.get("nextNode");
            String nextState = map.get("nextState") == null ? "" : (String) map.get("nextState");
            String userList = map.get("userList") == null ? "" : (String) map.get("userList");
            String actReceiver = map.get("actReceiver") == null ? "" : (String) map.get("actReceiver");
            List<String> users=new ArrayList<>();
            if(""!=userList){
                users=JsonUtils.jsonTOList(userList,String.class);
            }
            Boolean nextFlag = this.saveActNode(
                    nextNode,
                    userList,
                    new DateTime(),
                    actReceiver,
                    new DateTime(),
                    "",
                    nextState,
                    procInstId,
                    "",
                    "",
                    users
            );
            if(nextFlag){
                throw new RuntimeException("流程处理失败，请联系管理员！");
            }

            code= ResultCode.SUCCESS.getCode();
            message="更新预警任务及信息成功！";
        }catch (RuntimeException r){
            r.printStackTrace();
            return new AppResponse(code,r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AppResponse(code,e.getMessage());
        }
        return new AppResponse(code,message);
    }

    /**
     * 封装意见
     * @param map
     * @return
     */
    public String getComment(Map<String, Object> map){
        CommentEntity en =new CommentEntity();
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");
        String review = (String)map.get("review");
        String evaluation = (String)map.get("evaluation");

        en.setFeedBack(feedBack);
        en.setHandleResults(handleResults);
        en.setPicture(picture);
        en.setEvaluation(evaluation);
        en.setReview(review);
        return JsonUtils.beanToJson(en);
    }


    /**
     * 获取登录人
     * @return
     */
    public String getLogin(String token){
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }
        return loginNm;
    }


    /**
     * 根据名称退回
     * @param processInstanceId
     * @param nowUser
     * @param backNode
     * @param evaluation
     * @throws RuntimeException
     * @throws Exception
     */
    public String revoke(String processInstanceId, String nowUser,String backNode,String evaluation) throws RuntimeException,Exception {
        //Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(nowUser).singleResult();
        if (task == null) {
            throw new Exception("流程未启动或已执行完成，无法撤回");
        }
        //通过processInstanceId查询历史节点
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .asc()
                .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        //找到当前运行的节点
        for (HistoricTaskInstance hti : htiList) {
            if (nowUser.equals(hti.getAssignee())) {
                myTaskId = hti.getId();
                myTask = hti;
                break;
            }
        }
        if (null == myTaskId) {
            throw new Exception("该任务非当前用户提交，无法撤回");
        }
        String processDefinitionId = myTask.getProcessDefinitionId();
        //获取流程模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //查询已经完成的流程节点，查询到上一条已完成的节点，则跳出循环
        /*List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .executionId(myTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : haiList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }*/
        //查询到上一条已完成的节点
        FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(backNode);
        //当前运行的节点
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);
        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(flowNode.getOutgoingFlows());
        //清理活动方向
        flowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        //1.终审岗新方向
        SequenceFlow newFinalFlow = new SequenceFlow();
        newFinalFlow.setId("newFinalFlowId");
        newFinalFlow.setSourceFlowElement(flowNode);
        newFinalFlow.setTargetFlowElement(myFlowNode);
        newSequenceFlowList.add(newFinalFlow);
        //当前节点新方向
        flowNode.setOutgoingFlows(newSequenceFlowList);
        Authentication.setAuthenticatedUserId(nowUser);
        //增加审批备注
        CommentEntity en =new CommentEntity();
        en.setEvaluation(evaluation);
        taskService.addComment(task.getId(),task.getProcessInstanceId(),JsonUtils.beanToJson(en));
        //完成任务
        taskService.complete(task.getId());
        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);

        return task.getId();
    }

    /**
     * 保存节点数据
     * @param actCode
     * @param actReceiver
     * @param actReceiveTm
     * @param actHandler
     * @param actHandleTm
     * @param actComment
     * @param actState
     * @param actInstId
     * @param actTaskId
     * @param actMark
     * @return
     */
    public Boolean saveActNode(String actCode,
                               String actReceiver,
                               DateTime actReceiveTm,
                               String actHandler,
                               DateTime actHandleTm,
                               String actComment,
                               String actState,
                               String actInstId,
                               String actTaskId,
                               String actMark,
                               List<String> userList){

        try {
            //流程处理记录新增
            ActInstEntity actEntity=new ActInstEntity();
            actEntity.setActCode(Integer.parseInt(actCode));
            actEntity.setActReceiver(actReceiver);
            actEntity.setActReceiveTm(actReceiveTm);
            actEntity.setActHandler(actHandler);
            actEntity.setActHandleTm(actHandleTm);
            actEntity.setActComment(actComment);
            actEntity.setActState(actState);
            actEntity.setActInstId(actInstId);
            actEntity.setActTaskId(actTaskId);
            actEntity.setActMark(actMark);
            taskActInstMapper.addAct(actEntity);

            //流程处理记录新增处理人
            if(null!=userList && userList.size()>0){
                int actId = actEntity.getActId();
                for(int h=0;h<userList.size();h++){
                    AssEntity assEntity=new AssEntity();
                    assEntity.setAssActId(actId);
                    assEntity.setAssAssignee(userList.get(h));
                    assEntity.setAssModifyTm(actReceiveTm);
                    assEntity.setAssState(TaskActEnums.ASS_STATE_01.getCode());
                    taskActAssigneeMapper.addAssignee(assEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

        return false;
    }

    /**
     * 修改节点数据
     * @param actHandler
     * @param actHandleTm
     * @param actComment
     * @param actState
     * @param actMark
     * @return
     */
    public Boolean updateActNode(String actHandler,
                               DateTime actHandleTm,
                               String actComment,
                               String actState,
                               String actMark,
                               String actTaskId,
                               String currentState
        ){
        try {
            ActInstEntity actEntity=new ActInstEntity();
            actEntity.setActHandler(actHandler);
            actEntity.setActHandleTm(actHandleTm);
            actEntity.setActComment(actComment);
            actEntity.setActState(actState);
            actEntity.setActMark(actMark);
            actEntity.setActTaskId(actTaskId);
            actEntity.setCurrentState(currentState);
            taskActInstMapper.updateAct(actEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

}
