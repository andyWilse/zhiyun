package com.religion.zhiyun.interfaces.service.impl;

import com.religion.zhiyun.interfaces.entity.*;
import com.religion.zhiyun.interfaces.service.MinZonService;
import com.religion.zhiyun.task.config.TaskParamsEnum;
import com.religion.zhiyun.task.dao.TaskInfoMapper;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.ResultCode;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Random;

@Service
public class MinZonServiceImpl implements MinZonService {

    @Value("min.zon.baseUrl")
    private String baseUrl;

    @Value("min.zon.appId")
    private String appId;

    @Value("min.zon.appSecret")
    private String appSecret;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Override
    public AuthorEntity getAuthorize() throws Exception {
        String authorizeUrl=baseUrl+"/oauth2/authorize";
        String authorizeCode="";//返回值

        //请求参数
        String code="";
        String state= String.valueOf(new Random().nextInt(999999));

        //1.请求参数封装
        HttpParamers params=new HttpParamers(HttpMethod.GET);
        params.addParam("appId",appId);//服务端分配的客户端id
        params.addParam("responseType",code);//此值固定传：code
        //用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，
        // 可设置为简单的随机数加session进行校验
        params.addParam("state",state);

        //2.结果解析
        HttpService httpService=new HttpService(authorizeUrl);
        String response = httpService.service(authorizeUrl, params);
        AuthorEntity entity=JsonUtils.jsonTOBean(response,AuthorEntity.class);
        return entity;
    }

    @Override
    public TokenEntity getToken(String authorizeCode) throws Exception {
        String tokenUrl=baseUrl+"/oauth2/token";
        String token="";//返回值

        //请求参数
        String grantType="";
        String tenantId="";
        //1.请求参数封装
        HttpParamers params=new HttpParamers(HttpMethod.GET);
        params.addParam("appId",appId);//服务端分配的客户端id
        params.addParam("appSecret",appSecret);//服务端分配的客户端秘钥
        params.addParam("authorizeCode",authorizeCode);//上一步获取到的授权码
        params.addParam("grantType",grantType);//此值固定传：authorization_code
        params.addParam("tenantId",tenantId);//租户id

        //2.结果解析
        HttpService httpService=new HttpService(tokenUrl);
        String response = httpService.service(tokenUrl, params);
        TokenEntity entity=JsonUtils.jsonTOBean(response,TokenEntity.class);
        return entity;
    }

