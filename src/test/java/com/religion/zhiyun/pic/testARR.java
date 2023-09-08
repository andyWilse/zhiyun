package com.religion.zhiyun.pic;

import com.religion.zhiyun.staff.entity.StaffEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class testARR {
    public static void main(String[] args) {

       /* Map<String,Object> map=new HashMap<>();
        List<StaffEntity> l=new ArrayList<>();
        StaffEntity s=new StaffEntity();
        s.setStaffId(20000001);
        s.setStaffName("11");
        s.setStaffTelphone("22");
        l.add(s);
        System.out.println(l.toArray());*/
        String aa="1,1,2,3,4";
        System.out.println(aa.replace("，",","));
        System.out.println(aa.replace(",",","));



    }
}
