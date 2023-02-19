package com.religion.zhiyun.group.service.impl;

import com.religion.zhiyun.group.dao.RmGroupInfoMapper;
import com.religion.zhiyun.group.entity.GroupEntity;
import com.religion.zhiyun.group.service.RmGroupInfoService;
import com.religion.zhiyun.utils.response.RespPageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RmGroupInfoServiceImpl implements RmGroupInfoService {
    @Autowired
    private RmGroupInfoMapper rmGroupInfoMapper;

    @Override
    public RespPageBean getPage(Integer page, Integer size, String groupName) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<GroupEntity> dataList=rmGroupInfoMapper.getGroupByPage(page,size,groupName);
        Object[] objects = dataList.toArray();
        Long total=rmGroupInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }
}
