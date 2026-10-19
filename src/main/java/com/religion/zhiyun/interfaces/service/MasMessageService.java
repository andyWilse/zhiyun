package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.interfaces.entity.mas.MasResponse;

import java.text.ParseException;
import java.util.List;

public interface MasMessageService {

    /** 连接mas系统 **/
    String getMasMessage() throws Exception;

    /** 保存mas短信 **/
    void addMasMessage(MasResponse masResponse) throws ParseException;

}
