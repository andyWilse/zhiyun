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

    @Value("${file.ai.image.down.url}")
    private String  downUrl;

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
            /*String[] split = eventFile.split("=");
            int length = split.length;
            String fileName = split[length - 1];*/

            String[] split = eventFile.split("&");
            String storage = split[0];
            String fm = split[1];
            String[] spli = storage.split("=");
            String storageId =spli[1];
            String[] split1 = fm.split("=");
            String fileName =split1[1];


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
            System.out.println("发送AI图片授权请求!开始------");
            HttpService httpService=new HttpService(authorizeUrl);
            String response = httpService.service(authorizeUrl, params,header);
            System.out.println("发送AI图片授权请求!结束------"+response);
            //1.4.结果解析
            AiAuthorEntity entity= JsonUtils.jsonTOBean(response, AiAuthorEntity.class);
            String accessToken = entity.getAccess_token();
            System.out.println("发送AI图片授权请求结果accessToken:"+accessToken);

            /**2.获取图⽚接⼝**/
            //String getPictureUrl=baseUrl+"/hook/v1/gb/algorithm/image";
            String getPictureUrl=baseUrl+"/img/v1/image";
            //2.1.HttpHeader参数封装
            HttpHeader picHeader=new HttpHeader();
            String authorizationPic="Bearer "+accessToken;
            picHeader.addParam("Authorization",authorizationPic);
            //固定为：multipart/form-data
            picHeader.addParam("Content-Type","multipart/form-data");
            //1.2.body参数封装
            HttpParamers picParams=new HttpParamers(HttpMethod.GET);
            picParams.addParam("storageId", storageId);//存储桶
            picParams.addParam("fileName",fileName);
            //1.3.发送请求
            System.out.println("发送AI!开始:storageId("+storageId+")fileName:("+fileName+")!-----");
            HttpService httpServicePic=new HttpService(getPictureUrl);
            String responsePic = httpServicePic.service(getPictureUrl, picParams,picHeader);
            System.out.println("发送AI!结束------"+responsePic);
            //1.4.结果解析
            AiImageEntity entityImage= JsonUtils.jsonTOBean(responsePic, AiImageEntity.class);
            int code = entityImage.getCode();
            System.out.println("发送AI图片请求结果code:"+code);
            if(200==code){
                String dataJson = entityImage.getData();
                AiImageEntity data= JsonUtils.jsonTOBean(dataJson, AiImageEntity.class);
                downloadUrl = data.getDownloadUrl();
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
