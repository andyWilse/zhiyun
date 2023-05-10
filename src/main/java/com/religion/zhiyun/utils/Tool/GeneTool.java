package com.religion.zhiyun.utils.Tool;

public class GeneTool {
    public static boolean isEmpty(String value) {
        boolean flag=false;

        if(value=="" || value==null || value.length()==0 || value=="undefined"){
            flag=true;
        }
        return flag;
    }
}
