package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.FilingEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskFilingService;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.TokenUtils;
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
    public AppResponse launch(TaskEntity taskEntity, String token) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        String result="";
        try {
            if("03".equals(taskEntity.getFlowType())){
                message="活动备案任务发起";
            }else if("04".equals(taskEntity.getFlowType())){
                message="场所更新任务发起";
            }
            //获取登录人
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            Map<String, Object> variables = new HashMap<>();
            //根据登录人获取街镇干事 ab
            List<String> userList = taskInfoMapper.getJieGanUsers(taskEntity.getRelVenuesId());
            variables.put("handleList",userList );

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

            //保存任务信息
            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setLaunchTime(new Date());
            taskEntity.setTaskType(TaskParamsEnum.ZY_FILING_TASK_KEY.getName());
            taskEntity.setProcInstId(tmp.getProcessInstanceId());
            //taskEntity.setEmergencyLevel("02");//普通
            taskInfoMapper.addTask(taskEntity);

            log.info("任务id："+processInstanceId+" 发起申请，任务开始！");

            //保存备案信息
            if("03".equals(taskEntity.getFlowType())){
                taskEntity.setFilingStatus("01");//申请中
                taskInfoMapper.addFill(taskEntity);
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
    public AppResponse handle(TaskEntity taskEntity,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="";
        try {
            if("03".equals(taskEntity.getFlowType())){
                message="活动备案任务处理";
            }else if("04".equals(taskEntity.getFlowType())){
                message="场所更新任务处理";
            }
            //获取登录人
            String loginNm = this.getLogin(token);
            String procInstId = taskEntity.getProcInstId();
            String flowType = taskEntity.getFlowType();
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
            //流程更新处理结果
            taskEntity.setHandlePerson(loginNm);
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

            message=message+"成功！";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
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
}
