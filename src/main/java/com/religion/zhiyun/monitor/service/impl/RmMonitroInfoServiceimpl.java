package com.religion.zhiyun.monitor.service.impl;

import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.event.entity.NotifiedEntity;
import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MoVenuesEntity;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.login.api.ResultCode;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.task.service.TaskReportService;
import com.religion.zhiyun.user.dao.SysUserMapper;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.ParamCode;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;
import com.religion.zhiyun.venues.entity.ParamsVo;
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

import java.util.*;

@Slf4j
@Service
public class RmMonitroInfoServiceimpl implements RmMonitroInfoService {
    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RmFileMapper rmFileMapper;
    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;
    @Autowired
    private EventNotifiedMapper eventNotifiedMapper;
    @Autowired
    private TaskService taskService;

    @Autowired
    TaskInfoMapper taskInfoMapper;

    @Override
    public List<MonitroEntity> allMonitro() {
        return rmMonitroInfoMapper.allMonitro();
    }

    @Override
    public void addMonitro(MonitroEntity monitroEntity) {
        if(!monitroEntity.getMonitorUrl().isEmpty() && !monitroEntity.getAccessNumber().isEmpty()){
            rmMonitroInfoMapper.addMonitro(monitroEntity);
        }
    }

    @Override
    public PageResponse addMonitorEvent(String repairJson) {
        long code=ResultCode.FAILED.getCode();
        String message="监控设备报修,数据处理！";

        try {
            if(null==repairJson || repairJson.isEmpty()){
                throw new RuntimeException("报修设备信息不能为空！");
            }
            //解析接收参数
            Map<String, Object> map = JsonUtils.jsonToMap(repairJson);

            String venuesId= (String) map.get("venuesId");//10000143",
            String repairAccessNumber= (String) map.get("repairAccessNumber");//",
            String repairState= (String) map.get("repairState");//02",
            String repairStateNm= (String) map.get("repairStateNm");//报修",
            String repairTime= (String) map.get("repairTime");//2023-03-13 06:31:52",
            String locationName= (String) map.get("locationName");//报修地址"
            List<String> pictureRecs= (List<String>) map.get("pictureRecs");//报修地址"
            //数据校验
            if(null==venuesId || venuesId.isEmpty()){
                throw new RuntimeException("场所不能为空！");
            }
            if(null==repairAccessNumber || repairAccessNumber.isEmpty()){
                throw new RuntimeException("设备编号不能为空！");
            }
            if(null==locationName || locationName.isEmpty()){
                throw new RuntimeException("报修地址不能为空！");
            }

            //1.更新监控状态
            List<MonitroEntity> monitorsList = rmMonitroInfoMapper.getMonitorsList(repairAccessNumber);
            if(null==monitorsList || monitorsList.size()<1){
                throw new RuntimeException("报修设备"+repairAccessNumber+"填写错误，请确认设备编号后重新填写！");
            }
            MonitroEntity monitroEntity = monitorsList.get(0);
            monitroEntity.setState("02");
            monitroEntity.setLastModifyTime(TimeTool.getYmdHms());
            monitroEntity.setLastModifier("设备报修");
            rmMonitroInfoMapper.updateMonitro(monitroEntity);

            //2.保存图片
            String picturePath="";
            if(null!=pictureRecs && pictureRecs.size()>0){
                for(int i=0;i<pictureRecs.size();i++){
                    String pictureRec = pictureRecs.get(i);
                    FileEntity fileEntity=new FileEntity();
                    fileEntity.setFilePath(pictureRec);
                    fileEntity.setFileType(ParamCode.FILE_TYPE_01.getCode());
                    fileEntity.setCreator("监控设备报修");
                    fileEntity.setCreateTime(TimeTool.getYmdHms());
                    rmFileMapper.add(fileEntity);
                    picturePath =picturePath+fileEntity.getFileId()+"," ;
                }
            }

            //3.添加事件
            EventEntity event = new EventEntity();
            event.setWarnTime(repairTime);
            event.setRelVenuesId(Integer.parseInt(venuesId));
            event.setAccessNumber(repairAccessNumber);
            event.setEventType(ParamCode.EVENT_TYPE_05.getCode());
            event.setEventLevel(ParamCode.EVENT_LEVEL_1.getCode());
            event.setEventResource(ParamCode.EVENT_TYPE_05.getCode());
            event.setLocation(locationName);
            event.setPicturesPath(picturePath);
            event.setEventState(ParamCode.EVENT_STATE_00.getCode());
            event.setHandleResults("0");
            event.setHandleTime(TimeTool.getYmdHms());
            rmEventInfoMapper.addEvent(event);

            //4.添加通知
            String nextHandler = this.addNotifiedParty("05", Integer.parseInt(venuesId), event.getEventId(), locationName);
            //5.发起任务
            this.launch(event,nextHandler);

            code=ResultCode.SUCCESS.getCode();
            message="监控设备报修,数据处理成功！";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message);
    }

