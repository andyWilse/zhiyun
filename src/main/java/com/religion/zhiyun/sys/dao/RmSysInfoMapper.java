package com.religion.zhiyun.sys.dao;

import com.religion.zhiyun.sys.entity.SysEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmSysInfoMapper {
    List<SysEntity> allSys();
    void addSys(SysEntity sysEntity);
    void updateSys(SysEntity sysEntity);
    void deleteSys(String sysId);

}
