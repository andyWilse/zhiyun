package com.religion.zhiyun.userLogs.service.impl;

import com.religion.zhiyun.userLogs.dao.RmUserLogsInfoMapper;
import com.religion.zhiyun.userLogs.entity.LogsEntity;
import com.religion.zhiyun.userLogs.service.RmUserLogsInfoService;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RmUserLogsInfoServiceimpl implements RmUserLogsInfoService {
    @Autowired
    private RmUserLogsInfoMapper rmUserLogsInfoMapper;

    @Override
    public List<LogsEntity> alllogs() {
        return rmUserLogsInfoMapper.alllogs();
    }

    @Override
    public void addlogs(LogsEntity logsEntity) {
        rmUserLogsInfoMapper.addlogs(logsEntity);
    }

    @Override
    public void updatelogs(LogsEntity logsEntity) {
        rmUserLogsInfoMapper.updatelogs(logsEntity);
    }

    @Override
    public void deletelogs(String logsId) {
        rmUserLogsInfoMapper.deletelogs(logsId);
    }

    @Override
    public RespPageBean getLogsByPage(Integer page, Integer size, String userName) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<VenuesEntity> dataList=rmUserLogsInfoMapper.getLogsByPage(page,size,userName);
        Object[] objects = dataList.toArray();
        Long total=rmUserLogsInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }
}
