package com.religion.zhiyun.schedule;

import com.religion.zhiyun.schedule.service.SchedulesService;
import com.religion.zhiyun.utils.sms.call.VoiceCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SysSchedules {

    @Autowired
    private SchedulesService schedulesService;

    /** 每隔1分钟执行一次 **/
    @Scheduled(cron ="0 */5 * * * ?")
    public void AutoReport() {
        //自动上报
        //schedulesService.UrgentAutoReport();
    }

    /** 每天9点执行一次 **/
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

    /** 每天8:30-17:30 ,5分钟执行一次 **/
    @Scheduled(cron ="0 30/5 8-17 * * ? ")
    public void warnCall() {
        schedulesService.warnCallReport();
    }

}
