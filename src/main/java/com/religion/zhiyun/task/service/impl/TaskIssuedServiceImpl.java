package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskIssuedService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.TokenUtils;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.AppResponse;
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
public class TaskIssuedServiceImpl implements TaskIssuedService {

    @Autowired
    private TaskService taskService;

    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public AppResponse deployment() {
        return null;
    }

    @Override
    @Transactional
    public AppResponse launch(TaskEntity taskEntity,String token) {

        long code=ResultCode.FAILED.getCode();
        String message="任务下达发起";

        //任务下达补充——地址改为：层级辖区或个体场所
        String processInstanceId = null;
        Task tmp = null;
        String province="";
        String city="";
        String area="";
        String town="";
        String relVenuesId="";
        String relVenuesIds="";

        try {
            //获取下达范围
            if(!taskEntity.getProvince().isEmpty()){
                province=taskEntity.getProvince();
                relVenuesIds=province;
            }
            if(!taskEntity.getCity().isEmpty()){
                city=taskEntity.getCity();
                relVenuesIds=city;
            }
            if(!taskEntity.getArea().isEmpty()){
                area=taskEntity.getArea();
                relVenuesIds=area;
            }
            if(!taskEntity.getTown().isEmpty()){
                town=taskEntity.getTown();
                relVenuesIds=town;
            }
            if(!taskEntity.getRelVenuesId().isEmpty()){
                relVenuesId=taskEntity.getRelVenuesId();
                relVenuesIds=relVenuesId;
            }
            taskEntity.setRelVenuesId(relVenuesIds);
            //获取登录人身份信息
            String loginNm = this.getLogin(token);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            if(null==sysUserEntity){
                throw new RuntimeException("登录人信息丢失，请重新登录");
            }
            String identity = sysUserEntity.getIdentity();
            if(RoleEnums.ZU_ZHANG.getCode().equals(identity) || RoleEnums.ZU_YUAN.getCode().equals(identity)){
                throw new RuntimeException("三人驻堂人员无任务下达权限！");
            }
            //根据身份封装参数
            String identityStr="";
            if(RoleEnums.QU_WEI.getCode().equals(identity)){
                identityStr="10000002";
                if(area.isEmpty()){
                    throw new RuntimeException("区不能为空！");
                }
            }else if(RoleEnums.QU_GAN.getCode().equals(identity)){
                identityStr = "10000002,10000003";
                if(area.isEmpty()){
                    throw new RuntimeException("区不能为空！");
                }
            }else if(RoleEnums.JIE_WEI.getCode().equals(identity)){
                identityStr = "10000004";
                if(town.isEmpty()){
                    throw new RuntimeException("街道不能为空！");
                }
            }else if(RoleEnums.JIE_GAN.getCode().equals(identity)){
                identityStr = "10000004,10000005";
                if(town.isEmpty()){
                    throw new RuntimeException("街道不能为空！");
                }
            }
            String[] identityArr = identityStr.split(",");

            //根据身份获取具体下达人
            List<String> userList = taskInfoMapper.getIssuedUsers(loginNm, province, city, area, town, relVenuesId, identityArr);
            Map<String, Object> variables = new HashMap<>();
            variables.put("handleList",userList );

            Authentication.setAuthenticatedUserId(loginNm);

            /**start**/
            //开启流程。myProcess_2为流程名称。获取方式把bpmn改为xml文件就可以看到流程名
            ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
            RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
            ProcessInstance processInstance =runtimeService.startProcessInstanceByKey(TaskParamsEnum.ZY_ISSUED_TASK_KEY.getCode(),variables);
            processInstanceId = processInstance.getProcessInstanceId();
            /**end**/

            //完成此节点。由下一节点审批。完成后act_ru_task会创建一条由下节点审批的数据
            TaskQuery taskQuery = taskService.createTaskQuery();
            tmp = taskQuery.processInstanceId(processInstanceId).singleResult();
            //taskService.setAssignee("assignee2",userNbr);
            taskService.complete(tmp.getId(),variables);

            taskEntity.setLaunchPerson(loginNm);
            taskEntity.setLaunchTime(new Date());
            taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
            taskEntity.setProcInstId(tmp.getProcessInstanceId());
            taskEntity.setFlowType("02");
            //保存任务信息
            taskInfoMapper.addTask(taskEntity);

            log.info("任务id："+processInstanceId+" 发起申请，任务开始！");
            code= ResultCode.SUCCESS.getCode();
            message="下达流程下达成功！流程id(唯一标识)procInstId:"+ tmp.getProcessInstanceId();
        } catch (RuntimeException e) {
            message=e.getMessage();
            //throw new RuntimeException(message) ;
        } catch (Exception e) {
            code= ResultCode.FAILED.getCode();
            message="下达流程下达失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse handle(String procInstId, String handleResults, String feedBack, String picture,String token) {
        long code=ResultCode.FAILED.getCode();
        String message="任务下达处理";
        try {
            String loginNm = this.getLogin(token);
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
            taskEntity.setHandleTime(new Date());
            taskEntity.setHandleResults(handleResults);
            taskEntity.setProcInstId(procInstId);
            taskInfoMapper.updateTask(taskEntity);

            log.info("任务id："+procInstId+" 已处理，数据更新！");
            code= ResultCode.SUCCESS.getCode();
            message="下达流程处理成功！";
        }catch (RuntimeException e) {
            message=e.getMessage();
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="下达流程处理失败！";
            e.printStackTrace();
        }

        return new AppResponse(code,message);
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
