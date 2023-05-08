package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.CommentEntity;
import com.religion.zhiyun.task.entity.FilingEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.entity.UpFillEntity;
import com.religion.zhiyun.task.service.TaskFilingService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.TokenUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RmFileMapper rmFileMapper;


    @Override
    @Transactional
    public AppResponse launch(String taskJson, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        String result="";
        try {
            if(null==taskJson || taskJson.isEmpty()){
                throw new RuntimeException("任务内容为空，请重新填写");
            }
            UpFillEntity upFillEntity = JsonUtils.jsonTOBean(taskJson, UpFillEntity.class);
            //封装任务信息
            TaskEntity tasksEntity=new TaskEntity();
            if("03".equals(upFillEntity.getFlowType())){
                message="活动备案任务发起";
                tasksEntity.setTaskName(TaskParamsEnum.TASK_FLOW_TYPE_03.getName());
                tasksEntity.setFlowType(TaskParamsEnum.TASK_FLOW_TYPE_03.getCode());
            }else if("04".equals(upFillEntity.getFlowType())){
                message="场所更新任务发起";
                tasksEntity.setTaskName(TaskParamsEnum.TASK_FLOW_TYPE_04.getName());
                tasksEntity.setFlowType(TaskParamsEnum.TASK_FLOW_TYPE_04.getCode());
            }
            //获取登录人
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);

            //根据登录人获取街镇干事 ab
            Map<String, Object> variables = new HashMap<>();
            List<String> userList = taskInfoMapper.getJieGanUsers(upFillEntity.getVenuesId());
            if(null!=userList && userList.size()>0){
                variables.put("handleList",userList );
            }else{
                throw new RuntimeException("无相关处理人，请重新确认！");
            }

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

            //发起人
            taskInfoMapper.updateHiActinst(loginNm,processInstanceId);
            //保存任务信息
            tasksEntity.setTaskContent(taskJson);
            tasksEntity.setRelVenuesId(upFillEntity.getVenuesId());
            tasksEntity.setTaskPicture(upFillEntity.getPicturesPath());
            tasksEntity.setLaunchPerson(loginNm);
            tasksEntity.setLaunchTime(TimeTool.getYmdHms());
            tasksEntity.setTaskType(TaskParamsEnum.ZY_FILING_TASK_KEY.getName());
            tasksEntity.setProcInstId(tmp.getProcessInstanceId());

            taskInfoMapper.addTask(tasksEntity);
            log.info("任务id："+processInstanceId+" 发起申请，任务开始！");

            //保存备案信息
            if("03".equals(upFillEntity.getFlowType())){
                tasksEntity.setFilingStatus("01");//申请中
                tasksEntity.setTaskContent(upFillEntity.getTaskContent());
                tasksEntity.setTaskName(upFillEntity.getTaskName());
                tasksEntity.setPartNum(upFillEntity.getPartNum());
                tasksEntity.setTaskTime(upFillEntity.getTaskTime());
                //负责人
                tasksEntity.setResponsiblePerson(upFillEntity.getManagerMobile());

                taskInfoMapper.addFill(tasksEntity);
            }

            result="流程id(唯一标识)procInstId:"+tmp.getProcessInstanceId();
            message=message+"成功！";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
        } catch (Exception e) {
            message=message+"失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message,result);
    }

    @Override
    @Transactional
    public AppResponse handle(Map<String,Object> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        try {
            String procInstId = (String) map.get("procInstId");
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("流程id丢失，请联系管理员！");
            }
            String flowType = (String) map.get("flowType");
            String handleResults = (String)map.get("handleResults");
            if("03".equals(flowType)){
                message="活动备案任务处理";
            }else if("04".equals(flowType)){
                message="场所更新任务处理";
            }
            //获取登录人
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                Boolean flag=true;
                for (Task item : T) {
                    if (item.getAssignee().equals(loginNm)) {
                        flag=false;
                        Map<String, Object> variables = new HashMap<>();
                        variables.put("nrOfCompletedInstances", 1);
                        variables.put("isSuccess", true);
                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(), "isSuccess", true);
                        //增加审批备注
                        taskService.addComment(item.getId(), item.getProcessInstanceId(), this.getComment(map));
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id：" + procInstId + " 已处理，流程结束！");
                    }
                }
                //任务已被处理
                if(flag){
                    throw new RuntimeException("任务已被他人处理，流程已结束！");
                }
            }else{
                throw new RuntimeException("任务已被他人处理，流程已结束！");
            }
            //流程更新处理结果
            TaskEntity taskEntity=new TaskEntity();
            taskEntity.setHandlePerson(loginNm);
            taskEntity.setHandleTime(TimeTool.getYmdHms());
            taskEntity.setHandleResults(handleResults);
            taskEntity.setProcInstId(procInstId);
            taskInfoMapper.updateTask(taskEntity);
            log.info("任务id："+procInstId+" 已处理，数据更新！");

            //更新场所信息
            if("04".equals(flowType)){
                //获取更新信息
                String taskContent = taskInfoMapper.getTaskContent(procInstId);
                UpFillEntity upFillEntity = JsonUtils.jsonTOBean(taskContent, UpFillEntity.class);
                String venuesId = upFillEntity.getVenuesId();
                String briefIntroduction = upFillEntity.getBriefIntroduction();
                String taskPicture = upFillEntity.getPicturesPath();
                String venuesPhone = upFillEntity.getVenuesPhone();
                //封装更新数据
                VenuesEntity venues=new VenuesEntity();
                venues.setBriefIntroduction(briefIntroduction);
                venues.setPicturesPath(taskPicture);
                venues.setVenuesPhone(venuesPhone);
                venues.setVenuesId(Integer.parseInt(venuesId));
                venues.setLastModifier(loginNm);
                venues.setLastModifyTime(TimeTool.getYmdHms());
                rmVenuesInfoMapper.updateFillVenues(venues);
                log.info("场所更新成功！");
            }else if("03".equals(flowType)){
                //保存备案信息
                taskInfoMapper.updateFill(taskEntity);
                log.info("活动备案成功！");
            }

            message=message+"成功！";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message=message+"失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message);
    }

    @Override
    public PageResponse getFillHistory(Integer page,Integer size,String search, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="获取历史备案信息";
        List<Map<String, Object>> fillHistory=new ArrayList<>();
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            String login = this.getLogin(token);
            fillHistory = taskInfoMapper.getFillHistory(search, login, page, size);
            message="获取历史备案信息成功！";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
        } catch (Exception e) {
            message="获取历史备案信息失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message,fillHistory.toArray());
    }

    @Override
    public PageResponse getFillHisDetail(String filingId) {
        long code= ResultCode.FAILED.getCode();
        String message="获取历史备案详情";
        List<Map<String, Object>> fillHisDetail =new ArrayList<>();
        try {
            fillHisDetail = taskInfoMapper.getFillHisDetail(filingId);
            if(null!=fillHisDetail && fillHisDetail.size()>0){
                for(int i=0;i<fillHisDetail.size();i++){
                    Map<String, Object> map = fillHisDetail.get(i);
                    //封装图片信息
                    String taskPicture = (String) map.get("taskPicture");
                    List<Map<String, Object>> path =new ArrayList<>();
                    if(null!=taskPicture && !taskPicture.isEmpty()){
                        path = rmFileMapper.getPath(taskPicture.split(","));
                    }
                    map.put("picturesPath",path.toArray());
                }
            }
            message="获取历史备案详情成功！";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
        } catch (Exception e) {
            message="获取历史备案详情失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message,fillHisDetail.toArray());
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
     * 封装意见
     * @param map
     * @return
     */
    public String getComment(Map<String, Object> map){
        CommentEntity en =new CommentEntity();
        String handleResults = (String)map.get("handleResults");
        String feedBack = (String)map.get("feedBack");
        String picture = (String)map.get("picture");
        en.setFeedBack(feedBack);
        en.setHandleResults(handleResults);
        en.setPicture(picture);
        return JsonUtils.beanToJson(en);
    }

}
