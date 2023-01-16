package com.religion.zhiyun.sys.dict.dao;

import com.religion.zhiyun.sys.dict.entity.SysEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RmSysInfoMapper {
    List<SysEntity> getSysDicts(@Param("dictTypeCd")String dictTypeCd);
    void addSys(SysEntity sysEntity);
    void updateSys(SysEntity sysEntity);
    void deleteSys(String sysId);

}
