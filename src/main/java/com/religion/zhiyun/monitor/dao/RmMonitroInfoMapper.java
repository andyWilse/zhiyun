package com.religion.zhiyun.monitor.dao;

import com.religion.zhiyun.monitor.entity.MonitroEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmMonitroInfoMapper {
    List<MonitroEntity> allMonitro();

    void addMonitro(MonitroEntity monitroEntity);

    void updateMonitro(MonitroEntity monitroEntity);

    void deleteMonitro(String monitroId);

}
