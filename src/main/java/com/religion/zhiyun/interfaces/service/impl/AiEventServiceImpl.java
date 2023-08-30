package com.religion.zhiyun.interfaces.service.impl;

import com.religion.zhiyun.interfaces.entity.ai.AiAuthorEntity;
import com.religion.zhiyun.interfaces.entity.ai.AiImageEntity;
import com.religion.zhiyun.interfaces.service.AiEventService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

public class AiEventServiceImpl implements AiEventService {
    @Value("${file.ai.username}")
    private String  userNameAI;

    @Value("${file.ai.password}")
    private String  passWordAI;

    @Value("${file.ai.image.url}")
    private String  baseUrl;

    @Override
    public String getAiFile(String fileName) throws Exception,RuntimeException {
        String downloadUrl ="";

        /**1.获取授权**/
        String authorizeUrl=baseUrl+"/auth/oauth/token";

        //1.1.HttpHeader参数封装
        String authorization="Basic dGVzdDp0ZXN0";
        String contentType="application/x-www-form-urlencoded";
        HttpHeader header=new HttpHeader();
        header.addParam("Authorization",authorization);
        header.addParam("Content-Type",contentType);
        //1.2.body参数封装
        HttpParamers params=new HttpParamers(HttpMethod.POST);
        params.addParam("grant_type","password");//授权类型，"password"
        params.addParam("username",userNameAI);
        params.addParam("password",passWordAI);
        //1.3.发送请求
        HttpService httpService=new HttpService(authorizeUrl);
        String response = httpService.service(authorizeUrl, params,header);
        //1.4.结果解析
        AiAuthorEntity entity= JsonUtils.jsonTOBean(response, AiAuthorEntity.class);
        String accessToken = entity.getAccess_token();

        /**2.获取图⽚接⼝**/
        String getPictureUrl=baseUrl+"/v1/gb/algorithm/image";
        //2.1.HttpHeader参数封装
        HttpHeader picHeader=new HttpHeader();
        String authorizationPic="Bearer "+accessToken;
        picHeader.addParam("Authorization",authorizationPic);
        //固定为：multipart/form-data
        picHeader.addParam("Content-Type","multipart/form-data");
        //1.2.body参数封装
        HttpParamers picParams=new HttpParamers(HttpMethod.GET);
        picParams.addParam("storageId", TimeTool.getYmdHms());//存储桶
        picParams.addParam("fileName",fileName);
        //1.3.发送请求
        HttpService httpServicePic=new HttpService(getPictureUrl);
        String responsePic = httpServicePic.service(getPictureUrl, picParams,picHeader);
        //1.4.结果解析
        AiImageEntity entityImage= JsonUtils.jsonTOBean(responsePic, AiImageEntity.class);
        int code = entityImage.getCode();
        if(200==code){
            downloadUrl = entityImage.getDownloadUrl();
        }else{
            String msg = entityImage.getMsg();
            throw new RuntimeException(msg);
        }

        return downloadUrl;
    }
}
