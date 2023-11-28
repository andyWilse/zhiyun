package com.religion.zhiyun.utils.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeneTool {

    /**
     * 字符串判空
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        boolean flag=false;

        if(value=="" || value==null || value.length()==0 || value=="undefined"){
            flag=true;
        }
        return flag;
    }

    /**
     * 判断时间是否处于某个时间段内
     *
     * @param time 需要比较的时间
     * @param from 起始时间
     * @param to 结束时间
     * @return
     */
    public static boolean calendarCompare(Date time, Date from, Date to) {
        Calendar date = Calendar.getInstance();
        date.setTime(time);
        Calendar after = Calendar.getInstance();
        after.setTime(from);
        Calendar before = Calendar.getInstance();
        before.setTime(to);
        if (date.after(after) && date.before(before)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时间比较
     * @param from
     * @param to
     * @return
     */
    public static boolean calendarCompare(String from, String to) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
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

        Boolean flag = calendarCompare(now, beginTime, endTime);
        return flag;
    }
}
