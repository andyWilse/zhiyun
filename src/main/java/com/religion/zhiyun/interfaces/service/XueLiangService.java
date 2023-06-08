package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.utils.response.AppResponse;

public interface XueLiangService {

    /**Token 认证示例**/
    String getAccessToken() throws Exception;

    /**实时视频播放**/
    AppResponse getPlaySync(String channelId);
}
