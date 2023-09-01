package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.utils.response.AppResponse;

public interface AiEventService {
    /**
     *
     * @param fileId
     */
    AppResponse getAiFile(String fileId);
}