    @Override
    public AppResponse submitEvent(String procInstId,String token){
        String submitUrl=baseUrl+"/open/v1/submit";

        long codes=ResultCode.ERROR.code();
        String message="民宗快响：事件上报";

        try {
            /**3. 事件上报**/
            /**3.1. head参数处理**/
            HttpHeader header = this.getHttpHeader();

            /**3.2.请求参数封装 json**/
            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.addParam("params",this.getParam(procInstId,token));

            /**3.3.接口调用**/
            HttpService httpService=new HttpService(submitUrl);
            String submitResponse = httpService.service(submitUrl, params, header);
            BaseEntity baseEntity = JsonUtils.jsonTOBean(submitResponse, BaseEntity.class);
            Boolean success=baseEntity.isSuccess();
            String msg=baseEntity.getMsg();

            /**3.4.结果分析**/
            if(!success){
                throw new RuntimeException("民宗快响事件上报调用,响应失败:"+msg);
            }

            codes=ResultCode.SUCCESS.code();
            message="民宗快响：事件上报成功";

        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new AppResponse(codes,message);
    }

    @Override
    public AppResponse finishEvent(String procInstId, String token) {
        String finishUrl=baseUrl+"/open/v1/finish";
        long codes=ResultCode.ERROR.code();
        String message="民宗快响：事件办结";

        try {
            /**1.请求头*/
            HttpHeader header = this.getHttpHeader();
            /**2.请求参数封装 json**/
            HttpParamers params=new HttpParamers(HttpMethod.POST);
            FinishEntity vo=new FinishEntity();

            //获取上报人
            String loginNm = stringRedisTemplate.opsForValue().get(token);
            if(loginNm.isEmpty()){
                throw new RuntimeException("登录过期，请重新登陆！");
            }
            vo.setFinishTime(TimeTool.getYmdHms());
            vo.setPhone(loginNm);
            params.addParam("params",JsonUtils.beanToJson(vo));

            /**3.接口调用**/
            HttpService httpService=new HttpService(finishUrl);
            String submitResponse = httpService.service(finishUrl, params, header);
            BaseEntity baseEntity = JsonUtils.jsonTOBean(submitResponse, BaseEntity.class);
            Boolean success=baseEntity.isSuccess();
            String msg=baseEntity.getMsg();

            /**4.结果分析**/
            if(!success){
                throw new RuntimeException("民宗快响事件办结调用,响应失败:"+msg);
            }

            codes=ResultCode.SUCCESS.code();
            message="民宗快响：事件办结成功";
        } catch (RuntimeException r) {
            message=r.getMessage();
            r.printStackTrace();
        } catch (Exception e) {
            message=e.getMessage();
            e.printStackTrace();
        }

        return new AppResponse(codes,message);
    }

    @Override
    public SceneEntity getScene(String code,String type) throws Exception {
        String sceneUrl=baseUrl+"/open/v1/scene";
        HttpHeader header = this.getHttpHeader();
        HttpParamers params=new HttpParamers(HttpMethod.GET);
        if("01".equals(type)){
            params.addParam("eventType",code);
        }else if("02".equals(type)){
            params.addParam("eventType",code);
        }else if("03".equals(type)){
            params.addParam("eventType",code);
        }
        HttpService httpService=new HttpService(sceneUrl);
        String service = httpService.service(sceneUrl, params,header);
        return JsonUtils.jsonTOBean(service,SceneEntity.class);
    }

    @Override
    public DictBizEntity getDictBiz(String code) throws Exception {
        String dictUrl=baseUrl+"/open/v1/dict‑biz";

        HttpHeader httpHeader = this.getHttpHeader();
        HttpParamers params=new HttpParamers(HttpMethod.GET);
        params.addParam("code",code);//字典编码
        HttpService httpService=new HttpService(dictUrl);
        String service = httpService.service(dictUrl, params,httpHeader);
        return JsonUtils.jsonTOBean(service,DictBizEntity.class);
    }

    /**
     * 民宗快响,业务接口：请求头
     * @return
     * @throws Exception
     */
    public HttpHeader getHttpHeader() throws Exception {
        String authorizeCode ="";//授权码
        String accessToken ="";//token
        /** 1. 获取授权码 **/
        //1.1.获取授权码
        AuthorEntity authorize = this.getAuthorize();
        Boolean success=authorize.isSuccess();
        String msg = authorize.getMsg();
        int code= authorize.getCode();
        String states = authorize.getState();

        //1.2.返回数据解析
        if(success){
            authorizeCode = authorize.getAuthorizeCode();
        }else{
            throw new RuntimeException("民宗快响获取授权码调用,响应失败:"+msg);
        }
        /** 2. 获取token **/
        //2.1.获取token
        TokenEntity tokenEntity = this.getToken(authorizeCode);
        code = tokenEntity.getCode();//状态码
        Object data = tokenEntity.getData();//承载数据
        String tokenType = tokenEntity.getToken_type();//token类型
        String expiresIn = tokenEntity.getExpires_in();//有效期：默认24小时
        msg = tokenEntity.getMsg();//返回消息
        success = tokenEntity.isSuccess();//是否成功
        //2.2.返回数据解析
        if(success){
            accessToken=tokenEntity.getAccess_token();;
        }else{
            throw new RuntimeException("民宗快响获取token调用,响应失败:"+msg);
        }

        /**3. 事件上报**/
        /**3.1. head参数处理**/
        //authorization生成规则：appId、appSecret组合后转换成的Base64编码
        String param=appId+appSecret;
        String authorizationHead=Base64.getEncoder().encodeToString(param.getBytes());
        //获取token接口返回的token_type与access_token两个值拼接，中间以空格隔开
        String bladeAuthHead=tokenType+" "+accessToken;
        HttpHeader header=new HttpHeader();
        header.addParam("Authorization",authorizationHead);
        header.addParam("Blade‑Auth",bladeAuthHead);

        return header;
    }

    /**
     * 封装上报数据
     * @return
     */
    public String getParam(String procInstId,String token) throws Exception {
        if(procInstId.isEmpty()){
            throw new RuntimeException("流程id丢失，请联系管理员！");
        }
        //获取上报人
        String loginNm = stringRedisTemplate.opsForValue().get(token);
        if(loginNm.isEmpty()){
            throw new RuntimeException("登录过期，请重新登陆！");
        }

        //获取事件基本信息
        SubmitEntity submitEvent = taskInfoMapper.getSubmitEvent(procInstId, loginNm);
        if(null==submitEvent){
            throw new RuntimeException("事件基本信息丢失，请联系管理员！");
        }
        //获取场景
        String code="";//1-突发事件;2-网络舆情;3-重大任务交办;4-事件通报;
        String type="01";
        String flowType = submitEvent.getFlowType();
        if(TaskParamsEnum.TASK_FLOW_TYPE_05.getCode().equals(flowType)){
            code="1";
        }else if(TaskParamsEnum.TASK_FLOW_TYPE_02.getCode().equals(flowType)){
            code="3";
        }else if(TaskParamsEnum.TASK_FLOW_TYPE_01.getCode().equals(flowType)
                || TaskParamsEnum.TASK_FLOW_TYPE_03.getCode().equals(flowType)
                || TaskParamsEnum.TASK_FLOW_TYPE_04.getCode().equals(flowType)
                || TaskParamsEnum.TASK_FLOW_TYPE_07.getCode().equals(flowType)
        ){
            code="4";
        }else{
            code="0";
        }
        SceneEntity scene = this.getScene(code, type);
        String msg = scene.getMsg();
        boolean success = scene.isSuccess();
        if(success){
            submitEvent.setEventType(Integer.parseInt(code));
            submitEvent.setSceneId(Integer.parseInt(scene.getKey()));
        }else{
            throw new RuntimeException("民宗快响业务场景调用,响应失败:"+msg);
        }

        //事件等级
        int eventLevel=0;
        String towncode = submitEvent.getTowncode();
        String adcode = submitEvent.getAdcode();
        String citycode = submitEvent.getCitycode();
        if(!towncode.isEmpty()){
            eventLevel=2;
        }else if(!adcode.isEmpty()){
            eventLevel=3;
        }else{
            eventLevel=1;
        }
        submitEvent.setLevel(eventLevel);

        //来源
        submitEvent.setOrigion("3");

        return JsonUtils.beanToJson(submitEvent);
    }
}
