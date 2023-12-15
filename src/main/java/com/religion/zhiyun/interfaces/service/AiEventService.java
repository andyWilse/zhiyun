package com.religion.zhiyun.interfaces.service;

import com.religion.zhiyun.utils.response.AppResponse;

public interface AiEventService {
    /**
     *AI图片查看
     * @param fileId
     */
    AppResponse getAiFile(String fileId);

    /**
     * AI图片下载
     * @param filePath
     * @return
     */
    AppResponse downImage(String filePath,String fileId);
    /**
     * AI图片下载（历史）
     * @return
     */
    AppResponse initImage();
}
