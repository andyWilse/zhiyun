package com.religion.zhiyun.venues.services.impl;

import com.religion.zhiyun.venues.dao.RmVenuesInfoMapper;
import com.religion.zhiyun.venues.entity.VenuesEntity;
import com.religion.zhiyun.venues.services.RmVenuesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void delete(String venuesId) {
        rmVenuesInfoMapper.delete(venuesId);
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
}
