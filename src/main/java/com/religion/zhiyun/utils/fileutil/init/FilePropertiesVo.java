package com.religion.zhiyun.utils.fileutil.init;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilePropertiesVo {

    /**
     * 文件服务器地址
     */
    @Getter
    private static String  baseUrl;
    @Value("${images.down.url}")
    public void setBaseUrl(String baseUrl) {
        FilePropertiesVo.baseUrl = baseUrl;
    }

    /**
     * 图片存放位置
     */
    @Getter
    private static String  uploadUrl;
    @Value("${images.upload.url}")
    public void setUploadUrl(String uploadUrl) {
        FilePropertiesVo.uploadUrl = uploadUrl;
    }

    /**
     * 历史视频获取
     */
    @Getter
    private static String  streamUrl;
    @Value("${xue.liang.his.streamUrl}")
    public void setStreamUrl(String streamUrl) {
        FilePropertiesVo.streamUrl = streamUrl;
    }

    @Getter
    private static String  streamMode;
    @Value("${xue.liang.stream.mode}")
    public void setStreamModel(String streamMode) {
        FilePropertiesVo.streamMode = streamMode;
    }
    @Getter
    private static String  streamCode;
    @Value("${xue.liang.stream.code}")
    public void setStreamCode(String streamCode) {
        FilePropertiesVo.streamCode = streamCode;
    }
    @Getter
    private static String  streamTime;
    @Value("${xue.liang.stream.time}")
    public void setStreamTime(String streamTime) {
        FilePropertiesVo.streamTime = streamTime;
    }


}
