package com.religion.zhiyun.sys.file.service;

import com.religion.zhiyun.sys.file.entity.FileEntity;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.PageResponse;
import com.religion.zhiyun.utils.response.RespPageBean;

import javax.servlet.http.HttpServletRequest;

public interface RmFileService {

    //文件新增
    int add(FileEntity fileEntity);

    //图片上传
    RespPageBean uploadImage(HttpServletRequest request);

    //图片展示
    RespPageBean showPicture(String picture);

    /**
     * 生成登录用户图片
     * @param token
     * @return
     */
    AppResponse buildUserPic(String token);

    /**
     * 图片清理
     * @param pictures
     */
    void deletePicture(String pictures);

    /**
     * 获取图片地址
     * @param picPath
     * @return
     */
    PageResponse getFile(String picPath);

}
