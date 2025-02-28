package com.religion.zhiyun.utils.sms.sm;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.sms.call.util.HwCallVo;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import com.religion.zhiyun.utils.sms.sm.util.SendParam;
import org.springframework.http.HttpMethod;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageSend {

    private static String CONTENT_TYPE="application/json;charset=utf-8";

    /**
     * 单挑短信发送
     * @param messageList
     * @return
     */
    public static String sendSmsOne(List<Map<String,Object>> messageList){

        String message="";//返回信息

        String appKey= HwCallVo.getAppKey();
        String appSecret=HwCallVo.getAppSecret();

        String smsUrl= SendParam.getSmsUrl();//请求url
        String username=SendParam.getUserName();//用户名
        String password=SendParam.getPassword();//密码
        String oneSend = SendParam.getOneSend();//api
        long timestamp = System.currentTimeMillis();//获取时间戳

        //请求地址
        String sendUrl=smsUrl+oneSend;
        System.out.println("第一次smsUrl："+sendUrl);
        try {
            /*** 1.HttpHeader参数封装 ***/
            HttpHeader header=new HttpHeader();
            //固定填写为application/json;charset=UTF-8。
            header.addParam("Content-Type",CONTENT_TYPE);
            //application/json
            header.addParam("Accept","application/json");

            //body参数封装
            Map<String, Object> bodys = new HashMap();
            bodys.put("userName",username);
            //数组形式，包含多个 JSON 对象，对象参数见下表。每个 JSON 对象包含短信内容和号码数据，最大 1000 个号码。
            bodys.put("messageList",messageList.toArray());
            //当前时间戳，精确到毫秒。例如 2020 年 8 月 1 日 12:00:00 时间戳为：1596254400000 ；类型Long
            bodys.put("timestamp",timestamp);
            //计算：MD5(userName + timestamp + MD5(password))
            String pw = DigestUtils.md5DigestAsHex((password).getBytes());
            String sign = DigestUtils.md5DigestAsHex((username+timestamp+pw).getBytes());
            bodys.put("sign", sign);
            //短信定时发送时间，格式：yyyy-MM-dd HH:mm:ss。定时时间限制 15 天以内。
            String ymdHms = TimeTool.getYmdHms();
            bodys.put("sendTime", ymdHms);

            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.setJsonParamer(bodys);

            /*** 3.调用接口 ***/
            HttpService httpService=new HttpService(sendUrl);
            String response = httpService.service(sendUrl, params, header);

            System.out.println("单条短信响应结果："+response);
            //结果解析
            if(null!=response && ""!=response){
                Map<String, Object> mapResponse = JsonUtils.jsonToMap(response);
                //获取码值
                Double coded = (Double) mapResponse.get("code");
                int code=coded.intValue();
                String messages = (String) mapResponse.get("message");
                if(0==code){
                    message="发送短信成功";
                }else{
                    message="发送短信失败！"+messages;
                }
            }

        } catch (Exception e) {
            message="发送短信失败！"+e.getMessage();
            e.printStackTrace();
        }

        return message;
    }


    /**
     * 批量短信发送
     * @param content
     * @param mobiles
     * @return
     */
    public static String sendSmsMass(String content, List<String> mobiles){
        String message="";//返回信息

        String smsUrl= SendParam.getSmsUrl();//请求url
        String username=SendParam.getUserName();//用户名
        String password=SendParam.getPassword();//密码
        String massSend = SendParam.getMassSend();//api
        long timestamp = System.currentTimeMillis();//获取时间戳
        String ymdHms = TimeTool.getYmdHms();

        //请求地址
        String sendUrl=smsUrl+massSend;
        System.out.println("批量短信url:"+sendUrl);
        try {
            /*** 1.HttpHeader参数封装 ***/
            HttpHeader header=new HttpHeader();
            //固定填写为application/json;charset=UTF-8。
            header.addParam("Content-Type",CONTENT_TYPE);
            //application/json
            header.addParam("Accept","application/json");

            //body参数封装
            Map<String, Object> bodys = new HashMap();
            bodys.put("userName",username);
            bodys.put("content",content);
            //发送手机号码，JSON 数组格式。最大数量不得超过 10000 个号码，系统将自动去除重复号码。
            bodys.put("phoneList",mobiles.toArray());
            //当前时间戳，精确到毫秒。例如 2020 年 8 月 1 日 12:00:00 时间戳为：1596254400000 ；类型Long
            bodys.put("timestamp",timestamp);
            //计算：MD5(userName + timestamp + MD5(password))
            String pw = DigestUtils.md5DigestAsHex((password).getBytes());
            String sign = DigestUtils.md5DigestAsHex((username+timestamp+pw).getBytes());
            bodys.put("sign", sign);
            //短信定时发送时间，格式：yyyy-MM-dd HH:mm:ss。定时时间限制 15 天以内。
            bodys.put("sendTime", ymdHms);
            //可选，附带通道扩展码
            //bodys.put("extcode", "");
            //用户回传数据，最大长度 64。用户若传递此参数将在回执推送时回传给用户。
            //bodys.put("callData", "");

            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.setJsonParamer(bodys);

            /*** 3.调用接口 ***/
            HttpService httpService=new HttpService(sendUrl);
            String response = httpService.service(sendUrl, params, header);

            System.out.println("批量短信响应结果："+response);
            //结果解析
            if(null!=response && ""!=response){
                Map<String, Object> mapResponse = JsonUtils.jsonToMap(response);
                //获取码值
                String messages = (String) mapResponse.get("message");
                Double coded = (Double) mapResponse.get("code");
                int code=coded.intValue();
                if(0==code){
                    message="发送短信成功";
                }else{
                    message="发送短信失败！"+messages;
                }
            }

        } catch (Exception e) {
            message="发送短信失败！"+e.getMessage();
            e.printStackTrace();
        }

        return message;
    }
}
