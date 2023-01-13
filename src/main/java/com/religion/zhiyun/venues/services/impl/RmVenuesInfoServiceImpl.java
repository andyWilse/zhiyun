package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.utils.RespPageBean;
import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RmVenuesInfoServiceImpl implements RmVenuesInfoService {
    @Autowired
    private RmVenuesInfoMapper rmVenuesInfoMapper;

    @Override
    public void add(VenuesEntity venuesEntity) {
        rmVenuesInfoMapper.add(venuesEntity);
    }

    @Override
    public void update(VenuesEntity venuesEntity) {
        rmVenuesInfoMapper.update(venuesEntity);
    }

    @Override
    public int delete(int venuesId) {
        return rmVenuesInfoMapper.delete(venuesId);
    }

    @Override
    public VenuesEntity getByResponsiblePerson(String responsiblePerson) {
        return rmVenuesInfoMapper.getByResponsiblePerson(responsiblePerson
        );
    }

    @Override
    public List<VenuesEntity> queryAll() {
        return rmVenuesInfoMapper.queryAll();
    }

    @Override
    public List<VenuesEntity> querySectAll(String religiousSect) {
        return rmVenuesInfoMapper.querySectAll(religiousSect);
    }

    @Override
    public List<VenuesEntity> getByVenuesFaculty(String venuesName, String responsiblePerson) {
        return rmVenuesInfoMapper.getByVenuesFaculty(venuesName,responsiblePerson);
    }

    @Override
    public VenuesEntity getVenueByID(String venuesId) {
        return rmVenuesInfoMapper.getVenueByID(venuesId);
    }

    @Override
    public List<Map<String, Object>> getAllNum() {
        return rmVenuesInfoMapper.getAllNum();
    }

    @Override
    public RespPageBean getEmpByPage(Integer page, Integer size, String venuesName, String responsiblePerson, String religiousSect) {
        if(page!=null&&size!=null){
            page=(page-1)*size;
        }
        List<VenuesEntity> dataList=rmVenuesInfoMapper.getEmpByPage(page,size,venuesName,responsiblePerson,religiousSect);
        Object[] objects = dataList.toArray();
        /*VenuesEntity[] date = new VenuesEntity[dataList.size()];
        VenuesEntity[] datas = dataList.toArray(date);*/
        Long total=rmVenuesInfoMapper.getTotal();
        RespPageBean bean = new RespPageBean();
        bean.setDatas(objects);
        bean.setTotal(total);
        return bean;
    }
}
