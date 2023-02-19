package com.religion.zhiyun.group.service;


import com.religion.zhiyun.utils.response.RespPageBean;

public interface RmGroupInfoService {

    RespPageBean getPage(Integer page,Integer size,String groupName);
}
