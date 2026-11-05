package com.religion.zhiyun.back;

import com.religion.zhiyun.interfaces.entity.mas.MasEntity;
import com.religion.zhiyun.interfaces.entity.xueliang.PlayEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;

import java.sql.Timestamp;

public class Base64 {

    public static void main(String[] args) {
       /* byte[] bytes = "clientId:clientSecret".getBytes();
        String encoded = java.util.Base64.getEncoder().encodeToString(bytes);
        System.out.println(encoded);

        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        String data = encoder.encodeToString(bytes);*/
        String data ="{\"masId\":1000001463,\"masDeviceId\":\"33030497631320000170\",\"masMonitorTime\":\"Oct 29, 2026 4:27:05 PM\",\"masAcctTime\":\"Nov 4, 2026 11:06:40 PM\",\"masState\":\"01\",\"masVenueId\":10000090,\"masRemark\":\"\"}";
        MasEntity mas= new MasEntity();
        mas.setMasAcctTime(TimeTool.getTimestamp());

        String s = JsonUtils.beanToJson(data);
        System.out.println(s);
        MasEntity mass=JsonUtils.jsonTOBean(s,MasEntity.class);
        System.out.println(mass.getMasAcctTime());
         Timestamp masMonitorTime =  mass.getMasAcctTime();
        String startTime = TimeTool.getTimestamp(masMonitorTime);
        System.out.println(startTime);
        //结束时间
        masMonitorTime.setTime(masMonitorTime.getTime() + 10000l);
        String endTime = TimeTool.getTimestamp(masMonitorTime);
        System.out.println(endTime);


    }
}
