package com.religion.zhiyun.monitor.service.impl;

import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.sys.login.api.ResultCode;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RmMonitroInfoServiceimpl implements RmMonitroInfoService {
    @Autowired
    private RmMonitroInfoMapper rmMonitroInfoMapper;

    @Override
    public List<MonitroEntity> allMonitro() {
        return rmMonitroInfoMapper.allMonitro();
    }

    @Override
    public void addMonitro(MonitroEntity monitroEntity) {
        rmMonitroInfoMapper.addMonitro(monitroEntity);
    }

    @Override
    public void updateMonitro(MonitroEntity monitroEntity) {
        rmMonitroInfoMapper.updateMonitro(monitroEntity);
    }

    @Override
    public void deleteMonitro(String monitroId) {
        rmMonitroInfoMapper.deleteMonitro(monitroId);
    }

    @Override
    public List<Map<String, Object>> getAllNum() {
        return rmMonitroInfoMapper.getAllNum();
    }

    @Override
    public List<MonitroEntity> getMonitorByState(String state) {
        return rmMonitroInfoMapper.getMonitorByState(state);
    }

    @Override
    public List<MonitroEntity> getMonitorByVenuesId(String state) {
        return rmMonitroInfoMapper.getMonitorByVenuesId(state);
    }

    @Override
    public RespPageBean getMonitrosByPage(Integer page, Integer size, String accessNumber) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<MonitroEntity> dataList=rmMonitroInfoMapper.getMonitrosByPage(page,size,accessNumber);
        Object[] objects = dataList.toArray();
        Long total=rmMonitroInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }

    @Override
    public String getMonitorURLByAccessNum(String accessNum) {
        return rmMonitroInfoMapper.getMonitorURLByAccessNum(accessNum);
    }

    @Override
    public RespPageBean getVenuesMonitor(Integer page, Integer size,String venuesName,String accessNumber) {
        long code= ResultCode.FAILED.getCode();
        String message="监控场所查询失败";
        List<Map<String, Object>> map=null;
        Long total =0l;
        try {
            if(page!=null&&size!=null){
                page=(page-1)*size;
            }
            map = rmMonitroInfoMapper.getVenuesMonitor(page,size,venuesName,accessNumber);
            total = rmMonitroInfoMapper.getVenuesMonitorTotal(venuesName, accessNumber);
            code= ResultCode.SUCCESS.getCode();
            message="监控场所查询成功";
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            message="监控场所查询失败";
            e.printStackTrace();
        }
        return new RespPageBean(code,message,total,map);
    }

    @Override
    public RespPageBean getMonitors(String venuesName, String accessNumber, String state) {
        long code= ResultCode.SUCCESS.getCode();
        List<Map<String, Object>> map=null;
        try {
            map = rmMonitroInfoMapper.getMonitors(venuesName,accessNumber,state);
        } catch (Exception e) {
            code=ResultCode.FAILED.getCode();
            e.printStackTrace();
        }

        return new RespPageBean(code,map);
    }
}
