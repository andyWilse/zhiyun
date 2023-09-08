package com.religion.zhiyun.test202303;

import com.religion.zhiyun.task.entity.TaskEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@SpringBootTest
class ZhiyunApplicationTests {

    @Test
    void contextLoads() throws FileNotFoundException {

        /*String str="123";
        String s = DigestUtils.md5Hex(str);*/

        TaskEntity e=new TaskEntity();
        //e.setEndTime("2023-02-14 11:15:21");
        e.setEmergencyLevel("01");
        //e.setHandlePerson("");
        //e.setHandleResults("");
        //e.setHandleTime("");
        e.setLaunchPerson("aa1");
        e.setTaskContent("火灾告警");
        //e.setProcInstId();
        e.setRelVenuesId("10000001");
        e.setTaskName("任务上报-火灾预警");
        e.setTaskPicture("1111");
        e.setTaskType("事件上报任务流程");
        e.setTaskVideo("1111");

        System.out.println(JsonUtils.beanToJson(e));

    }

}
