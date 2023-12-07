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


}
