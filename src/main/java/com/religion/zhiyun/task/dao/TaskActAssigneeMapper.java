package com.religion.zhiyun.task.dao;

import com.religion.zhiyun.task.entity.AssEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TaskActAssigneeMapper {

    /**新增**/
    void addAssignee(AssEntity assEntity);

    /**修改**/
    void updateAssignee(AssEntity assEntity);

    /**获取接收人**/
    List<AssEntity> getAssignee(@Param("actId") Integer actId,@Param("assId") Integer assId);
}