    /**
     * 任务发起
     * @param entity
     * @param nextHandler
     * @return
     */
    public AppResponse launch(EventEntity entity, String nextHandler) {
        long code=ResultCode.FAILED.getCode();
        String message="监控设备维修任务发起";
        String procInstId="";

            Authentication.setAuthenticatedUserId("系统发起");
            List<String> userList =new ArrayList<>();
            if(null!=nextHandler && !nextHandler.isEmpty()){
                String[] split = nextHandler.split(",");
                for(int i=0;i<split.length;i++){
                    userList.add(split[i]);
                }
            }
            //任务处理
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
            TaskEntity taskEntity=new TaskEntity();
            taskEntity.setTaskName("监控设备报修");
            taskEntity.setTaskContent("系统发起");
            taskEntity.setTaskPicture(entity.getPicturesPath());
            taskEntity.setRelEventId(String.valueOf(entity.getEventId()));
            taskEntity.setRelVenuesId(String.valueOf(entity.getRelVenuesId()));
            taskEntity.setLaunchPerson("系统发起");
            taskEntity.setLaunchTime(new Date());
            taskEntity.setTaskType(TaskParamsEnum.ZY_REPORT_TASK_KEY.getName());
            taskEntity.setProcInstId(procInstId);
            taskEntity.setFlowType("07");//设备维修
            taskInfoMapper.addTask(taskEntity);

            code=ResultCode.SUCCESS.getCode();
            message="任务id："+processInstanceId+" 发起申请，任务开始！";
            log.info(message);
        return new AppResponse(code,message);
    }
    /**
     * 预警通知保存
     * @param eventType
     * @param relVenuesId
     * @param relEventId
     * @return
     */
    public String addNotifiedParty(String eventType,int relVenuesId,int relEventId,String location) {

        String contents="【云监控中心】您好！您位于"+location+"的设备报修，请您尽快处理！！";
        String user="";//监管

        //1.根据场所获取场所三人驻堂的成员
        List<Map<String, Object>> userList = sysUserMapper.getSanByVenues(relVenuesId);

        if(null!=userList && userList.size()>0){
            for(int i=0;i<userList.size();i++){
                Map<String, Object> map = userList.get(i);
                String userMobile = (String) map.get("userMobile");
                user=user+userMobile+",";
                //短信通知
                //String message = SendMassage.sendSms(contents, userMobile);
                String message ="";
                System.out.println(userMobile+message+"，共发送"+(i+1)+"条短信");
            }
        }else{
            throw new RuntimeException("该场所内尚未添加三人驻堂成员！");
        }

        log.info(relEventId+"预警信息已通知：三人驻堂的成员（"+user+")");
        return user;
    }

    @Override
    public void deleteMonitro(String monitroId) {
        rmMonitroInfoMapper.deleteMonitro(monitroId);
    }

    @Override
    public PageResponse getAllNum(String token) {
        long code= ResultCode.FAILED.getCode();
        String result="监控数量统计失败！";
        List<Map<String, Object>> allNum = new ArrayList<>();
        try {
            ParamsVo auth = this.getAuth(token);
            allNum = rmMonitroInfoMapper.getAllNum(auth);
            Long venuesMonitorTotal = rmMonitroInfoMapper.getVenuesMonitorTotal(auth);
            if(null!=allNum && allNum.size()>0){
                Map<String, Object> map = allNum.get(0);
                map.put("religionsNum",venuesMonitorTotal);
            }
            code=ResultCode.SUCCESS.getCode();
            result="监控数量统计成功！";
        } catch (RuntimeException e) {
            result=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            result="监控数量统计失败！";
            e.printStackTrace();
        }
        return new PageResponse(code,result,allNum.toArray());
    }

    @Override
    public List<MonitroEntity> getMonitorByState(String state) {
        return rmMonitroInfoMapper.getMonitorByState(state);
    }

    @Override
    public List<MonitroEntity> getMonitorByVenuesId(String state) {
        return rmMonitroInfoMapper.getMonitorByVenuesId(state);
    }

