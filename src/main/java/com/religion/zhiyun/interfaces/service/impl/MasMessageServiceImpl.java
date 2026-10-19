package com.religion.zhiyun.interfaces.service.impl;

import com.google.gson.*;
import com.religion.zhiyun.interfaces.entity.mas.MasEntity;
import com.religion.zhiyun.interfaces.entity.mas.MasErrEntity;
import com.religion.zhiyun.interfaces.entity.mas.MasResponse;
import com.religion.zhiyun.interfaces.service.MasMessageService;
import com.religion.zhiyun.monitor.dao.MonitorBaseMapper;
import com.religion.zhiyun.record.service.OperateRecordService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.Tool.TimeTool;
import com.religion.zhiyun.utils.enums.OperaEnums;
import com.religion.zhiyun.utils.sms.http.HttpHeader;
import com.religion.zhiyun.utils.sms.http.HttpParams;
import com.religion.zhiyun.utils.sms.http.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class MasMessageServiceImpl implements MasMessageService{

    @Value("${mas.send.url}")
    private String masUrl;
    @Value("${mas.send.user.name}")
    private String masUserName;
    @Value("${mas.send.user.password}")
    private String masPassword;

    @Autowired
    private MonitorBaseMapper monitorBaseMapper;

    private static String CONTENT_TYPE="application/json;charset=utf-8";


  /*  报错InvalidUsrOrPwd：
    1.检查apId、secretKey等参数的正确性（登录云mas平台核实接口配置是否一致），特别是secretKey接口密码。
    2.检查MAC参数拼接顺序是否正确，拼接中不能有空格，加密取值32位小写。MD5加密参考地址：https://md5jiami.bmcx.com/
    3.编码格式是否为UTF-8，把经过BASE64编码后的数据解码，如有乱码说明编码格式不是UTF-8。
    4.HTTPS需要忽略ssl认证，不校验。
    以上检查都无误，请修改接口密码重试，接口密码不支持 “/”和“&”“+”，以及中文符号和特殊符号。*/
    @Override
    public String getMasMessage(){
            /*** 1.HttpHeader参数封装 ***/
            HttpHeader header=new HttpHeader();
            //固定填写为application/json;charset=UTF-8。
            header.addParam("Content-Type",CONTENT_TYPE);
            //application/json
            header.addParam("Accept","application/json");

            //body参数封装
            Map<String, Object> body = new HashMap();
            //企业名称
            body.put("ecName","浙江智云科技文化有限公司");
            //接口账号用户名。
            body.put("apId",masUserName);
            //接口账号密码
            body.put("secretKey",masPassword);

            HttpParams params=new HttpParams(HttpMethod.POST);
            params.setBase64JsonParam(body);

            /*** 3.调用接口 ***/
            HttpService httpService=new HttpService(masUrl);
            //String response = httpService.service(masUrl, params, header);

        String response="[{\n" +
                "        \"addSerial\": \"1065082212070515\",\n" +
                "        \"mobile\": \"13566299809\",\n" +
                "        \"sendTime\": \"20261015161819\",\n" +
                "        \"smsContent\": \"抓拍时间：2026-10-15 16:18:15:000；抓拍设备编码：33030497631320000132。\"\n" +
                "    }, {\n" +
                "        \"addSerial\": \"1065082212070515\",\n" +
                "        \"mobile\": \"13566299809\",\n" +
                "        \"sendTime\": \"20261015163121\",\n" +
                "        \"smsContent\": \"抓拍时间：2026-10-1516:31:17:000；抓拍设备编码：33030497631320000170。\"\n" +
                "    }, {\n" +
                "        \"addSerial\": \"1065082212070515\",\n" +
                "        \"mobile\": \"13566299809\",\n" +
                "        \"sendTime\": \"20261015192015\",\n" +
                "        \"smsContent\": \"抓拍时间：2026-10-15 19:20:10:000；抓拍设备编码：33030497631320000045。\"\n" +
                "    }\n" +
                "]";

                /*List<MasResponse> masResponseList=new ArrayList<>();
                JsonElement jsonResponse = JsonParser.parseString(response);
                if (jsonResponse.isJsonObject()) {
                    //JsonObject jsonObject = jsonResponse.getAsJsonObject();
                    // 处理对象逻辑
                    MasErrEntity masErrEntity = JsonUtils.jsonTOBean(response, MasErrEntity.class);
                    throw new RuntimeException("MAS访问错误!");
                } else if (jsonResponse.isJsonArray()) {
                    //JsonArray jsonArray = jsonResponse.getAsJsonArray();
                    // 处理数组逻辑
                } else if (jsonResponse.isJsonPrimitive()) {
                    //JsonPrimitive asJsonPrimitive = jsonResponse.getAsJsonPrimitive();
                    // 处理基本数据类型逻辑
                }else{
                    throw new RuntimeException("MAS短信接收发生未知错误!");
                }*/


            return response;
    }

    @Override
    public void addMasMessage(MasResponse masResponse){
        MasEntity mas= new MasEntity();
        mas.setMasAcctTime(TimeTool.getTimestamp());
        String masState = "0";
        String masRemark = "";
        if (null == masResponse) {
            masRemark="MAS短信信息读取失败！";
        }else{
            String smsContent = masResponse.getSmsContent();
            if(GeneTool.isEmpty(smsContent)){
                masRemark = "MAS短息信息丢失！";
            }else{
                String[] split = smsContent.split("；");
                if(null!=split && split.length==2){
                    if(GeneTool.isEmpty(split[0].substring(5, 24))){
                        masRemark = "MAS抓拍时间丢失！";
                    }else{
                        mas.setMasMonitorTime(TimeTool.toTimestamp(split[0].substring(5, 24)));
                        String[] split1 = split[1].split("：");
                        if(null!=split1 && split1.length==2){
                            String[] split2 = split1[1].split("。");
                            if(null!=split2){
                                if(GeneTool.isEmpty(split2[0])){
                                    masRemark = "MAS抓拍设备编码丢失！";
                                }else{
                                    masState = "1";
                                    mas.setMasDeviceId(split2[0]);
                                }
                            }else{
                                masRemark = "MAS抓拍设备编码丢失。";
                            }
                        }else{
                            masRemark = "MAS:抓拍设备编码丢失！";
                        }
                    }

                }else{
                    masRemark = "MAS短信信息部分丢失";
                }
            }

        }
        //保存数据
        mas.setMasState(masState);
        mas.setMasRemark(masRemark);
        monitorBaseMapper.addMas(mas);
    }


}
