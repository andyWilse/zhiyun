package com.religion.zhiyun.utils.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTool {
    public static String nowtime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(Calendar.getInstance().getTime());

    public static String ymdHms="yyyy-MM-dd HH:mm:ss";

    public static String getYmdHms(){
        Date date = new Date();
        SimpleDateFormat format=new SimpleDateFormat(ymdHms);
        String format1 = format.format(date);
        return format1;
    }
}
