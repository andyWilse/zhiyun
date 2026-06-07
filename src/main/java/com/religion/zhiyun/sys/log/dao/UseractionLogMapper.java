package com.religion.zhiyun.sys.log.dao;

import com.religion.zhiyun.sys.log.entity.UseractionLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UseractionLogMapper {
    /**
     * 新增
     * @param useractionLogEntity
     * @return
     */
    int add(UseractionLogEntity useractionLogEntity);
}
