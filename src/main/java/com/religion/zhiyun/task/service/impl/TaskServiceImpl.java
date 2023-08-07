package com.religion.zhiyun.task.service.impl;

import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.CommentEntity;
import com.religion.zhiyun.task.entity.ProcdefEntity;
import com.religion.zhiyun.task.entity.UpFillEntity;
import com.religion.zhiyun.task.service.TaskService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.enums.RoleEnums;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
            //获取登录用户
            String login = this.getLogin(token);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
            String identity = sysUserEntity.getIdentity();
            String identityArr ="10000002,10000003,10000004,10000005,10000006,10000007";
            if(RoleEnums.ZU_YUAN.getCode().equals(identity) || RoleEnums.ZU_ZHANG.getCode().equals(identity)){
                identityArr="10000006,10000007";
            }else if(RoleEnums.JIE_GAN.getCode().equals(identity) || RoleEnums.JIE_WEI.getCode().equals(identity)){
                identityArr="10000004,10000005";
            }else if(RoleEnums.QU_GAN.getCode().equals(identity) || RoleEnums.QU_WEI.getCode().equals(identity)){
                identityArr="10000002,10000003";
            }
           // list=taskInfoMapper.getTaskGather(num,login);
            list= taskInfoMapper.getTaskAgvGather(num, login, identityArr.split(","));

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
        if("go".equals(flag)){
            if(null!=userList && userList.size()>0){
                variables.put("handleList"+assiNo, userList);
            }else{
                throw new RuntimeException("无相关处理人，请重新确认！");
            }
        }

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
    public PageResponse getTaskCommon(String procInstId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取任务流转意见";
        List<Map<String,Object>> taskCommon = new ArrayList<>();

        try {
            String login = this.getLogin(token);
            SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
            if(null==sysUserEntity){
                throw new RuntimeException("用户信息丢失");
            }
            taskCommon= taskInfoMapper.queryTaskCommon(procInstId,sysUserEntity.getUserNm());
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
            //条件查询
            String taskName = (String) map.get("taskName");
            String venues = (String) map.get("venues");
            vo.setSearchOne(taskName);
            vo.setVenues(venues);
            //用户
            String login = this.getLogin(token);
            //类型
            String type = (String)map.get("type");
            String stat = (String) map.get("stat");
            if("00".equals(stat)){
                vo.setSearchThree(stat);
            }
            if("admin".equals(login)){
                taskList =taskInfoMapper.getMyLaunchTask(vo);
                total =taskInfoMapper.getMyLaunchTaskTotal(vo);
            }else {
                vo.setSearchTwo(login);
                if("01".equals(type)){//我发起的
                    taskList =taskInfoMapper.getMyLaunchTask(vo);
                    total =taskInfoMapper.getMyLaunchTaskTotal(vo);
                }else if("02".equals(type)){//我执行的
                    taskList =taskInfoMapper.getMyHandleTask(vo);
                    total =taskInfoMapper.getMyHandleTotal(vo);
                }else{
                    taskList =taskInfoMapper.getMyTask(vo);
                    total =taskInfoMapper.getMyTaskTotal(vo);
                }
            }

            //查询
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
    public PageResponse getFirstTask(Map<String, Object> map, String token) {
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
            //用户
            String login = this.getLogin(token);
            vo.setSearchOne(login);

            taskList=taskInfoMapper.getFirstTask(vo);
            total=taskInfoMapper.getFirstTaskTotal(vo);
            //查询
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
    public PageResponse getPcTask(Map<String, Object> map, String token) {
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
            //条件查询
            String taskName = (String) map.get("taskName");
            String venues = (String) map.get("venues");
            vo.setSearchOne(taskName);
            vo.setVenues(venues);
            //用户
            String login = this.getLogin(token);
            if(!"admin".equals(login)){
                vo.setSearchTwo(login);
            }
            //类型
            String type = (String)map.get("type");
            if("00".equals(type)){//待完成
                taskList=taskInfoMapper.getUnHandleTask(vo);
                total=taskInfoMapper.getUnHandleTaskTotal(vo);
            }else if("01".equals(type)){//经手未结束
                vo.setSearchThree("01");
                vo.setSearchFour("");
                taskList=taskInfoMapper.getHandTask(vo);
                total=taskInfoMapper.getHandTaskTotal(vo);
            }else if("02".equals(type)){//经手已结束
                vo.setSearchThree("");
                vo.setSearchFour("02");
                taskList=taskInfoMapper.getHandTask(vo);
                total=taskInfoMapper.getHandTaskTotal(vo);
            }



            //查询
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
    public PageResponse getTaskDetail(String procInstId,String token) {
        long code= ResultCode.FAILED.getCode();
        String message= "获取任务详情";
        List<Map<String,Object>> taskList = new ArrayList<>();

        try {
            if(null==procInstId || procInstId.isEmpty()){
                throw new RuntimeException("任务id不能为空！");
            }
            String login = this.getLogin(token);
            //查询
            taskList =taskInfoMapper.getTaskDetail(login,procInstId);
            if(null!=taskList && taskList.size()>0){
                //返回
                Map<String, Object> taskMap = taskList.get(0);
                String taskContent= (String) taskMap.get("taskContent");
                String flowType= (String) taskMap.get("flowType");
                if(!GeneTool.isEmpty(taskContent) && (flowType.equals("03") || flowType.equals("04"))){
                    UpFillEntity upFillEntity = JsonUtils.jsonTOBean(taskContent, UpFillEntity.class);
                    String picturesPath = upFillEntity.getTaskPicture();
                    //图片
                    if(!GeneTool.isEmpty(picturesPath)){
                        String[] split = picturesPath.split(",");
                        List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(split);
                        upFillEntity.setPicturesUrl(fileUrl.toArray());
                        String con = JsonUtils.beanToJson(upFillEntity);
                        taskMap.put("taskContent",con);
                    }
                }

                //返回意见
                List<Map<String, Object>> commentList = new ArrayList<>();
                //获取意见
                //List<Map<String, Object>> mapList = taskInfoMapper.queryTaskCommon(procInstId,sysUserEntity.getUserNm());
                List<Map<String, Object>> mapList = taskInfoMapper.getTaskCommon(procInstId);

                //意见不为空，处理
                if(null!=mapList && mapList.size()>0){
                    for(int i=0;i<mapList.size();i++){
                        Map<String, Object> map= mapList.get(i);
                        //获取推送
                        Map<String, Object> sendMap= new HashMap<>();
                        String actId = (String) map.get("actId");
                        if(!"taskStart".equals(actId) && !"taskEnd".equals(actId)){
                            sendMap= taskInfoMapper.getTaskSend(procInstId, actId);
                        }
                        sendMap.put("handlePerson","");
                        sendMap.put("handleTime","");
                        sendMap.put("handleResults","");
                        sendMap.put("feedBack","");
                        sendMap.put("picture","");
                        String sendTime = (String) sendMap.get("sendTime");
                        if(!GeneTool.isEmpty(sendTime)){
                            commentList.add(sendMap);
                        }
                        //重新组装
                        Map<String, Object> commentMap= new HashMap<>();
                        //节点
                        //commentMap.put("jieDian",(String) map.get("jieDian"));
                        //处理人
                        commentMap.put("handlePerson",(String) map.get("taskNm"));
                        commentMap.put("handleTime",(String) map.get("handleTime"));
                        //意见
                        String messages = (String) map.get("message");
                        String handleTime = (String) map.get("handleTime");
                        String pictures="";
                        if(null!=messages && !messages.isEmpty()){
                            Map<String, Object> cmap = JsonUtils.jsonToMap(messages);
                            String handleResults = (String) cmap.get("handleResults");
                            String results ="";
                            if("1".equals(handleResults)){
                                results ="已解决不";
                            }else if("0".equals(handleResults)){
                                results ="未解决";
                            }else{
                                results=handleResults;
                            }

                            commentMap.put("handleResults",results);
                            commentMap.put("feedBack",cmap.get("feedBack"));
                            //图片处理
                            pictures= (String) cmap.get("picture");
                        }else if(null!=handleTime && !handleTime.isEmpty()  ){
                            pictures= (String) taskMap.get("taskPicture");
                        }
                        List<Map<String, Object>> fileUrl =new ArrayList<>();
                        if(null!=pictures && !pictures.isEmpty()){
                            fileUrl = rmFileMapper.getFileUrl(pictures.split(","));
                        }
                        commentMap.put("picture",fileUrl.toArray());

                        //推送人
                        commentMap.put("sendNames","");
                        commentMap.put("sendTime","");
                        //放入
                        if(!GeneTool.isEmpty(handleTime)){
                            commentList.add(commentMap);
                        }

                    }
                }
                taskMap.put("taskComment",commentList.toArray());


                //一键上报人
                String reportMen = taskInfoMapper.getReportMen(procInstId);
                taskMap.put("reportMen",reportMen==null?"":reportMen);

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
