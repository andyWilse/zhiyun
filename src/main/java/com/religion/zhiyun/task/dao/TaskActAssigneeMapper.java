package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.task.entity.AssEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TaskActAssigneeMapper {

    /**新增**/
    void addAssignee(AssEntity assEntity);
}
