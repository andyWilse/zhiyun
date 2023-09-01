package com.religion.zhiyun.interfaces.service.impl;

import com.religion.zhiyun.interfaces.entity.ai.AiAuthorEntity;
import com.religion.zhiyun.interfaces.entity.ai.AiImageEntity;
import com.religion.zhiyun.interfaces.service.AiEventService;
import com.religion.zhiyun.sys.file.dao.RmFileMapper;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.ResultCode;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class AiEventServiceImpl implements AiEventService {
    @Value("${file.ai.username}")
    private String  userNameAI;

    @Value("${file.ai.password}")
    private String  passWordAI;

    @Value("${file.ai.image.url}")
    private String  baseUrl;

    @Autowired
    private RmFileMapper rmFileMapper;

    @Override
    public AppResponse getAiFile(String fileId){
        long codes= ResultCode.ERROR.code();
        String message="AI图片获取失败！";
        String downloadUrl ="";
        try {
            List<Map<String, Object>> fileUrl = rmFileMapper.getFileUrl(fileId.split(","));
            String eventFile="";
            if(null!=fileUrl && fileUrl.size()>0){
                eventFile= (String) fileUrl.get(0).get("url");
            }else{
                throw new RuntimeException("AI图片信息丢失，请联系管理员！");
            }
            String[] split = eventFile.split("=");
            int length = split.length;
            String fileName = split[length - 1];

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
            codes= ResultCode.SUCCESS.code();
            message="AI图片获取成功！";
        } catch (RuntimeException e) {
            message=e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new AppResponse(codes,message,downloadUrl);
    }
}
