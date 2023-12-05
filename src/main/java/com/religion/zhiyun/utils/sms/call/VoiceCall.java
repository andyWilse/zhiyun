package com.religion.zhiyun.utils.sms.call;

import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.sms.call.util.AkskUtil;
import com.religion.zhiyun.utils.sms.call.util.CallResponse;
import com.religion.zhiyun.utils.sms.call.util.HwCallVo;
import com.religion.zhiyun.utils.sms.call.util.PlayContentInfo;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParamers;
import com.religion.zhiyun.utils.sms.http.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.util.*;

@Slf4j
public class VoiceCall {
    private static String CONTENT_TYPE="application/json;charset=UTF-8";
    public static  String AUTHORIZATION = "AKSK realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    public static String voiceCall(Map<String,Object> map){

        String sessionId="";
        try {
            //参数
            String calleeNbr="";
            String venuesAddres="";
            String venuesName="";
            String event="";
            if(null!=map){
                calleeNbr= (String) map.get("phone");
                venuesAddres= (String) map.get("venuesAddres");
                venuesName= (String) map.get("venuesName");
                event= (String) map.get("event");
            }

            /*** 1.HttpHeader参数封装 ***/
            HttpHeader header=new HttpHeader();
            //固定填写为application/json;charset=UTF-8。
            header.addParam("Content-Type",CONTENT_TYPE);
            //固定填写为AKSK realm="SDP",profile="UsernameToken",type="Appkey"。
            header.addParam("Authorization",AUTHORIZATION);
            //X-AKSK 封装
            String appKey= HwCallVo.getAppKey();
            String appSecret=HwCallVo.getAppSecret();
            String aksk = AkskUtil.buildWsseHeader(appKey, appSecret);
            header.addParam("X-AKSK",aksk);

            /** 2.body参数封装 **/
            Map<String, Object> bodys = new HashMap();
            //固话号码，被叫终端上显示的主叫号码
            bodys.put("displayNbr", HwCallVo.getDisplayNbr());//必输
            //被叫号码。
            bodys.put("calleeNbr", calleeNbr);//必输
            //播放信息列表，最大支持5个，每个播放信息携带的参数都可以不相同。
            List<PlayContentInfo> playInfoList=new ArrayList<>();
            PlayContentInfo p=new PlayContentInfo();
            p.setCollectInd(1);
            //p.setNotifyVoice("");
            p.setCollectContentTriggerReplaying("9");
            p.setReplayAfterCollection(true);
            p.setTemplateId("9dba200d2a5a4e80a4c86430f8146fdb");
            //模板采用UTF-8编码格式，汉字和中文符号为3个字节，字母、数字和英文符号为1个字节。
            String[] pa=new String[6];
            //地址
            String[] ve = subString(venuesAddres);
            pa[0]=ve[0];
            pa[1]=ve[1];
            pa[2]=ve[2];
            //场所
            String[] vn = subString(venuesName);
            pa[3]=vn[0];
            pa[4]=vn[1];
            //事件
            pa[5]=event;
            p.setTemplateParas(pa);
            playInfoList.add(p);

            bodys.put("playInfoList", playInfoList);//必输

            //URL可填写为http://IP:Port或域名，推荐使用域名，支持http和https。且该域名对应多个服务器，避免单点故障无法接收通知
            //bodys.put("statusUrl","http://172.17.28.75:8081/api/huawei/callBack" );//非必输
            /*//URL可填写为http://IP:Port或域名，推荐使用域名，支持http和https。且该域名对应多个服务器，避免单点故障无法接收话单
            bodys.put("feeUrl","1" );//非必输
            //指示是否需要返回平台的空闲端口数量。默认 false
            bodys.put("returnIdlePort", false);//非必输
            //用户附属信息，此标识由第三方服务器定义，会在后续的通知消息中携带此信息。
            bodys.put("userData", "1");//非必输*/

            HttpParamers params=new HttpParamers(HttpMethod.POST);
            params.setJsonParamer(bodys);

            /*** 3.调用接口 ***/
            HttpService httpService=new HttpService(HwCallVo.getVoiceUrl());
            String submitResponse = httpService.service(HwCallVo.getVoiceUrl(), params, header);

            /*** 4.解析接口响应 ***/
            if(!GeneTool.isEmpty(submitResponse)){
                CallResponse callResponse = JsonUtils.jsonTOBean(submitResponse, CallResponse.class);
                if(null!=callResponse){
                    String resultcode = callResponse.getResultcode();
                    String resultdesc = callResponse.getResultdesc();
                    if("0".equals(resultcode) && "Success".equals(resultdesc)){
                        sessionId=callResponse.getSessionId();
                        log.info("("+calleeNbr+")电话通知("+new Date()+")已发送。");
                    }else{
                        throw new RuntimeException("("+calleeNbr+")电话通知响应错误:错误码为（"+resultcode+"),错误描述（"+resultdesc+")。");
                    }
                }else{
                    throw new RuntimeException(calleeNbr+":电话通知无响应！");
                }
            }else{
                throw new RuntimeException(calleeNbr+":电话通知无响应！");
            }

        } catch (RuntimeException r) {
            r.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return sessionId;
    }

    /**
     * 字符分割
     * @param contents
     * @return
     */
    public static String[] subString(String contents){
        String result1 ="";
        String result2 ="";
        String result3 ="";
        String result4 ="";
        String result5 ="";

        int length = contents.length();
        if(length<10){
            result1 = contents.substring(0,length);
        }else{
            result1 = contents.substring(0,10);
            if(length<20){
                result2 = contents.substring(10,length);
            }else{
                result2 = contents.substring(10,20);
                if(length<30){
                    result3 = contents.substring(20,length);
                }else{
                    result3 = contents.substring(20,30);
                    if(length<40){
                        result4 = contents.substring(30,length);
                    }else{
                        result4 = contents.substring(30,40);
                        if(length<50){
                            result5 = contents.substring(40,length);
                        }else{
                            throw new  RuntimeException("("+contents+")字符串长度超出范围，长度为："+length);
                        }
                    }
                }
            }
        }

        String[] str=new String[5];
        str[0]=result1;
        str[1]=result2;
        str[2]=result3;
        str[3]=result4;
        str[4]=result5;

        return str;
    }
}
