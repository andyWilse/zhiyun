package com.religion.zhiyun.utils.Tool;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeTool {
    public static String nowtime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(Calendar.getInstance().getTime());
}
