package com.religion.zhiyun.schedule;

import com.religion.zhiyun.schedule.service.SchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    @Scheduled(cron ="0 0 9 * * ?")
    public void AutoFill() {
        //普通通知
        //schedulesService.CommonAutoFill();
    }

    /** 每天8:30-17:30 ,5分钟执行一次 **/
    @Scheduled(cron ="0 30/5 8-17 * * ? ")
    public void warnCall() {
        schedulesService.warnCallReport();
    }

}
