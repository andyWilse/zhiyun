package com.religion.zhiyun.monitor.service.impl;

import com.religion.zhiyun.monitor.dao.RmMonitroInfoMapper;
import com.religion.zhiyun.monitor.entity.MonitroEntity;
import com.religion.zhiyun.monitor.service.RmMonitroInfoService;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;
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
}
