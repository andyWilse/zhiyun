package com.religion.zhiyun.interfaces.service.impl;

import com.religion.zhiyun.interfaces.entity.xueliang.AccessToken;
import com.religion.zhiyun.interfaces.entity.xueliang.PlayEntity;
import com.religion.zhiyun.interfaces.service.XueLiangService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.ResultCode;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class XueLiangServiceImpl implements XueLiangService {

    @Value("xue.liang.client")
    private String client;
    @Value("xue.liang.username")
    private String username;
    @Value("xue.liang.password")
    private String password;
    @Value("xue.liang.baseUrl")
    private String baseUrl;


    /**
     * Token 认证示例
     * 根据用户名密码获取 token 认证信息
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {

        String token="";
        String url=baseUrl+"/v2/oauth/token";

        /**1.Header**/
        HttpHeader header=new HttpHeader();
        //加密字符串为clientId:clientSecret 的Base64 加密
        byte[] bytes = client.getBytes();
        String encoded = Base64.getEncoder().encodeToString(bytes);
        header.addParam("Authorization",encoded);
        header.addParam("Content-Type","application/x-www-form-urlencoded");//浏览器原生表单提交

        /**2.params***/
        HttpParamers params=new HttpParamers(HttpMethod.POST);
        params.addParam("username",username);//申请到的用户名
        params.addParam("password",password);//申请到的密码->小写 32 位 md5 加密
        params.addParam("grant_type","password");//授权类型，此处为 password
        params.addParam("scope","read+write");//授权范围，此处为 read+write
        params.addParam("client_id","xlzpt");//客户端 id，此处为 xlzpt
        params.addParam("client_secret","3b8e54d0da451dab");//客户端秘钥，此处为 3b8e54d0da451dab

        /**3.接口调用***/
        HttpService httpService=new HttpService(url);
        String response = httpService.service(url, params,header);
        if(GeneTool.isEmpty(response)){
            throw new RuntimeException("Token认证接口调用异常，请联系管理员");
        }

        /**4.结果解析***/
        AccessToken entity= JsonUtils.jsonTOBean(response, AccessToken.class);
        if(null!=entity){
            token=entity.getAccess_token();
        }

        return token;
    }

    /**
     * 实时视频播放 V3.0
     * @param channelId
     * @return
     */
    public AppResponse getPlaySync(String channelId){
        long codes= ResultCode.ERROR.code();
        String message="实时视频播放";

        String flv="";
        try {
            String url=baseUrl+"/v3/play/playSync";

            /**1.Header**/
            HttpHeader header=new HttpHeader();
            //授权信息访问业务数据请求头,Hearder 需要包含 token信息
            String accessToken = this.getAccessToken();
            header.addParam("Authorization",accessToken);
            header.addParam("Content-Type","application/x-www-form-urlencoded");//浏览器原生表单提交

            /**2.params***/
            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.addParam("channelId",channelId);//通道编码
            /**3.接口调用***/
            HttpService httpService=new HttpService(url);
            String response = httpService.service(url, params,header);

            /**4.结果解析***/
            PlayEntity entity= JsonUtils.jsonTOBean(response, PlayEntity.class);
            if(null!=entity){
                String code = entity.getCode();
                if("0".equals(code)){
                    codes= ResultCode.SUCCESS.code();
                    message="播放成功";
                    flv=entity.getFlv();
                }else if("301".equals(code)){
                    throw new RuntimeException("当前通道繁忙");
                }else if("403".equals(code)){
                    throw new RuntimeException("无权限访问");
                }else if("500".equals(code)){
                    throw new RuntimeException("播放设备异常");
                }else{
                    throw new RuntimeException("访问异常，请联系管理员！");
                }
            }

        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return new AppResponse(codes,message,flv);
    }
}
