package com.religion.zhiyun.utils.Tool;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTool {

    public static String ymdHms="yyyy-MM-dd HH:mm:ss";
    public static String ymd="yyyyMMdd";
    public static String year="yyyy";
    public static String month="MM";
    public static String day="dd ";
    public static String ymdHmsTZ="yyyy-MM-dd'T'HH:mm:ss'Z'";


    public static String getYmdHms(){
        Date date = new Date();
        SimpleDateFormat format=new SimpleDateFormat(ymdHms);
        String format1 = format.format(date);
        return format1;
    }

    public static String getYmd(){
        Date date = new Date();
        SimpleDateFormat format=new SimpleDateFormat(ymd);
        String format1 = format.format(date);
        return format1;
    }

    public static String getYmdHmsTZ(){
        Date date = new Date();
        SimpleDateFormat format=new SimpleDateFormat(ymdHmsTZ);
        String format1 = format.format(date);
        return format1;
    }
    //获取当前年
    public static String getCurrentYear(Date date){
        SimpleDateFormat format=new SimpleDateFormat(year);
        String formatYear = format.format(date);
        return formatYear;
    }

    //获取当前月
    public static String getCurrentMonth(Date date){
        SimpleDateFormat format=new SimpleDateFormat(month);
        String formatMonth = format.format(date);
        return formatMonth;
    }

    //获取当前日
    public static String getCurrentDay(Date date){
        SimpleDateFormat format=new SimpleDateFormat(day);
        String formatDay= format.format(date);
        return formatDay;
    }

    //转换为date
    public static Date strToDate(String date) throws ParseException {
        SimpleDateFormat format=new SimpleDateFormat(day);
        Date strToDate = format.parse(date);
        return strToDate;
    }

    //转换为date
    public static Date strYmdHmsToDate(String date) throws ParseException {
        SimpleDateFormat format=new SimpleDateFormat(ymdHms);
        Date strToDate = format.parse(date);
        return strToDate;
    }
}
