package com.religion.zhiyun.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RespPageBean {

    private Long total;
    private List<?> data;
    private Object[] datas;
    private Object[] dataone;

    private String result;
    private long code;

}
