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


    //mas相关预警 截图存放文件夹
    @Getter
    private static String  masFolder;
    @Value("${picture.mas.cut.folder}")
    public void setMasFolder(String masFolder) {
        FilePropertiesVo.masFolder = masFolder;
    }

    //mas相关预警 截图地址
    @Getter
    private static String  masCutUrl;
    @Value("${picture.mas.cut.url}")
    public void setMasCutUrl(String masCutUrl) {
        FilePropertiesVo.masCutUrl = masCutUrl;
    }

    //mas相关预警 截图地址
    @Getter
    private static String  fileCutUrl;
    @Value("${file.mas.cut.url}")
    public void setFileCutUrl(String fileCutUrl) {
        FilePropertiesVo.fileCutUrl = fileCutUrl;
    }

    //图片存放文件夹（根）
    @Getter
    private static String  rootFile;
    @Value("${images.upload.url}")
    public void setRootFile(String rootFile) {
        FilePropertiesVo.rootFile = rootFile;
    }

    @Getter
    private static String  imageUrl;
    @Value("${images.down.url}")
    public void setImageUrl(String imageUrl) {
        FilePropertiesVo.imageUrl = imageUrl;
    }


}
