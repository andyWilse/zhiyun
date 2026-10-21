package com.religion.zhiyun.schedule;

import com.religion.zhiyun.schedule.service.SchedulesService;
import com.religion.zhiyun.schedule.socket.VideoReceiver;
import com.religion.zhiyun.schedule.socket.VideoServer;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.propertie.Switch;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.sms.call.VoiceCall;
import com.religion.zhiyun.utils.sms.http.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SysSchedules {

    @Autowired
    private SchedulesService schedulesService;

    @Autowired
    private VideoServer videoServer;
    @Autowired
    private VideoReceiver videoReceiver;

    /** 自动上报：每隔1分钟执行一次 **/
    @Scheduled(cron ="0 */5 * * * ?")
    public void AutoReport() {
        //自动上报
        //schedulesService.UrgentAutoReport();
    }

    /**明火提醒： 每天9点执行一次 **/
    @Scheduled(cron ="0 20 17 * * ?")
    public void AutoFill() {
        //普通通知
        //schedulesService.CommonAutoFill();
       /* Map<String, Object> map=new HashMap<>();
        map.put("phone","18514260203");
        map.put("venuesAddres","浙江省温州市瓯海区郭溪街道凰桥村燎原中路226-228号");
        map.put("venuesName","陈岙基督教堂(潘桥)");
        map.put("event","明火");
        VoiceCall.voiceCall(map);*/
    }

    /** 电话提醒：每天8:30-17:30 ,5分钟执行一次 **/
    @Scheduled(cron ="0 30/5 8-17 * * ? ")
    public void warnCall() {
       // schedulesService.warnCallReport();
    }
    /** 每天上午9点执行一次 "0 0 9 * * ? "**/
    @Scheduled(cron ="0 0 9 * * ? ")
    public void metricRecord() {
        //schedulesService.metricRecord();
    }

    /** 每隔1分钟执行一次 **/
    @Scheduled(cron ="0 */1 * * * ?")
    public void repeatMessage() {
        //短信提醒查询提醒人：
        if(Switch.getAlert()){
            schedulesService.repeatMessage();
        }
    }

    /** 每隔1分钟执行一次 **/
    @Scheduled(cron ="0 */1 * * * ?")
    public void cutSyncSchedule() {
        if(Switch.getCut()){
            //1.实时视频截图
            schedulesService.cutSyncPicture();

            //2.下载历史视频：
            /*schedulesService.handleCutPicture();
            videoServer.startServer(7082);
            videoReceiver.receiveVideo("//home//zjzz//dump.bin");
            videoServer.stopServer();*/
        }
    }

    /** 每隔20秒执行一次 **/
    @Scheduled(cron ="0/20 * 8-17 * * ?")
    public void masSchedule() {
        //MAS短信获取
        if(Switch.getMas()){
            log.info("mas定时任务开始时间："+ TimeTool.getYmdHms());
            schedulesService.getMasMessage();
            log.info("mas定时任务结束时间："+ TimeTool.getYmdHms());
        }
    }

}
