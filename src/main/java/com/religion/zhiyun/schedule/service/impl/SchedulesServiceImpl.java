package com.religion.zhiyun.schedule.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.event.dao.RmEventInfoMapper;
import com.religion.zhiyun.event.entity.EventEntity;
import com.religion.zhiyun.interfaces.entity.mas.MasErrEntity;
import com.religion.zhiyun.interfaces.entity.mas.MasResponse;
import com.religion.zhiyun.interfaces.entity.xueliang.HisPlayReqParam;
import com.religion.zhiyun.interfaces.entity.xueliang.PlayEntity;
import com.religion.zhiyun.interfaces.service.impl.MasMessageServiceImpl;
import com.religion.zhiyun.interfaces.service.impl.XueLiangServiceImpl;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.schedule.service.SchedulesService;
import com.religion.zhiyun.sys.file.service.impl.RmFileServiceImpl;
import com.religion.zhiyun.sys.log.dao.AppmetricLogMapper;
import com.religion.zhiyun.sys.log.entity.AppmetricLogEntity;
import com.religion.zhiyun.task.dao.TaskActInstMapper;
import com.religion.zhiyun.task.entity.ActInstEntity;
import com.religion.zhiyun.task.service.impl.TaskAiWarnServiceImpl;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.enums.TaskActEnums;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class SchedulesServiceImpl implements SchedulesService {

    @Value("${xue.liang.accept.url}")
    private String  acceptUrl;
    @Value("${xue.liang.accept.ip}")
    private Integer  acceptIp;
    @Value("${xue.liang.accept.speed}")
    private Integer  acceptSpeed;
    @Value("${xue.liang.accept.stream}")
    private Integer  acceptStream;
    @Value("${picture.his.folder}")
    private String  hisFolder;

    @Autowired
    private RmEventInfoMapper rmEventInfoMapper;
    @Autowired
    private EventNotifiedMapper eventNotifiedMapper;
    @Autowired
    private AppmetricLogMapper appmetricLogMapper;
    @Autowired
    private TaskActInstMapper taskActInstMapper;
    @Autowired
    private TaskAiWarnServiceImpl taskAiWarnServiceImpl;
    @Autowired
    private RmFileServiceImpl rmFileServiceImpl;
    @Autowired
    private MasMessageServiceImpl masMessageServiceImpl;
    @Autowired
    private OperateRecordService operateRecordService;
    @Autowired
    private XueLiangServiceImpl xueLiangServiceImpl;

    @Override
    public void UrgentAutoReport() {
        //查询十分钟没人解除预警且无人上报，
        List<EventEntity> urgentEvent = rmEventInfoMapper.findFillEvent("","","-10");
        System.out.println("查询十分钟没人解除预警且无人上报===="+urgentEvent.size());
        if(null!=urgentEvent && urgentEvent.size()>0){
            // 发起人自动上报
            for(int i=0;i<urgentEvent.size();i++){
                EventEntity event = urgentEvent.get(i);
            }
        }
    }

    @Override
    public void CommonAutoFill() {
        //十天后预警无人处理，则短信通知
        List<EventEntity> urgentEvent = rmEventInfoMapper.findFillEvent("-1","","");
        System.out.println("十天后预警无人处理，则短信通知==="+urgentEvent.size());
    }

    @Override
    public void warnCallReport() {
        Map<String, String> notified = eventNotifiedMapper.getNotified();
        if(null!=notified){
            String manegers = notified.get("manegers");
            String users = notified.get("users");
            String arr = notified.get("arr");
            String nm = notified.get("nm");
            String event = notified.get("event");
            String relEventId = notified.get("eventId");
            //管理
            if(!GeneTool.isEmpty(manegers)){
                String[] ma = manegers.split(",");
                for(int i=0;i<ma.length;i++){
                    String ph = ma[i];
                    Map<String, Object> map=new HashMap<>();
                    map.put("phone",ph);
                    map.put("venuesAddres",arr);
                    map.put("venuesName",nm);
                    map.put("event",event);
                    /*String sessionId = VoiceCall.voiceCall(map);
                    //保存数据
                    FeeInfo feeInfo =new FeeInfo();
                    feeInfo.setSessionId(sessionId);
                    feeInfo.setEventType(CallEnums.fee.getCode());
                    feeInfo.setRefEventId(String.valueOf(relEventId));
                    eventNotifiedMapper.addCall(feeInfo);*/
                }
            }
            //监管
            if(!GeneTool.isEmpty(users)){
                String[] us = users.split(",");
                for(int j=0;j<us.length;j++){
                    String ph = us[j];
                    Map<String, Object> umap=new HashMap<>();
                    umap.put("phone",ph);
                    umap.put("venuesAddres",arr);
                    umap.put("venuesName",nm);
                    umap.put("event",event);
                    /*String sessionId = VoiceCall.voiceCall(umap);
                    //保存数据
                    FeeInfo feeInfo =new FeeInfo();
                    feeInfo.setSessionId(sessionId);
                    feeInfo.setEventType(CallEnums.fee.getCode());
                    feeInfo.setRefEventId(String.valueOf(relEventId));
                    eventNotifiedMapper.addCall(feeInfo);*/
                }
            }
        }
    }

    @Override
    public void metricRecord() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
        System.out.println(localDateTime);
        //获取预警事件记录总数
        int eventAdd = appmetricLogMapper.getEventAdd(localDateTime);
        if(eventAdd>0){
            AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"预警事件",String.valueOf(eventAdd),"A330000100000202105005924");
            appmetricLogMapper.add(metricLog);
        }
        //获取任务完成总数
        HashMap<String, BigDecimal> taskMap = appmetricLogMapper.getTask(localDateTime);
        if(null!=taskMap && taskMap.size()>0){
            BigDecimal report = taskMap.get("report");
            if(report.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"任务上报",String.valueOf(eventAdd),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal issued = taskMap.get("issued");
            if(issued.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"任务下达",String.valueOf(issued),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal filing = taskMap.get("filing");
            if(filing.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"活动备案",String.valueOf(filing),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal upd = taskMap.get("upd");
            if(upd.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"场所更新",String.valueOf(upd),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
            BigDecimal warn = taskMap.get("warn");
            if(warn.compareTo(BigDecimal.ZERO)>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"预警处理",String.valueOf(warn),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
        }
        //获取短信通知
        String notify = appmetricLogMapper.getNotify(localDateTime);
        if(!StringUtils.isEmpty(notify)){
            int notyNum = notify.split(",").length;
            if(notyNum>0){
                AppmetricLogEntity metricLog=new AppmetricLogEntity(new Date(),"短信通知",String.valueOf(notyNum),"A330000100000202105005924");
                appmetricLogMapper.add(metricLog);
            }
        }
    }

    @Override
    public void repeatMessage() {
        //1.初审岗：在15分钟内未处理的预警事件，再次通知
        List<ActInstEntity> unReviewTask = taskActInstMapper.getUnAiTaskAct(15, TaskActEnums.AI_WARN_NODE_02.getCode());
        if(null!=unReviewTask && unReviewTask.size()>0){
            for(int i=0;i<unReviewTask.size();i++){
                ActInstEntity actInstEntity = unReviewTask.get(i);
                String actInstId = actInstEntity.getActInstId();
                String actReceiver = actInstEntity.getActReceiver();
                List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                //短信通知
                taskAiWarnServiceImpl.sendMsg(actInstId,
                        userSend,
                        null,
                        null,
                        null,
                        null);
            }
        }
        //2.如果30分钟内未处理
        List<ActInstEntity> thirtyTask = taskActInstMapper.getUnAiTaskAct(30, null);
        if(null!=thirtyTask && thirtyTask.size()>0){
            for(int j=0;j<thirtyTask.size();j++){
                ActInstEntity thirtyEntity = thirtyTask.get(j);
                String actInstId = thirtyEntity.getActInstId();
                String actReceiver = thirtyEntity.getActReceiver();
                int actCode = thirtyEntity.getActCode();
                String acCode = String.valueOf(actCode);
                //2.1.初审岗：直接进入下一岗
                if(TaskActEnums.AI_WARN_NODE_02.getCode().equals(acCode)){
                    //继续流程
                    List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                    taskAiWarnServiceImpl.review("初审通过",actInstId,"",userSend.get(0));
                }else {
                    //2.2.其他岗位：再次通知
                    //短信通知
                    List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                    taskAiWarnServiceImpl.sendMsg(actInstId,
                            userSend,
                            null,
                            null,
                            null,
                            null);
                }
            }
        }

        //3.如果60分钟内未处理
        List<ActInstEntity> sixtyTask = taskActInstMapper.getUnAiTaskAct(60, null);
        if(null!=sixtyTask && sixtyTask.size()>0){
            for(int j=0;j<sixtyTask.size();j++){
                ActInstEntity sixtyEntity = sixtyTask.get(j);
                String actInstId = sixtyEntity.getActInstId();
                String actReceiver = sixtyEntity.getActReceiver();
                List<String> userSend = JsonUtils.jsonTOList(actReceiver, String.class);
                int actCode = sixtyEntity.getActCode();
                String acCode = String.valueOf(actCode);
                //继续流程
                if(TaskActEnums.AI_WARN_NODE_03.getCode().equals(acCode)
                    || TaskActEnums.AI_WARN_NODE_06.getCode().equals(acCode)){
                    //处置岗
                    taskAiWarnServiceImpl.handle(actInstId,"1","系统自动处理","","",userSend.get(0));
                }else if(TaskActEnums.AI_WARN_NODE_04.getCode().equals(acCode)){
                    //评审岗
                    taskAiWarnServiceImpl.evaluate(actInstId,"系统自动处理","",userSend.get(0));
                }
            }
        }

    }

    @Override
    public void handleCutPicture() {

        try {
            //获取未截图
            List<Map<String, Object>> unVp = rmFileServiceImpl.getUnVp();
            if(null!=unVp && unVp.size()>0){
                for(int i=0;i<unVp.size();i++){
                    Map<String, Object> vpMap = unVp.get(i);
                    if(null!=vpMap){
                        Integer vpId = (Integer) vpMap.get("vpId");
                        String vpChannelId = (String) vpMap.get("vpChannelId");
                        this.getPlaySync(vpChannelId);
                        LocalDateTime vpTmStart= (LocalDateTime) vpMap.get("vpTmStart");
                        LocalDateTime vpTmEnd= (LocalDateTime) vpMap.get("vpTmEnd");
                        Integer vpFrame= (Integer) vpMap.get("vpFrame");
                        String vpTimeStart = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(vpTmStart);
                        String vpTimeEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(vpTmEnd);
                        //下载视频并截图
                        HisPlayReqParam hisPlayReqParam=new HisPlayReqParam();
                        hisPlayReqParam.setChannelId(vpChannelId);
                        hisPlayReqParam.setDeviceId(vpChannelId);
                        hisPlayReqParam.setStartTime(vpTimeStart);
                        hisPlayReqParam.setEndTime(vpTimeEnd);
                        hisPlayReqParam.setIpAddr(acceptUrl);
                        hisPlayReqParam.setPort(acceptIp);
                        hisPlayReqParam.setSpeed(acceptSpeed);
                        //流模式，1:udp0:tcp
                        hisPlayReqParam.setStreamMode(acceptStream);
                        int fileId = rmFileServiceImpl.cutStreamPicture(hisPlayReqParam,vpFrame,hisFolder);
                        //更新截图处理表
                        //rmFileServiceImpl.updateVideoPicture(vpId,String.valueOf(fileId), new Date());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getPlaySync(String vpChannelId) throws Exception {
        //实时视频地址
        /*String response="{\"msg\":\"延迟:308,重新拉流:true\",\"code\":0,\"data\":{\"ssrc\":\"330300-02112466601433374721\",\"deviceID\":\"33030002002000010019\",\"cahnnelId\":\"33030497631320000026\",\"flv\":\"http://172.20.23.35:2080/rtp/330300-02112466601433374721.flv?token=6452800b8a475f196c17c6a707483dbf\",\"ws_flv\":\"ws://172.20.23.35:2080/rtp/330300-02112466601433374721.flv?token=6452800b8a475f196c17c6a707483dbf\",\"httpsFlv\":\"https://xlspgx.wenzhou.gov.cn:2443/rtp/330300-02112466601433374721.flv?token=6452800b8a475f196c17c6a707483dbf\",\"wss_flv\":\"wss://xlspgx.wenzhou.gov.cn:2443/rtp/330300-02112466601433374721.flv?token=6452800b8a475f196c17c6a707483dbf\",\"rtmp\":\"rtmp://172.20.23.35:1935/rtp/330300-02112466601433374721?token=6452800b8a475f196c17c6a707483dbf\",\"hls\":\"\",\"rtsp\":\"rtsp://172.20.23.35:1554/rtp/330300-02112466601433374721?token=6452800b8a475f196c17c6a707483dbf\",\"tracks\":\"\",\"callId\":\"04d03d079edf9ad308c83d5d877479e9@172.20.23.34\",\"ip\":\"172.20.23.35\",\"createTime\":\"2026-10-20 16:51:00\",\"mediaPort\":\"31948\",\"forwardPort\":\"\",\"upIp\":\"\",\"regist\":false}}\n";
        PlayEntity entitys= JsonUtils.jsonTOBean(response, PlayEntity.class);
        String data = entitys.getData();
        PlayEntity entity= JsonUtils.jsonTOBean(data, PlayEntity.class);*/

        PlayEntity playSyncs = xueLiangServiceImpl.getPlaySync(vpChannelId);
        String data = playSyncs.getData();
        PlayEntity playSync= JsonUtils.jsonTOBean(data, PlayEntity.class);

        // 视频流地址（RTSP/RTMP/HTTP/文件都可以）
        String url ="";
        // 输出图片路径
        String output ="";
        if(null!=playSync){
            String flv = playSync.getFlv();
            log.info("flv地址："+flv);
            if(!GeneTool.isEmpty(flv)){
                url = flv;
                output = "//home//zhiyun//files/video//flv"+ TimeTool.getYMDHms()+".jpg";
                this.cutPic(url,output);
            }
            String rtmp = playSync.getRtmp();
            log.info("rtmp地址："+rtmp);

            if(!GeneTool.isEmpty(rtmp)){
                url = rtmp;
                output = "//home//zhiyun//files/video//rtmp"+ TimeTool.getYMDHms()+".jpg";
                this.cutPic(url,output);
            }
            String rtsp = playSync.getRtsp();
            log.info("rtsp地址："+rtsp);
            if(!GeneTool.isEmpty(rtsp)){
                url = rtsp;
                output = "//home//zhiyun//files/video//rtsp"+ TimeTool.getYMDHms()+".jpg";
                this.cutPic(url,output);
            }
        }
    }

    void cutPic(String url,String outputPath){
        try {
            // 创建抓取器
            FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(url);
            grabber.setOption("rtsp_transport", "tcp"); // RTSP 建议使用 TCP，减少丢包
            grabber.start();
            // 抓取一帧图像
            Frame frame = grabber.grabImage();
            log.info("play抓取一帧图像："+frame);
            if (frame != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                log.info("抓取一帧图像bufferedImage"+bufferedImage);
                if (bufferedImage != null) {
                    File outputFile = new File(outputPath);
                    log.info("抓取一帧图像outputFile"+outputPath);
                    // 确保目录存在
                    if (!outputFile.getParentFile().exists()) {
                        outputFile.getParentFile().mkdirs();
                    }
                    ImageIO.write(bufferedImage, "jpg", outputFile);
                    log.info("成功已保存至: " + outputPath);
                }
            }

            grabber.stop();
            grabber.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void getMasMessage() {
        //增加日志信息
        Map<String,Object> vuMap=new HashMap<>();
        try {
            vuMap.put("operator","admin");
            vuMap.put("operateTime",new Date());
            vuMap.put("operateRef","MAS");
            vuMap.put("operateContent", "");
            String operateDetail="";
            String operateType="";

            //1.连接mas获取短信
            String response = masMessageServiceImpl.getMasMessage();

            //2.解析mas返回
            List<MasResponse> masResponseList=new ArrayList<>();
            JsonElement jsonResponse = JsonParser.parseString(response);
            if (jsonResponse.isJsonObject()) {
                // 处理对象逻辑
                MasErrEntity masErrEntity = JsonUtils.jsonTOBean(response, MasErrEntity.class);
                log.info("MAS访问错误："+response);
                throw new RuntimeException("MAS访问错误!");
            } else if (jsonResponse.isJsonArray()) {
                // 处理数组逻辑
                log.info("MAS数组："+response);
                masResponseList = JsonUtils.jsonTOList(response, MasResponse.class);
            }else{
                throw new RuntimeException("MAS短信接收发生未知错误!");
            }

            //3.保存返回数据
            if(null!=masResponseList && masResponseList.size()>0){
                operateDetail="接收短信共"+masResponseList.size()+"条";
                operateType= OperaEnums.mas_accept.getCode();
                for(int i=0;i<masResponseList.size();i++){
                    MasResponse masResponse = masResponseList.get(i);
                    //保存短信内容
                    masMessageServiceImpl.addMasMessage(masResponse);
                }
            }
            //4.获取截图


            //3.发起流程



            //4.保存日志
            /*vuMap.put("operateType", operateType);
            vuMap.put("operateDetail",operateDetail);
            operateRecordService.addRecord(vuMap);*/
        } catch (RuntimeException r) {
            /*vuMap.put("operateType", OperaEnums.mas_accept_err.getCode());
            vuMap.put("operateDetail",r.getMessage());
            operateRecordService.addRecord(vuMap);*/
            r.printStackTrace();
        } catch (Exception e) {
            /*vuMap.put("operateType", OperaEnums.mas_accept_err.getCode());
            vuMap.put("operateDetail","未知错误，请联系管理员！");
            operateRecordService.addRecord(vuMap);*/
            e.printStackTrace();
        }
    }


}
