package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.task.entity.ActInstEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TaskActInstMapper {

    /**新增**/
    void addAct(ActInstEntity actEntity);

    /**更新**/
    void updateAct(ActInstEntity actEntity);
}
