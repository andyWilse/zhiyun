package com.religion.zhiyun.staff.service.impl;

import com.religion.zhiyun.staff.dao.RmStaffInfoMapper;
import com.religion.zhiyun.staff.entity.StaffEntity;
import com.religion.zhiyun.staff.service.RmStaffInfoService;
import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RmStaffInfoServiceimpl implements RmStaffInfoService {
    @Autowired
    private RmStaffInfoMapper staffInfoMapper;

    @Override
    public void add(StaffEntity staffEntity) {
        staffInfoMapper.add(staffEntity);
    }

    @Override
    public List<StaffEntity> all() {
        return staffInfoMapper.all();
    }

    @Override
    public void update(StaffEntity staffEntity) {
        staffInfoMapper.update(staffEntity);
    }

    @Override
    public void delete(String staffId) {
        staffInfoMapper.delete(staffId);
    }

    @Override
    public RespPageBean getStaffByPage(Integer page, Integer size, String staffName, String staffPost, String religiousSect) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<VenuesEntity> dataList=staffInfoMapper.getStaffByPage(page,size,staffName,staffPost,religiousSect);
        Object[] objects = dataList.toArray();
        /*VenuesEntity[] date = new VenuesEntity[dataList.size()];
        VenuesEntity[] datas = dataList.toArray(date);*/
        Long total=staffInfoMapper.getTotal();
        RespPageBean respPageBean = new RespPageBean();
        respPageBean.setDatas(objects);
        respPageBean.setTotal(total);
        return respPageBean;
    }

    @Override
    public Long getMaxStaffCd() {
        return staffInfoMapper.getMaxStaffCd();
    }
}
