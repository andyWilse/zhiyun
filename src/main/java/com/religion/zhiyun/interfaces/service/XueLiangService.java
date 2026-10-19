package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.interfaces.entity.xueliang.HisPlayReqParam;
import com.religion.zhiyun.interfaces.entity.xueliang.PlayEntity;
import com.religion.zhiyun.utils.response.AppResponse;

import java.util.Map;

public interface XueLiangService {

    /**Token 认证示例**/
    String getAccessToken(String username,String password,String targetUrl) throws Exception;

    /**实时视频播放**/
    PlayEntity getPlaySync(String channelId)throws Exception;

    /**更新所有通道状态**/
    AppResponse getPlaySyncInit();

    /*视频地址获取*/
    AppResponse getPlaySyncOne(String channelId);

    AppResponse getPlays(Map<String,Object> map);

    /* 下载历史视频*/
    String downPlayHistory(HisPlayReqParam hisPlayReqParam) throws Exception;
}
