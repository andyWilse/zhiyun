package com.religion.zhiyun.interfaces.service.impl;

import com.religion.zhiyun.interfaces.entity.xueliang.AccessToken;
import com.religion.zhiyun.interfaces.entity.xueliang.PlayEntity;
import com.religion.zhiyun.interfaces.service.XueLiangService;
import com.religion.zhiyun.monitor.dao.MonitorBaseMapper;
import com.religion.zhiyun.monitor.dao.MonitorStatMapper;
import com.religion.zhiyun.monitor.entity.MonitorStatEntity;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.ResultCode;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class XueLiangServiceImpl implements XueLiangService {

    @Value("${xue.liang.client}")
    private String client;
    @Value("${xue.liang.username}")
    private String username;
    @Value("${xue.liang.password}")
    private String password;
    @Value("${xue.liang.baseUrl}")
    private String baseUrl;


    @Autowired
    private MonitorBaseMapper monitorBaseMapper;

    @Autowired
    private MonitorStatMapper monitorStatMapper;

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
        //byte[] bytes = client.getBytes();
        //String encoded = Base64.getEncoder().encodeToString(bytes);
        header.addParam("Authorization","Basic eGx6cHQ6M2I4ZTU0ZDBkYTQ1MWRhYg==");
        header.addParam("Content-Type","application/x-www-form-urlencoded");//浏览器原生表单提交

        /**2.params***/
        HttpParamers params=new HttpParamers(HttpMethod.POST);
        params.addParam("username",username);//申请到的用户名
        String hashedPwd1 = DigestUtils.md5DigestAsHex((password).getBytes());
        params.addParam("password",hashedPwd1);//申请到的密码->小写 32 位 md5 加密
        params.addParam("grant_type","password");//授权类型，此处为 password
        params.addParam("scope","read+write");//授权范围，此处为 read+write
        params.addParam("client_id","xlzpt");//客户端 id，此处为 xlzpt
        params.addParam("client_secret","3b8e54d0da451dab");//客户端秘钥，此处为 3b8e54d0da451dab
        //params.addParam("client_id","clientapp");
        //params.addParam("client_secret","123456");
        /**3.接口调用***/
        HttpService httpService=new HttpService(url);
        String response = httpService.service(url, params,header);
        if(GeneTool.isEmpty(response)){
            throw new RuntimeException("Token认证接口调用异常，请联系管理员");
        }
        /**4.结果解析***/
        AccessToken entity= JsonUtils.jsonTOBean(response, AccessToken.class);
        log.info("Token认证接口调用结果："+response);
        if(null!=entity){
            token=entity.getAccess_token();
        }
        log.info("Access_token:"+token);
        return token;
    }
    public AppResponse getPlaySync(String channelId){

        /*String response="{\"msg\":\"延迟:168,重新拉流:true\",\"code\":0,\"data\":{\"ssrc\":\"330300-01672871149124476929\",\"deviceID\":\"33030002002000010019\",\"cahnnelId\":\"33030461631322201980\",\"flv\":\"http://172.20.23.35:2080/rtp/330300-01672871149124476929.flv\",\"ws_flv\":\"ws://172.20.23.35:2080/rtp/330300-01672871149124476929.flv\",\"httpsFlv\":\"https://xlspgx.wenzhou.gov.cn:2443/rtp/330300-01672871149124476929.flv\",\"wss_flv\":\"wss://xlspgx.wenzhou.gov.cn:2443/rtp/330300-01672871149124476929.flv\",\"rtmp\":\"rtmp://172.20.23.35:1935/rtp/330300-01672871149124476929\",\"hls\":\"\",\"rtsp\":\"rtsp://172.20.23.35:1554/rtp/330300-01672871149124476929\",\"tracks\":\"\",\"callId\":\"4777764921290134314451934f9eba1a@172.20.23.34\",\"ip\":\"172.20.23.35\",\"createTime\":\"2023-06-25 15:35:37\",\"mediaPort\":\"61944\",\"forwardPort\":\"\"}}\n";
        response="{\"msg\":\"当前设备通道离线\",\"code\":500}";
        PlayEntity entity= JsonUtils.jsonTOBean(response, PlayEntity.class);
        String code = entity.getCode();
        String msg = entity.getMsg();
        String data = entity.getData();
        PlayEntity datas= JsonUtils.jsonTOBean(data, PlayEntity.class);
        MonitorStatEntity en=new MonitorStatEntity();
        en.setCode(code);
        en.setMsg(msg);
        en.setData(data);
        monitorStatMapper.add(en);*/

        List<Map<String, Object>> allMonitors = monitorBaseMapper.getAllMonitors();
        if(null!=allMonitors && allMonitors.size()>0){
            for(int i=0;i<allMonitors.size();i++){
                Map<String, Object> map = allMonitors.get(i);
                String cc = (String) map.get("channelId");
                this.getPlaySyn(cc);
            }

        }
        return new AppResponse(200l,"共："+allMonitors.size());
    }
    /**
     * 实时视频播放 V3.0
     * @param channelId
     * @return
     */
    public AppResponse getPlaySyn(String channelId){
        long codes= ResultCode.ERROR.code();
        String message="实时视频播放";

        String flv="";
        try {
            String url=baseUrl+"/v3/play/playSync";

            /**1.Header**/
            HttpHeader header=new HttpHeader();
            //授权信息访问业务数据请求头,Hearder 需要包含 token信息
            String accessToken = this.getAccessToken();
            //System.out.println("accessToken:"+accessToken);
            header.addParam("Authorization","Bearer "+accessToken);
            header.addParam("Content-Type","application/x-www-form-urlencoded");//浏览器原生表单提交

            /**2.params***/
            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.addParam("channelId",channelId);//通道编码
            /**3.接口调用***/
            HttpService httpService=new HttpService(url+"/"+channelId);
            String response = httpService.service(url+"/"+channelId, params,header);

            /**4.结果解析***/
            System.out.println("结果接收："+response);
            PlayEntity entity= JsonUtils.jsonTOBean(response, PlayEntity.class);

            if(null!=entity){
                String code = entity.getCode();
                message=entity.getMsg();
                String data = entity.getData();
                MonitorStatEntity en=new MonitorStatEntity();
                en.setCode(code);
                en.setMsg(message);
                en.setData(data);
                monitorStatMapper.add(en);


                /*if("0".equals(code)){
                    codes= ResultCode.SUCCESS.code();
                    message="播放成功";

                    String data = entity.getData();
                    if(!GeneTool.isEmpty(data)){
                        PlayEntity da= JsonUtils.jsonTOBean(data, PlayEntity.class);
                        flv=da.getFlv();
                    }

                }else if("301".equals(code)){
                    throw new RuntimeException("当前通道繁忙");
                }else if("403".equals(code)){
                    throw new RuntimeException("无权限访问");
                }else if("500".equals(code)){
                    throw new RuntimeException("播放设备异常");
                }else{
                    throw new RuntimeException("访问异常，请联系管理员！");
                }*/
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
