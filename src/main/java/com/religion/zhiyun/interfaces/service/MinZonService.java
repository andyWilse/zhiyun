package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.interfaces.entity.minzong.*;
import com.religion.zhiyun.utils.response.AppResponse;

public interface MinZonService {

    /** 民宗快响：获取授权码 **/
    public BaseEntity getAuthorize() throws Exception;

    /** 民宗快响：获取token **/
    public TokenEntity getToken(String authorizeCode) throws Exception;

    /** 民宗快响,业务接口：1.事件上报 **/
    public AppResponse submitEvent(String procInstId,String token);

    /** 民宗快响,业务接口：2.事件办结 **/
    public AppResponse finishEvent(String procInstId,String token);

    /** 民宗快响,业务接口：3.获取业务场景 **/
    public SceneEntity getScene(String code,String type) throws Exception;

    /** 民宗快响,业务接口：4.业务字典 **/
    public DictBizEntity getDictBiz(String code) throws Exception;

    /** 推送民宗快响：数据统计 **/
    public AppResponse mzSubmitSum();


}