    @Override
    public RespPageBean getMonitrosByPage(Integer page, Integer size, String accessNumber) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<MonitroEntity> dataList=rmMonitroInfoMapper.getMonitrosByPage(page,size,accessNumber);
        Object[] objects = dataList.toArray();
        Long total=rmMonitroInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }

    @Override
    public String getMonitorURLByAccessNum(String accessNum) {
        return rmMonitroInfoMapper.getMonitorURLByAccessNum(accessNum);
    }

    @Override
    public RespPageBean getVenuesMonitor(Integer page, Integer size,String venuesName,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="场所监控查询";
        List<Map<String,Object>> list=new ArrayList<>();
        Long total =0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            //获取用户
            ParamsVo auth = this.getAuth(token);
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(venuesName);

            //获取场所信息
            list = rmMonitroInfoMapper.getVenuesMonitor(auth);
            if(null!=list && list.size()>0){
                for(int i=0;i<list.size();i++){
                    Map<String, Object> map = list.get(i);
                    String monitors = (String) map.get("monitors");
                    String[] str={};
                    if(null!=monitors && !monitors.isEmpty()){
                        str=monitors.split(",");
                    }
                    map.put("monitors",str);
                }
            }
            total = rmMonitroInfoMapper.getVenuesMonitorTotal(auth);

            code= ResultCode.SUCCESS.getCode();
            message="场所监控查询成功";
        } catch (Exception e) {
            message="场所监控查询失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message,total,list.toArray());
    }

    @Override
    public RespPageBean getMonitors(Map<String, Object> map,String token) {
        long code= ResultCode.FAILED.getCode();
        String message="监控详情查询";

        List<Map<String,Object>> list=new ArrayList<>();
        Long total=0l;
        try {
            //获取登录人
            ParamsVo auth = this.getAuth(token);
            //获取前端参数
            String search = (String) map.get("search");
            String state = (String) map.get("state");
            String pages = (String) map.get("page");
            String sizes = (String)map.get("size");
            Integer size = Integer.valueOf(sizes);
            Integer page = Integer.valueOf(pages);
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            auth.setPage(page);
            auth.setSize(size);
            auth.setSearchOne(search);
            auth.setSearchTwo(state);
            list = rmMonitroInfoMapper.getMonitors(auth);
            total=rmMonitroInfoMapper.getMonitorsTotal(auth);
            code= ResultCode.SUCCESS.getCode();
            message="监控详情查询成功";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new RespPageBean(code,message,total,list.toArray());
    }

    @Override
    public PageResponse getMoDetail(String search,String type) {
        long code= ResultCode.FAILED.getCode();
        String message="监控详情查询失败";
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            if("01".equals(type)){//地图
                list = rmMonitroInfoMapper.getMoDetail("",search);
            }else if("02".equals(type)){//教职监控
                list = rmMonitroInfoMapper.getMoDetail(search,"");
            }
            //监控地址数据处理
            if(null!=list && list.size()>0){
                for(int i=0;i<list.size();i++){
                    Map<String, Object> map = list.get(i);
                    String monitors = (String) map.get("monitors");
                    String[] str={};
                    if(null!=monitors && !monitors.isEmpty()){
                        str=monitors.split(",");
                    }
                    map.put("monitors",str);
                }
            }else{
                code= ResultCode.VALIDATE_FAILED.getCode();
                throw new RuntimeException("该场所尚未接入监控信息！");
            }
            code= ResultCode.SUCCESS.getCode();
            message="监控详情查询成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }
        return new PageResponse(code,message,list.toArray());
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
     * 获取
     * @return
     */
    public ParamsVo getAuth(String token){
        String login = this.getLogin(token);
        SysUserEntity sysUserEntity = sysUserMapper.queryByName(login);
        String area="";
        String town ="";
        String relVenuesId="";
        String[] venuesArr={};
        if(null!=sysUserEntity){
            relVenuesId = sysUserEntity.getRelVenuesId();
            town = sysUserEntity.getTown();
            area = sysUserEntity.getArea();
        }else{
            throw new RuntimeException("用户已过期，请重新登录！");
        }
        ParamsVo vo=new ParamsVo();
        vo.setArea(area);
        vo.setTown(town);
        vo.setVenues(relVenuesId);
        if(null!=relVenuesId && !relVenuesId.isEmpty()){
            venuesArr=relVenuesId.split(",");
            vo.setVenuesArr(venuesArr);
        }
        return vo;
    }


}
