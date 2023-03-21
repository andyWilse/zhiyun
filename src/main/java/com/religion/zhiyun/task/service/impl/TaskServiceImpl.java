package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.CommentEntity;
import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RmFileMapper rmFileMapper;

    @Autowired
    private org.activiti.engine.TaskService taskService;

    @Override
    public AppResponse deployment(String taskKey) {
        long code= ResultCode.FAILED.getCode();
        String message= "流程部署";

        try {
            //第一步
            DeploymentBuilder builder=  repositoryService.createDeployment();
            String filePath="";
            if(taskKey.equals(TaskParamsEnum.ZY_REPORT_TASK_KEY.getCode())){//上报
                filePath=TaskParamsEnum.ZY_REPORT_TASK_KEY.getFilePath();
            }else if(taskKey.equals(TaskParamsEnum.ZY_ISSUED_TASK_KEY.getCode())){//下达
                filePath=TaskParamsEnum.ZY_ISSUED_TASK_KEY.getFilePath();
            }else if(taskKey.equals(TaskParamsEnum.ZY_FILING_TASK_KEY.getCode())){//活动备案、场所更新
                filePath=TaskParamsEnum.ZY_FILING_TASK_KEY.getFilePath();
            }else{
                throw new RuntimeException("流程key值在系统不存在，请确认后重新填写");
            }
            builder.addClasspathResource(filePath);
            String id = builder.deploy().getId();

            repositoryService.setDeploymentKey(id,taskKey);
            message = "流程id："+id+",部署成功";
            log.info(message);
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    public RespPageBean getProcdef(Integer page,Integer size,String taskName) {
        List<ProcdefEntity> procdef = taskInfoMapper.getProcdef(page,size,taskName);

        return new RespPageBean(200l,procdef.toArray());
    }

    @Override
    public PageResponse getRepairTask(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "报获取维修设备任务失败！";
        List<Map<String, Object>> monitorTask =new ArrayList<>();
        try {
            String login = this.getLogin(token);
            String type = (String) map.get("type");
            if("01".equals(type)){//未完成
                monitorTask = taskInfoMapper.getRepairTask(login,type,"");
            }else if("02".equals(type)){//已完成未解决
                monitorTask = taskInfoMapper.getRepairTask(login,"","0");
            }else if("03".equals(type)){//已完成已解决
                monitorTask = taskInfoMapper.getRepairTask(login,"","1");
            }
            message = "获取维修设备任务成功";
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message,monitorTask.toArray());
    }

    @Override
    public PageResponse getMonitorTask(String taskId) {
        long code= ResultCode.FAILED.getCode();
        String message= "报修设备任务信息获取失败！";
        List<Map<String, Object>> monitorTask =new ArrayList<>();
        try {
            monitorTask = taskInfoMapper.getMonitorTask(taskId);
            message = "报修设备任务信息获取成功";
            log.info(message);
            code= ResultCode.SUCCESS.getCode();
        }catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PageResponse(code,message,monitorTask.toArray());
    }

    @Override
    public AppResponse getTaskZxt(int num, String dateType,String token) {
        long code =ResultCode.FAILED.getCode();
        String message="统计任务数量数据失败！";
        List<Map<String,Object>> taskList=new ArrayList<>();
        try {
            Map<String,Object> map=new HashMap<>();
            //获取登录用户
            String login = this.getLogin(token);
            if ("month".equals(dateType)){
                taskList=taskInfoMapper.getTaskMonth(num,login);
            }else if ("week".equals(dateType)){
                taskList=taskInfoMapper.getTaskWeek(num,login);
            }else if ("day".equals(dateType)){
                taskList=taskInfoMapper.getTaskDay(num,login);
            }
            code= ResultCode.SUCCESS.getCode();
            message="统计任务数量数据成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="统计任务数量数据失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,taskList.toArray());
    }

    @Override
    public AppResponse getTaskGather(int num, String token) {
        long code =ResultCode.FAILED.getCode();
        String message="统计任务数量数据失败！";
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            Map<String,Object> map=new HashMap<>();
            //获取登录用户
            String login = this.getLogin(token);
            list=taskInfoMapper.getTaskGather(num,login);
            code= ResultCode.SUCCESS.getCode();
            message="统计任务数量数据成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message="统计任务数量数据失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message,list.toArray());
    }

    @Override
    @Transactional
    public AppResponse reportOneReport(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="一键上报任务上报失败！";

        try {
            String procInstId = (String)map.get("procInstId");
            String loginNm = this.getLogin(token);
            //下节点处理人
            List<String> userList=new ArrayList();
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            if(null!=sysUserEntity){
                String identify = sysUserEntity.getIdentity();
                //根据用户身份，查询
                if("10000006".equals(identify) || "10000007".equals(identify)){
                    //查找街干事、街委员
                    List<SysUserEntity> jie = sysUserMapper.getJie(loginNm, identify);
                    if(jie.size()>0 && null!=jie){
                        for(int i=0;i<jie.size();i++){
                            String userMobile = jie.get(i).getUserMobile();
                            userList.add(userMobile);
                        }
                    }
                }else if("10000004".equals(identify) || "10000005".equals(identify)){
                    //查找区干事、区委员
                    List<SysUserEntity> qu = sysUserMapper.getQu(loginNm, identify);
                    if(qu.size()>0 && null!=qu){
                        for(int i=0;i<qu.size();i++){
                            String userMobile = qu.get(i).getUserMobile();
                            userList.add(userMobile);
                        }
                    }
                }
            }else{
                throw new RuntimeException("系统中不存在下节点处理人，请先添加！");
            }

            //根据角色信息获取自己的待办 act_ru_task
            //List<Task> T = taskService.createTaskQuery().taskAssignee(nbr).list();
            //处理自己的待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        Map<String, Object> variables = this.setFlag(sysUserEntity.getIdentity(), "go", userList, procInstId);
                        variables.put("isSuccess", true);
                        //设置本地参数。在myListener1监听中获取。防止审核通过进行驳回
                        taskService.setVariableLocal(item.getId(),"isSuccess",false);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),this.getComment(map));
                        //完成此次审批。由下节点审批
                        taskService.complete(item.getId(), variables);
                    }
                }
            }
            log.info("任务id："+procInstId+" 上报");
            code= ResultCode.SUCCESS.getCode();
            message="一键上报流程上报成功！流程id(唯一标识)procInstId:"+ procInstId;
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message="上报流程上报失败！";
            e.printStackTrace();
        }
        return new AppResponse(code,message);
    }

    @Override
    @Transactional
    public AppResponse reportOneHandle(Map<String, Object> map, String token) {
        long code=ResultCode.FAILED.getCode();
        String message="任务处理";
        try {
            String procInstId = (String)map.get("procInstId");
            String loginNm = this.getLogin(token);
            Authentication.setAuthenticatedUserId(loginNm);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(loginNm);
            if(null==sysUserEntity){
                throw new RuntimeException("用户信息丢失，请联系管理员！");
            }
            //处理待办
            List<Task> T = taskService.createTaskQuery().processInstanceId(procInstId).list();
            if(!ObjectUtils.isEmpty(T)) {
                for (Task item : T) {
                    String assignee = item.getAssignee();
                    if(assignee.equals(loginNm)){
                        Map<String, Object> variables = this.setFlag(sysUserEntity.getIdentity(), "end",null,procInstId);
                        variables.put("nrOfCompletedInstances", 1);
                        variables.put("isSuccess", true);

                        //设置本地参数。在myListener1监听中获取。
                        taskService.setVariableLocal(item.getId(),"isSuccess",true);
                        //增加审批备注
                        taskService.addComment(item.getId(),item.getProcessInstanceId(),this.getComment(map));
                        //完成此次审批。如果下节点为endEvent。结束流程
                        taskService.complete(item.getId(), variables);
                        log.info("任务id："+procInstId+" 已处理，流程结束！");

                        //更新处理结果
                        TaskEntity taskEntity=new TaskEntity();
                        taskEntity.setHandlePerson(loginNm);
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        taskEntity.setHandleTime(new Date());
                        taskEntity.setHandleResults((String) map.get("handleResults"));
                        taskEntity.setProcInstId(procInstId);
                        taskInfoMapper.updateTask(taskEntity);
                    }
                }
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
        log.info("任务已处理，数据更新！");
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
        if("10000007".equals(identi) || "10000006".equals(identi)){
            no=1;
        }else{
            no=2;
        }
        int flagNo=0;
        int assiNo=0;
        if("10000007".equals(identity) || "10000006".equals(identity)){
            flagNo=no;
            assiNo=no+1;
        }else if("10000005".equals(identity) || "10000004".equals(identity)){
            flagNo=no+1;
            assiNo=no+2;
        }else if("10000003".equals(identity) || "10000002".equals(identity)){
            flagNo=no+2;
            assiNo=no+3;
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("flag"+flagNo, flag);
        variables.put("handleList"+assiNo, userList);
        return variables;
    }

    @Override
    public PageResponse getTasking(Map<String, Object> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取任务信息";
        List<Map<String,Object>> taskEntities = new ArrayList<>();
        long total=0l;
        try {
            //参数解析
            String taskName = (String) map.get("taskName");
            String taskContent = (String) map.get("taskContent");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //获取任务
            String loginNm = this.getLogin(token);
            ParamsVo vo=new ParamsVo();
            vo.setPage(page);
            vo.setSize(size);
            vo.setSearchOne(taskName);
            vo.setSearchTwo(taskContent);
            //超级管理员
            if(!loginNm.equals("admin")){
                vo.setSearchThree(loginNm);
            }
            taskEntities = taskInfoMapper.queryTasks(vo);
            total=taskInfoMapper.queryTasksTotal(vo);
            code= ResultCode.SUCCESS.getCode();
            message= "获取任务信息成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message= "获取任务信息失败！";
            e.printStackTrace();
        }

        return new PageResponse(code,message,total,taskEntities.toArray());
    }

    @Override
    public PageResponse getTaskCommon(String procInstId) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取任务流转意见";
        List<Map<String,Object>> taskCommon = new ArrayList<>();

        try {
            taskCommon= taskInfoMapper.queryTaskCommon(procInstId);
            code= ResultCode.SUCCESS.getCode();
            message= "获取任务流转意见成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message= "获取任务流转意见失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,taskCommon.toArray());
    }

    @Override
    public PageResponse getMyTask(Map<String, Object> map, String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取我的任务";
        List<Map<String,Object>> taskList = new ArrayList<>();
        long total=0l;
        try {
            ParamsVo vo=new ParamsVo();
            //分页
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer page = Integer.valueOf(pages);
            Integer size = Integer.valueOf(sizes);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            vo.setPage(page);
            vo.setSize(size);
            //类型
            String type = (String)map.get("type");
            if("01".equals(type)){
                vo.setSearchOne("startEvent");
            }else if("02".equals(type)){
                vo.setSearchOne("userTask");
            }
            //用户
            String login = this.getLogin(token);
            vo.setSearchTwo(login);
            //查询
            taskList =taskInfoMapper.getMyTask(vo);
            total =taskInfoMapper.getMyTaskTotal(vo);
            code= ResultCode.SUCCESS.getCode();
            message= "获取我的任务成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message= "获取我的任务失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,total,taskList.toArray());
    }

    @Override
    public PageResponse getTaskDetail(String procInstId) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取任务详情";
        List<Map<String,Object>> taskList = new ArrayList<>();
        try {
            //查询
            taskList =taskInfoMapper.getTaskDetail(procInstId);
            if(null!=taskList && taskList.size()>0){
                //返回
                Map<String, Object> taskMap = taskList.get(0);

                //返回意见
                List<Map<String, Object>> commentList = new ArrayList<>();
                //获取意见
                List<Map<String, Object>> mapList = taskInfoMapper.queryTaskCommon(procInstId);
                //意见不为空，处理
                if(null!=mapList && mapList.size()>0){
                    for(int i=0;i<mapList.size();i++){
                        Map<String, Object> map= mapList.get(i);
                        //重新组装
                        Map<String, Object> commentMap= new HashMap<>();
                        //节点
                        commentMap.put("jieDian",(String) map.get("jieDian"));
                        //处理人
                        commentMap.put("handlePerson",(String) map.get("taskNm"));
                        //意见
                        String messages = (String) map.get("message");
                        if(null!=messages && !messages.isEmpty()){
                            Map<String, Object> cmap = JsonUtils.jsonToMap(messages);
                            commentMap.put("handleResults",cmap.get("handleResults"));
                            commentMap.put("feedBack",cmap.get("feedBack"));
                            //图片处理
                            String picture = (String) cmap.get("picture");
                            List<Map<String, Object>> fileUrl =new ArrayList<>();
                            if(null!=picture && !picture.isEmpty()){
                                fileUrl = rmFileMapper.getFileUrl(picture.split(","));
                            }
                            commentMap.put("picture",fileUrl.toArray());
                        }
                        //放入
                        commentList.add(commentMap);
                    }
                }
                taskMap.put("taskComment",commentList.toArray());
            }
            code= ResultCode.SUCCESS.getCode();
            message= "获取详情成功！";
        }catch (RuntimeException r){
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message= "获取详情失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,message,taskList.toArray());
    }
}
