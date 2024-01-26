package com.religion.zhiyun.test202302;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTest {
        public static void main(String[] args) {
            //总毫秒数  从1970年1月1日开始计算
            long totalMilisSeconds = System.currentTimeMillis();
            //总秒数
            long totalSeconds = totalMilisSeconds / 1000;
            //当前秒数
            long currentSeconds = totalSeconds % 60;
            //总分钟
            long totalMinutes = totalSeconds / 60;
            //当前分钟
            long currentMinutes = totalMinutes % 60;
            //总小时（中国时区需加8小时）
            long totalHours = totalMinutes / 60 + 8;
            //当前小时
            long currentHours = totalHours % 24;
            //总天数
            long totalDays = totalHours / 24;
            Date date = new Date();
            System.out.println("总毫秒数："+totalMilisSeconds);
            System.out.println("总秒数："+totalSeconds);
            System.out.println("总分钟数："+totalMinutes);
            System.out.println("总小时："+totalHours);
            System.out.println("当前秒："+currentSeconds);
            System.out.println("当前分钟数："+currentMinutes);
            System.out.println("当前小时："+currentHours);
            System.out.println("总天数："+totalDays);
            SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(date);
            System.out.println(sdFormatter.format(date));
        }


}



