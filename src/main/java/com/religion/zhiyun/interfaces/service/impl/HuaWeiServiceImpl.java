package com.religion.zhiyun.interfaces.service.impl;

import com.religion.zhiyun.event.dao.EventNotifiedMapper;
import com.religion.zhiyun.interfaces.entity.huawei.CallBody;
import com.religion.zhiyun.interfaces.entity.huawei.CallStatusInfo;
import com.religion.zhiyun.interfaces.entity.huawei.FeeInfo;
import com.religion.zhiyun.interfaces.entity.huawei.RepeatedlyReadRequestWrapper;
import com.religion.zhiyun.interfaces.service.HuaWeiService;
import com.religion.zhiyun.utils.JsonUtils;
import com.religion.zhiyun.utils.Tool.GeneTool;
import com.religion.zhiyun.utils.response.AppResponse;
import com.religion.zhiyun.utils.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class HuaWeiServiceImpl implements HuaWeiService {

    @Autowired
    private EventNotifiedMapper eventNotifiedMapper;

    @Override
    public AppResponse handleCallStatus(HttpServletRequest request) {
        long codes= ResultCode.ERROR.code();
        String message="语音通知呼叫状态通知接收失败！！";

        try {
            RepeatedlyReadRequestWrapper status=new RepeatedlyReadRequestWrapper(request);
            String requestBody = status.getBody();
            log.info("CallStatusBody:"+requestBody);
            if(!GeneTool.isEmpty(requestBody)){
                CallBody callBody = JsonUtils.jsonTOBean(requestBody, CallBody.class);
                //callout：呼出事件● alerting：振铃事件 answer：应答事件 collectInfo：放音收号结果事件 disconnect：挂机事件
                String eventType = callBody.getEventType();
                String statusInfo = callBody.getStatusInfo();
                if(!GeneTool.isEmpty(statusInfo)){
                    CallStatusInfo callStatusInfo = JsonUtils.jsonTOBean(statusInfo, CallStatusInfo.class);
                    if(null!=callStatusInfo){
                        FeeInfo feeInfo =new FeeInfo();
                        feeInfo.setSessionId(callStatusInfo.getSessionId());
                        feeInfo.setEventType(eventType);
                        feeInfo.setCallDetail(statusInfo);
                        feeInfo.setUlFailReason(callStatusInfo.getStateCode());

                        eventNotifiedMapper.addCall(feeInfo);
                    }
                }
            }

            codes= ResultCode.SUCCESS.code();
            message="语音通知呼叫状态通知接收成功！";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new AppResponse(codes,message);
    }

    @Override
    public AppResponse handleFee(String requestBody) {
        long codes= ResultCode.ERROR.code();
        String message="语音通知话单通知接收失败！！";
        try {
            CallBody callBody = JsonUtils.jsonTOBean(requestBody, CallBody.class);
            String eventType = callBody.getEventType();//fee：话单事件
            String feeLst = callBody.getFeeLst();
            if(!GeneTool.isEmpty(feeLst)){
                List<FeeInfo> feeList = JsonUtils.jsonTOList(feeLst, FeeInfo.class);
                if(null!=feeList && feeList.size()>0){
                    FeeInfo feeInfo = feeList.get(0);
                    if(null!=feeInfo){
                        feeInfo.setCallDetail(feeLst);
                        feeInfo.setEventType(eventType);
                        int uc = eventNotifiedMapper.updateCall(feeInfo);
                        if(uc<=0){
                            throw new RuntimeException(feeInfo.getIcid()+"：呼叫话单事件更新失败！");
                        }
                    }
                }else{
                    throw new RuntimeException("呼叫话单事件的信息为空！");
                }
            }else{
                throw new RuntimeException("呼叫话单事件的信息为空！");
            }
            codes= ResultCode.SUCCESS.code();
            message="语音通知话单通知接收成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AppResponse(codes,message);
    }
}
