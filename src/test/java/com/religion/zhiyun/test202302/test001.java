package com.religion.zhiyun.test202302;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.religion.zhiyun.user.entity.SysUserEntity;
import com.religion.zhiyun.utils.Tool.GeneTool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class test001 {
    public static void main(String[] args) throws IOException {
        String[] pa=new String[2];
        pa[0]="aa";
        pa[1]="bb";
        Gson gson = new Gson();
        String ss =gson.toJson(pa);
        System.out.println(ss);

        String sss = JSONArray.toJSONString(pa);
        System.out.println(sss);

        /*List<SysUserEntity> dataList=new ArrayList<>();
        System.out.println(dataList.toArray());*/
        /*Object a=null;
        String b= (String) a;
        System.out.println("ss"+b);*/

        //拨打时间限制在8:30-17:30
        /*SimpleDateFormat f=new SimpleDateFormat("hhmm");
        String format = f.format(new Date());
        int i = Integer.parseInt(format);
        if(i<1730 && i>830){
            System.out.println(format);
        }else{
            System.out.println(00);
            System.out.println(format);
        }*/
        /*Date now=new Date();
        SimpleDateFormat f=new SimpleDateFormat("HHMM");
        Date from=f.parse("08-30");
        Date to=f.parse("17-30");
        now = f.parse(f.format(new Date()));
        boolean b = GeneTool.belongCalendar(now, from, to);
        System.out.println(b);
        System.out.println(from);
        System.out.println(to);*/

       /* SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now =null;
        Date beginTime = null;
        Date endTime = null;
        try {
            now = df.parse(df.format(new Date()));
            beginTime = df.parse("08:30");
            endTime = df.parse("17:30");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Boolean flag = GeneTool.calendarCompare(now, beginTime, endTime);
        System.out.println(flag);
        System.out.println(beginTime);
        System.out.println(endTime);*/
    }
}
